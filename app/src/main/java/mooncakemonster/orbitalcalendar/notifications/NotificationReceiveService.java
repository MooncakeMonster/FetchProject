package mooncakemonster.orbitalcalendar.notifications;


import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.cloudant.User;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.voteresult.ResultDatabase;
import mooncakemonster.orbitalcalendar.voteresult.ResultItem;
import mooncakemonster.orbitalcalendar.votesend.VotingDatabase;

/**
 * Created by MunKeat_2 on 12/7/2015.
 */
public class NotificationReceiveService extends Service {

    private static String LOCK_NAME_STATIC = "mooncakemonster.orbitalcalendar.Notification.NotificationReceiveService.STATIC";

    private PowerManager.WakeLock mWakeLock;

    // Connect to cloudant
    UserDatabase db;
    CloudantConnect cloudantConnect;
    VotingDatabase votingDatabase;
    ResultDatabase resultDatabase;
    NotificationDatabase notificationDatabase;

    // Custom notification items
    private ImageView notification_image;
    private TextView notification_title, notification_text;

    private static final String TAG = NotificationReceiveService.class.getSimpleName();

    private static final String VOTING_REQUEST_FOUND = "Voting Request Found!";
    private static final String VOTING_RESPONSE_ACCEPTED = "Event Voted!";
    private static final String VOTING_RESPONSE_REJECTED = "Event Rejected!";
    private static final String VOTING_CONFIRMATION = "Event Confirmation";
    private static final String VOTING_REMINDER = "Voting Reminder";

    /**
     * Simply return null, since our Service will not be communicating with
     * any other components. It just does its work silently.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * This is where we initialize. We call this when onStart/onStartCommand is
     * called by the system. We won't do anything with the intent here, and you
     * probably won't, either.
     */
    private void handleIntent(Intent intent) {
        // obtain the wake lock
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
        mWakeLock.acquire();

        // check the global background data setting
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (!cm.getBackgroundDataSetting()) {
            stopSelf();
            return;
        }

        // do the actual work, in a separate thread
        new PollTask().execute();
    }

    private class PollTask extends AsyncTask<Void, Void, Void> {
        /**
         * This is where YOU do YOUR work. There's nothing for me to write here
         * you have to fill this in. Make your HTTP request(s) or whatever it is
         * you have to do to get your updates in here, because this is run in a
         * separate thread
         */
        @Override
        protected Void doInBackground(Void... params) {
            while (true) {
                onReceiveUpdate();
                try {
                    //Thread.sleep(1000l * 60);
                } catch (Exception e) {

                }
            }

            //return null;
        }

        /**
         * In here you should interpret whatever you fetched in doInBackground
         * and push any notifications you need to the status bar, using the
         * NotificationManager. I will not cover this here, go check the docs on
         * NotificationManager.
         * <p/>
         * What you HAVE to do is call stopSelf() after you've pushed your
         * notification(s). This will:
         * 1) Kill the service so it doesn't waste precious resources
         * 2) Call onDestroy() which will release the wake lock, so the device
         * can go to sleep again and save precious battery.
         */
        @Override
        protected void onPostExecute(Void result) {
            // handle your data
            stopSelf();
        }
    }

    private void onReceiveUpdate() {
        if (cloudantConnect == null)
            this.cloudantConnect = new CloudantConnect(this, "user");

        // Retrieve own username
        db = new UserDatabase(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();

        notificationDatabase = new NotificationDatabase(this);
        votingDatabase = new VotingDatabase(this);
        resultDatabase = new ResultDatabase(this);

        // (1) Retrieve latest voting request
        String my_username = user.get("username");
        cloudantConnect.startPullReplication();
        User my_user = cloudantConnect.getTargetUser(my_username);

        /*
         * case 1: Target participants receive voting request
         * case 2,3: Sender received voting response from target participants (either voted, or rejected voting)
         * case 4: Target participants received confirmed date and time of an event
         * case 5: Target participants gets reminder to vote for an event
         * case 6: Target participants gets attendance
         */

        if(my_user.getOption_event_title() != null) {

            Log.d(TAG, "Voting request found");
            setNotification(my_user.getOption_event_colour(), VOTING_REQUEST_FOUND, my_user.getOption_my_username() + " has requested you to vote for the event \"" + my_user.getOption_event_title() + "\". Vote now!");

            notificationDatabase.putInformation(notificationDatabase, 1, my_user.getOption_event_id(), my_user.getOption_event_colour(),
                    my_user.getOption_my_username(), " has requested you to vote for the event \"",
                    my_user.getOption_event_title(), "\". Vote now!", my_user.getOption_event_location(),
                    my_user.getOption_event_notes(), my_user.getOption_start_date(), my_user.getOption_end_date(),
                    my_user.getOption_start_time(), my_user.getOption_end_time(), null);

            cloudantConnect.resetVotingRequest(my_user);
            cloudantConnect.startPushReplication();

        } if(my_user.getSelected_event_title() != null) {

            Log.d(TAG, "Voting response found");
            int notification_id;
            String notification_action, action, action1, action2, reject_reason = "", start_date = null, end_date = null, start_time = null, end_time = null;

            // case 1: Target participant has chosen the dates they can make it
            // case 2: Target participant has rejected the event
            if(my_user.getReject_reason() == null) {
                notification_id = 2;

                notification_action = " has responded to your event \"";
                setNotification(my_user.getSelected_event_colour(), VOTING_RESPONSE_ACCEPTED, my_user.getSelected_my_username() + notification_action + my_user.getSelected_event_title() + "\". Checkout the current voting result now!");

                start_date = my_user.getSelected_start_date();
                end_date = my_user.getSelected_end_date();
                start_time =  my_user.getSelected_start_time();
                end_time = my_user.getSelected_end_time();

                action = "accept";
                action1 = " has responded to your event \"";
                action2 = "\". Checkout the current voting result now!";
            } else {
                notification_id = 3;

                notification_action = " has rejected your event \"";
                setNotification(my_user.getSelected_event_colour(), VOTING_RESPONSE_REJECTED, my_user.getSelected_my_username() + notification_action + my_user.getSelected_event_title()  + "\". Find out why!");

                reject_reason = my_user.getReject_reason();

                action = "reject";
                action1 = " has rejected voting for your event \"";
                action2 = "\". Find out why!";
            }

            String event_id = "" + my_user.getSelected_event_id();
            String voted_participant = my_user.getSelected_my_username();

            notificationDatabase.putInformation(notificationDatabase, notification_id, Integer.parseInt(event_id), my_user.getSelected_event_colour(),
                    voted_participant, action1, my_user.getSelected_event_title(), action2, my_user.getSelected_event_location(),
                    my_user.getSelected_event_notes(), start_date, end_date, start_time, end_time, reject_reason);

            // (1) Update the voted participants for that event to indicate the number of voted participants in VotingFragment
            votingDatabase.updateInformation(votingDatabase, event_id, voted_participant, null, null, null, null, null);

            if(action.equals("accept")) {
                Log.d(TAG, "accept");
                // (2) Update the voted participant's selected dates
                resultDatabase.storeParticipants(resultDatabase, new ResultItem(event_id,
                        start_date, end_date, start_time, end_time, null, voted_participant, null, null, null));
                // (3) Update the voted participant's not selected dates
                resultDatabase.storeParticipants(resultDatabase, new ResultItem(event_id,
                        my_user.getNot_selected_start_date(), my_user.getNot_selected_end_date(), my_user.getNot_selected_start_time(),
                        my_user.getNot_selected_end_time(), null, null, voted_participant, null, null));
            } else if(action.equals("reject")) {
                Log.d(TAG, "reject");
                // (4) Add in the reject participants into database
                resultDatabase.storeParticipants(resultDatabase, new ResultItem(event_id,
                        null, null, null, null, null, null, null, voted_participant, null));
            }

            cloudantConnect.resetVotingResponse(my_user);
            cloudantConnect.startPushReplication();

        } if(my_user.getConfirm_event_title() != null) {

            Log.d(TAG, "Voting confirmation found");
            setNotification(my_user.getConfirm_event_colour(), VOTING_CONFIRMATION, my_user.getConfirm_my_username() + " has confirmed the date and time of the event \"" + my_user.getConfirm_event_title()  + "\"!");

            String start_date = my_user.getConfirm_start_date();
            String end_date = my_user.getConfirm_end_date();
            String start_time = my_user.getConfirm_start_time();
            String end_time = my_user.getConfirm_end_time();

            notificationDatabase.putInformation(notificationDatabase, 4, my_user.getConfirm_event_id(), my_user.getConfirm_event_colour(),
                    my_user.getConfirm_my_username(), " has confirmed the event \"", my_user.getConfirm_event_title(),
                    "\" to be from " + start_date + " to " + end_date + ", " + start_time + " - " + end_time, null, null,
                    start_date, end_date, start_time, end_time, null);

            cloudantConnect.resetVotingConfirmation(my_user);
            cloudantConnect.startPushReplication();

        } if(my_user.getReminder_event_title() != null) {

            Log.d(TAG, "Voting reminder found");
            setNotification(my_user.getReminder_event_colour(), VOTING_REMINDER, my_user.getReminder_my_username() + " reminds you to vote for the event \"" + my_user.getReminder_event_title()  + "\"!");

                    notificationDatabase.putInformation(notificationDatabase, 5, my_user.getReminder_event_id(), my_user.getReminder_event_colour(),
                            my_user.getReminder_my_username(), " reminds you to vote for the event \"", my_user.getReminder_event_title(),
                            "\". Vote now!", null, null, null, null, null, null, null);

            cloudantConnect.resetVotingReminder(my_user);
            cloudantConnect.startPushReplication();
        } if(my_user.getAttendance_event_title() != null) {

            Log.d(TAG, "Voting attendance found");
            setNotification(my_user.getAttendance_event_colour(), VOTING_REMINDER, my_user.getAttendance_my_username() + " will be coming to your event \"" + my_user.getAttendance_event_title()  + "\"!");

            notificationDatabase.putInformation(notificationDatabase, 6, my_user.getAttendance_event_id(), my_user.getAttendance_event_colour(),
                    my_user.getAttendance_my_username(), " will be coming to your event \"", my_user.getAttendance_event_title(),
                    "\"!", null, null, null, null, null, null, null);

            votingDatabase.updateInformation(votingDatabase, "" + my_user.getAttendance_event_id(), null, my_user.getAttendance_my_username(), null, null, null, null);

            cloudantConnect.resetVotingAttendance(my_user);
            cloudantConnect.startPushReplication();
        }
    }

    //Helper Method for Setting Notification
    public void setNotification(int event_colour, String titleOfNotification, String contentOfNotification) {

        Log.i(TAG, "Intent received!");
        int notification_id = 1;

        // TODO: Link user to notification fragment

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), Constant.getBearColour(event_colour));
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Fetch: " + titleOfNotification)
                .setContentText(contentOfNotification)
                .setLights(Color.CYAN, 1000, 1000)
                .setLargeIcon(largeIcon)
                .setSmallIcon(Constant.getBearColour(event_colour))
                .setVibrate(new long[]{1000, 1000})
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setNumber(notification_id++);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notification_id, builder.build());
    }

    /**
     * This is deprecated, but you have to implement it if you're planning on
     * supporting devices with an API level lower than 5 (Android 2.0).
     */
    @Override
    public void onStart(Intent intent, int startId) {
        handleIntent(intent);
    }

    /**
     * This is called on 2.0+ (API level 5 or higher). Returning
     * START_NOT_STICKY tells the system to not restart the service if it is
     * killed because of poor resource (memory/cpu) conditions.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    /**
     * In onDestroy() we release our wake lock. This ensures that whenever the
     * Service stops (killed for resources, stopSelf() called, etc.), the wake
     * lock will be released.
     */
    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
    }
}
