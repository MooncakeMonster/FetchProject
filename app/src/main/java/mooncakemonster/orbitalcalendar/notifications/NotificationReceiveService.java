package mooncakemonster.orbitalcalendar.notifications;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.HashMap;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.cloudant.User;

/**
 * Created by MunKeat_2 on 12/7/2015.
 */
public class NotificationReceiveService extends Service {

    private static String LOCK_NAME_STATIC = "mooncakemonster.orbitalcalendar.Notification.NotificationReceiveService.STATIC";

    private PowerManager.WakeLock mWakeLock;

    // Connect to cloudant
    UserDatabase db;
    CloudantConnect cloudantConnect;

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
        db = new UserDatabase(this.getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();

        // (1) Retrieve latest voting request
        String my_username = user.get("username");//
        Log.d(TAG, my_username);

        // Pull data from Cloudant and store in phone in real time
        cloudantConnect.startPullReplication();
        User my_user = cloudantConnect.getTargetUser(my_username);

        if (my_user.getOption_event_title() != null) {
            Log.d(TAG, VOTING_REQUEST_FOUND);
            setNotification(this, VOTING_REQUEST_FOUND, my_user.getOption_my_username() + " has requested you to vote for the event - " + my_user.getOption_event_title());

        } if (my_user.getSelected_event_title() != null) {
            Log.d(TAG, "Voting response found");

            String action;

            // case 1: Target participant has chosen the dates they can make it
            // case 2: Target participant has rejected the event
            if (my_user.getReject_reason() != null) {
                action = " has responded to your event - ";
                setNotification(this, VOTING_RESPONSE_ACCEPTED, my_user.getOption_my_username() + action + my_user.getOption_event_title());

            } else {
                action = " has rejected your event - ";
                setNotification(this, VOTING_RESPONSE_REJECTED, my_user.getOption_my_username() + action + my_user.getOption_event_title());
            }

        } if (my_user.getConfirm_event_title() != null) {
            setNotification(this, VOTING_CONFIRMATION, my_user.getOption_my_username() + " has confirmed the date and time of the event - " + my_user.getOption_event_title());

        } if (my_user.getReminder_event_title() != null) {
            setNotification(this, VOTING_REMINDER, my_user.getOption_my_username() + " reminds you to vote for the event - " + my_user.getOption_event_title());
        }
    }

    //Helper Method for Setting Notification
    public void setNotification(Context context, String titleOfNotification, String contentOfNotification) {

        Log.i(TAG, "Intent received!");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Set notification design: Set the icon, scrolling text and timestamp
        Notification notification = new Notification.Builder(context)
                .setContentTitle("Fetch: " + titleOfNotification)
                .setContentText(contentOfNotification)
                .setSmallIcon(R.drawable.bearicon)
                .setVibrate(new long[]{1000, 1000})
                .setAutoCancel(true).getNotification();

        notificationManager.notify(0, notification);
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
