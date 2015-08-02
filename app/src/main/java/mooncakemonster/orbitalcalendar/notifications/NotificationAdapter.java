package mooncakemonster.orbitalcalendar.notifications;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.database.AppointmentController;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.friendlist.FriendDatabase;
import mooncakemonster.orbitalcalendar.profilepicture.RoundImage;
import mooncakemonster.orbitalcalendar.voteinvitation.VoteInvitation;
import mooncakemonster.orbitalcalendar.voteinvitation.VoteInvitationSent;
import mooncakemonster.orbitalcalendar.votesend.VoteItem;
import mooncakemonster.orbitalcalendar.votesend.VotingDatabase;

/**
 * Created by BAOJUN on 7/7/15.
 */
public class NotificationAdapter extends ArrayAdapter<NotificationItem> {

    private static final String TAG = NotificationAdapter.class.getSimpleName();
    private List<NotificationItem> objects;
    private CloudantConnect cloudantConnect;
    private ProgressDialog progressDialog;
    private VotingDatabase votingDatabase;
    private NotificationDatabase notificationDatabase;
    private FriendDatabase friendDatabase;
    private UserDatabase db;
    private Intent intent;

    public NotificationAdapter(Context context, int resource, List<NotificationItem> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    static class Holder {
        LinearLayout layout;
        SimpleDraweeView action_image;
        TextView message;
        TextView timestamp;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater;
        Holder holder;

        inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Fresco.initialize(getContext());
        row = inflater.inflate(R.layout.row_notifications, parent, false);

        final NotificationItem notificationItem = objects.get(position);

        if (notificationItem != null) {
            holder = new Holder();

            final String sender_username = notificationItem.getSender_username();
            String sender_message = notificationItem.getMessage();
            final String sender_event = notificationItem.getSender_event();
            String action = notificationItem.getAction();

            // Create a new spannable
            SpannableString spannable = new SpannableString(sender_username + sender_message + sender_event + action);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, sender_username.length(), 0);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), sender_username.length() + sender_message.length(),
                    sender_username.length() + sender_message.length() + sender_event.length(), 0);

            // Set the text of a textView with the spannable object
            holder.layout = (LinearLayout) row.findViewById(R.id.notification_layout);
            holder.action_image = (SimpleDraweeView) row.findViewById(R.id.action_image);
            holder.timestamp = (TextView) row.findViewById(R.id.timestamp);

            if (cloudantConnect == null)
                cloudantConnect = new CloudantConnect(getContext(), "user");

            // Set darker colour to indicate user has not read the notification
            if (notificationItem.getClicked().equals("false"))
                holder.layout.setBackgroundColor(getContext().getResources().getColor(R.color.sky));

            RoundImage roundImage = new RoundImage(cloudantConnect.retrieveUserImage(notificationItem.getSender_username()));
            holder.action_image.setImageDrawable(roundImage);

            holder.message = (TextView) row.findViewById(R.id.message);
            holder.message.setText(spannable);

            // Get the timestamp
            holder.timestamp.setText(getTimestampText(notificationItem.getTimestamp()));

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setCancelable(false);


            final View inner_view = row;
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*
                     * case 1: Friend request received
                     * case 2: Friend accepted request
                     * case 3: Target participants receive voting request
                     * case 4,5: Sender received voting response from target participants (either voted, or rejected voting)
                     * case 6: Target participants received confirmed date and time of an event
                     * case 7: Target participants gets reminder to vote for an event
                     * case 8: Target participants gets attendance
                     */

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("vote_item", notificationItem);

                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

                    // Once notification is clicked, set as true to indicate user has seen the notification
                    notificationDatabase = new NotificationDatabase(getContext());
                    notificationDatabase.updateInformation(notificationDatabase, notificationItem.getRow_id(), "true", null, null);

                    switch (notificationItem.getNotificationId()) {
                        case 1:

                            if (notificationItem.getAction_done().equals("false")) {
                                dialogBuilder.setTitle("Friend request");
                                dialogBuilder.setMessage("Would you like to accept friend request from \"" + notificationItem.getSender_username() + "\"?");
                                dialogBuilder.setNegativeButton("Cancel", null);
                                dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        notificationDatabase.updateInformation(notificationDatabase, notificationItem.getRow_id(), null, "true", null);

                                        dialog.dismiss();
                                        progressDialog.setMessage("Informing " + notificationItem.getSender_username() + " ...");
                                        showDialog();

                                        Timer timer = new Timer();
                                        timer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {

                                                // Save friend into friendlist database
                                                friendDatabase = new FriendDatabase(getContext());
                                                friendDatabase.putInformation(friendDatabase, "true", Constant.retrieveCurrentTime(), notificationItem.getSender_username());

                                                // Send back to user to inform about friend request acceptance
                                                if (cloudantConnect == null)
                                                    cloudantConnect = new CloudantConnect(getContext(), "user");

                                                // Retrieve own username
                                                db = new UserDatabase(getContext());
                                                HashMap<String, String> user = db.getUserDetails();
                                                String my_username = user.get("username");
                                                cloudantConnect.sendFriendAccepted(my_username, notificationItem.getSender_username());
                                                cloudantConnect.startPushReplication();

                                                //Toast.makeText(getContext(), "Friend request accepted", Toast.LENGTH_SHORT).show();

                                                hideDialog();
                                            }
                                        }, 1500);
                                    }
                                });
                                dialogBuilder.show();
                            } else {
                                Constant.alertUser(getContext(), "Friend request", "You had already accepted friend request from \"" + notificationItem.getSender_username() + "\".");
                            }
                            break;
                        case 2:
                            break;
                        case 3:
                            if (notificationItem.getAction_done().equals("false")) {
                                intent = new Intent(getContext(), VoteInvitation.class);
                            } else {
                                intent = new Intent(getContext(), VoteInvitationSent.class);
                            }
                            intent.putExtras(bundle);
                            inner_view.getContext().startActivity(intent);
                            break;
                        case 4:
                            // TODO: Show voting result fragment or VotingResultActivity if possible
                            break;
                        case 5:
                            Constant.alertUser(getContext(), "Reason for Vote Rejection", notificationItem.getSender_username() +
                                    ": \"" + notificationItem.getReject_reason() + "\"");
                            break;
                        case 6:
                            if (notificationItem.getAction_done().equals("false")) {
                                dialogBuilder.setTitle("Confirm attendance");

                                // Check if user can make it, cannot make it or has rejected voting for this event
                                if (notificationItem.getConfirm_action().equals("can")) {
                                    dialogBuilder.setMessage("You had voted for this option previously.\n\n" + "Would you like to confirm that you can attend the event \""
                                            + notificationItem.getSender_event() + "\"?");
                                } else if (notificationItem.getConfirm_action().equals("cannot")) {
                                    dialogBuilder.setMessage("You did not vote for this option previously, but " + notificationItem.getSender_username() +
                                            " still hopes that you will come.\n\n Would you like to change your mind and confirm that you can attend the event \""
                                            + notificationItem.getSender_event() + "\"?");
                                } else if (notificationItem.getConfirm_action().equals("reject")) {
                                    dialogBuilder.setMessage("You had rejected voting for this event previously, but " + notificationItem.getSender_username() +
                                            " still hopes that you can attend. \n\n Would you like to change your mind and confirm that you can attend the event \""
                                            + notificationItem.getSender_event() + "\"?");
                                }

                                dialogBuilder.setNegativeButton("Cancel", null);
                                dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        notificationDatabase = new NotificationDatabase(getContext());
                                        notificationDatabase.updateInformation(notificationDatabase, notificationItem.getRow_id(), null, "true", null);

                                        dialog.dismiss();
                                        progressDialog.setMessage("Confirm attendance with " + sender_username + "...");
                                        showDialog();

                                        Timer timer = new Timer();
                                        timer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                if (cloudantConnect == null)
                                                    cloudantConnect = new CloudantConnect(getContext(), "user");
                                                // Retrieve own username
                                                db = new UserDatabase(getContext());
                                                HashMap<String, String> user = db.getUserDetails();
                                                String my_username = user.get("username");


                                                AppointmentController appointmentController = new AppointmentController(getContext());
                                                appointmentController.open();

                                                long start = Constant.stringToMillisecond(notificationItem.getStart_date(), notificationItem.getStart_time(),
                                                        new SimpleDateFormat("dd/MM/yyyy"), new SimpleDateFormat("hh:mma"));
                                                long end = Constant.stringToMillisecond(notificationItem.getEnd_date(), notificationItem.getEnd_time(),
                                                        new SimpleDateFormat("dd/MM/yyyy"), new SimpleDateFormat("hh:mma"));
                                                long size = fewDaysAppointment(start, end);

                                                // Few days event
                                                if(size > 1) {
                                                    for (int i = 0; i < size; i++) {
                                                        appointmentController.createAppointment((long) i, start, notificationItem.getSender_event(),
                                                                notificationItem.getStart_date(), start, end, notificationItem.getSender_location(),
                                                                notificationItem.getSender_notes(), -1, notificationItem.getEventId());
                                                    }
                                                } else {
                                                    appointmentController.createAppointment((long) -1, start, notificationItem.getSender_event(),
                                                            notificationItem.getStart_date(), start, end, notificationItem.getSender_location(),
                                                            notificationItem.getSender_notes(), -1, notificationItem.getEventId());
                                                }

                                                cloudantConnect.sendAttendanceToTargetParticipants(my_username, sender_username,
                                                        notificationItem.getEventId(), notificationItem.getImageId(), notificationItem.getSender_event());
                                                cloudantConnect.startPushReplication();

                                                //Toast.makeText(getContext(), "Attendance sent successfully", Toast.LENGTH_SHORT).show();

                                                hideDialog();
                                            }
                                        }, 1500);

                                    }
                                });
                                dialogBuilder.show();
                            } else {
                                Constant.alertUser(getContext(), "Confirm attendance", "You had already confirmed your attendance for the event \"" +
                                        notificationItem.getSender_event() + "\" with " + sender_username + ".");
                            }
                            break;
                        case 7:
                            intent = new Intent(getContext(), VoteInvitation.class);
                            intent.putExtras(bundle);
                            inner_view.getContext().startActivity(intent);
                            break;
                        case 8:
                            votingDatabase = new VotingDatabase(getContext());
                            VoteItem voteItem = votingDatabase.getVoteItem(votingDatabase, "" + notificationItem.getEventId());
                            String attendance = voteItem.getEvent_attendance();
                            if (attendance != null)
                                Constant.openAttendanceDialog(getContext(), attendance.split(" "));
                            break;
                        default:
                            break;
                    }
                }
            });

            row.setTag(holder);
        }

        return row;
    }

    private long fewDaysAppointment(long start_milliseconds, long end_milliseconds) {
        return (end_milliseconds - start_milliseconds) / Constant.DAY_IN_MILLISECOND;
    }

    // This method computes the time received the notification
    private String getTimestampText(long timestamp) {
        long current_second = Constant.retrieveCurrentTime();
        long difference = current_second - timestamp;

        Log.d(TAG, "current " + current_second);
        Log.d(TAG, "timestamp " + timestamp);
        Log.d(TAG, "difference" + difference);

        // (1) Received less than 10 seconds
        if (difference <= Constant.MIN_IN_MILLISECOND / 6) return "A few seconds ago";
            // (2) Received less than a minute (< 60 secs)
        else if (difference <= Constant.MIN_IN_MILLISECOND)
            return (difference / Constant.SECOND_IN_MILLISECOND) + " seconds ago";
            // (3) Received less than an hour (< 60 mins)
        else if (difference <= Constant.HOUR_IN_MILLISECOND) {
            long final_time = difference / Constant.MIN_IN_MILLISECOND;
            if (final_time == 1) return "1 min ago";
            return final_time + " mins ago";
        }
        // (4) Received an hour ago
        else if (difference <= Constant.DAY_IN_MILLISECOND && ((difference / Constant.HOUR_IN_MILLISECOND) < 2))
            return "About an hour ago";
            // (5) Received less than a day (< 24 hours)
        else if (difference <= Constant.DAY_IN_MILLISECOND) {
            long final_time = difference / Constant.HOUR_IN_MILLISECOND;
            if (final_time == 1) return "1 hour ago";
            return final_time + " hours ago";
        }
        // (6) Received yesterday
        else if (difference <= Constant.YESTERDAY_IN_MILLISECOND)
            return "Yesterday at" + Constant.getDate(timestamp, Constant.TIMEFORMATTER);
            // (7) Received within this week
        else if (difference <= Constant.WEEK_IN_MILLISECOND)
            return Constant.getDate(timestamp, Constant.NOTIFICATION_WEEK_DATEFORMATTER);
            // (8) Received other days
        else return Constant.getDate(timestamp, Constant.NOTIFICATION_DATEFORMATTER);
    }

    // This method shows progress dialog when not showing
    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    // This method dismiss progress dialog when required (dialog must be showing)
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
