package mooncakemonster.orbitalcalendar.notifications;

import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.voteinvitation.VoteInvitation;

/**
 * Created by BAOJUN on 7/7/15.
 */
public class NotificationAdapter extends ArrayAdapter<NotificationItem> {

    private static final String TAG = NotificationAdapter.class.getSimpleName();
    private List<NotificationItem> objects;
    CloudantConnect cloudantConnect;
    UserDatabase db;
    Intent intent;

    public NotificationAdapter(Context context, int resource, List<NotificationItem> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    static class Holder {
        LinearLayout layout;
        ImageView action_image;
        TextView message;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater;
        Holder holder;

        if (row == null) {
            inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_notifications, parent, false);
        }

        final NotificationItem notificationItem = objects.get(position);

        if (notificationItem != null) {
            holder = new Holder();

            String sender_username = notificationItem.getSender_username();
            String sender_message = notificationItem.getMessage();
            String sender_event = notificationItem.getSender_event();
            String action = notificationItem.getAction();

            // Create a new spannable
            SpannableString spannable = new SpannableString(sender_username + sender_message + sender_event + action);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, sender_username.length(), 0);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), sender_username.length() + sender_message.length(),
                    sender_username.length() + sender_message.length() + sender_event.length(), 0);

            // Set the text of a textView with the spannable object
            holder.layout = (LinearLayout) row.findViewById(R.id.notification_layout);
            holder.action_image = (ImageView) row.findViewById(R.id.action_image);
            String bitmap_string = new String (notificationItem.getSender_image());
            holder.action_image.setImageBitmap(Constant.stringToBitmap(bitmap_string));

            holder.message = (TextView) row.findViewById(R.id.message);
            holder.message.setText(spannable);


            final View inner_view = row;
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*
                     * case 1: Target participants receive voting request
                     * case 2: Sender received voting response from target participants that is voted
                     * case 3: Sender received voting response from target participants that is rejected
                     * case 4: Target participants received confirmed date and time of an event
                     * case 5: Target participants gets reminder to vote for an event
                     */

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("vote_item", notificationItem);

                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

                    switch (notificationItem.getNotificationId()) {
                        case 1:
                            intent = new Intent(getContext(), VoteInvitation.class);
                            intent.putExtras(bundle);
                            inner_view.getContext().startActivity(intent);
                            break;
                        case 2:
                            // TODO: Show voting result fragment or VotingResultActivity if possible
                            break;
                        case 3:
                            dialogBuilder.setTitle("Reason for Vote Rejection");
                            dialogBuilder.setMessage(notificationItem.getSender_username() +
                                    ": \"" + notificationItem.getReject_reason() + "\"");
                            dialogBuilder.setPositiveButton("Ok", null);
                            dialogBuilder.show();
                            break;
                        case 4:
                            dialogBuilder.setTitle("Confirm attendance");
                            dialogBuilder.setMessage("Would you like to confirm attendance for the event \""
                                    + notificationItem.getSender_event() + "\" with " + notificationItem.getSender_username() + "?");
                            dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (cloudantConnect == null)
                                        cloudantConnect = new CloudantConnect(getContext(), "user");

                                    // Retrieve own username
                                    db = new UserDatabase(getContext());
                                    HashMap<String, String> user = db.getUserDetails();
                                    String my_username = user.get("username");

                                    cloudantConnect.sendAttendanceToTargetParticipants(my_username, notificationItem.getSender_username(),
                                            notificationItem.getEventId(), notificationItem.getImageId(), notificationItem.getSender_event());
                                    cloudantConnect.startPushReplication();

                                    Toast.makeText(getContext(), "Attendance sent successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                            dialogBuilder.setNegativeButton("Cancel", null);
                            dialogBuilder.show();
                            break;
                        case 5:
                            intent = new Intent(getContext(), VoteInvitation.class);
                            intent.putExtras(bundle);
                            inner_view.getContext().startActivity(intent);
                            break;
                        case 6:

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

    // This method sets appropriate background image for notification.
    private int setBackground(int notificationId, int imageId) {
        switch (imageId) {
            case R.color.redbear:
                return R.drawable.ballred;
            case R.color.yellowbear:
                return R.drawable.ballyellow;
            case R.color.greenbear:
                return R.drawable.ballgreen;
            case R.color.bluebear:
                return R.drawable.ballblue;
            case R.color.purplebear:
                return R.drawable.ballpurple;
        }

        // Should not reach here
        return R.color.redbear;
    }
}
