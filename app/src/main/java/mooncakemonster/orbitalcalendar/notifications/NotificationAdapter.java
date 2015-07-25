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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.friendlist.FriendDatabase;
import mooncakemonster.orbitalcalendar.profilepicture.RoundImage;
import mooncakemonster.orbitalcalendar.voteinvitation.VoteInvitation;

/**
 * Created by BAOJUN on 7/7/15.
 */
public class NotificationAdapter extends ArrayAdapter<NotificationItem> {

    private static final String TAG = NotificationAdapter.class.getSimpleName();
    private List<NotificationItem> objects;
    CloudantConnect cloudantConnect;
    ProgressDialog progressDialog;
    FriendDatabase friendDatabase;
    UserDatabase db;
    Intent intent;

    public NotificationAdapter(Context context, int resource, List<NotificationItem> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    static class Holder {
        LinearLayout layout;
        SimpleDraweeView action_image;
        TextView message;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater;
        Holder holder;

        if (row == null) {
            inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Fresco.initialize(getContext());
            row = inflater.inflate(R.layout.row_notifications, parent, false);
        }

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

            if (cloudantConnect == null)
                cloudantConnect = new CloudantConnect(getContext(), "user");

            RoundImage roundImage = new RoundImage(cloudantConnect.retrieveUserImage(notificationItem.getSender_username()));
            holder.action_image.setImageDrawable(roundImage);

            holder.message = (TextView) row.findViewById(R.id.message);
            holder.message.setText(spannable);

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

                    switch (notificationItem.getNotificationId()) {
                        case 1:
                            dialogBuilder.setTitle("Friend request");
                            dialogBuilder.setMessage("Would you like to accept friend request from \"" + notificationItem.getSender_username() + "\"?");
                            dialogBuilder.setNegativeButton("Cancel", null);
                            dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    progressDialog.setMessage("Informing " + notificationItem.getSender_username() + " ...");
                                    showDialog();

                                    Timer timer = new Timer();
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            // Also add sender into friend list once friend request is accepted
                                            // Get date and time when friend is added
                                            Calendar c = Calendar.getInstance();
                                            Date date = c.getTime();
                                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy, EEE @ hh:mma");

                                            // Save friend into friendlist database
                                            friendDatabase = new FriendDatabase(getContext());
                                            friendDatabase.putInformation(friendDatabase, format.format(date), notificationItem.getSender_username());

                                            // Send back to user to inform about friend request acceptance
                                            if (cloudantConnect == null)
                                                cloudantConnect = new CloudantConnect(getContext(), "user");

                                            // Retrieve own username
                                            db = new UserDatabase(getContext());
                                            HashMap<String, String> user = db.getUserDetails();
                                            String my_username = user.get("username");
                                            cloudantConnect.sendFriendAccepted(my_username, notificationItem.getSender_username());

                                            hideDialog();
                                        }
                                    }, 1500);
                                }
                            });
                            dialogBuilder.show();
                            break;
                        case 2:
                            break;
                        case 3:
                            intent = new Intent(getContext(), VoteInvitation.class);
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
                            dialogBuilder.setTitle("Confirm attendance");
                            dialogBuilder.setMessage("Would you like to confirm attendance for the event \""
                                    + notificationItem.getSender_event() + "\" with " + sender_username + "?");
                            dialogBuilder.setNegativeButton("Cancel", null);
                            dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
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
                            break;
                        case 7:
                            intent = new Intent(getContext(), VoteInvitation.class);
                            intent.putExtras(bundle);
                            inner_view.getContext().startActivity(intent);
                            break;
                        case 8:

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
