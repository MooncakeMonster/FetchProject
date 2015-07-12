package mooncakemonster.orbitalcalendar.notifications;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.URISyntaxException;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;

/**
 * Created by BAOJUN on 7/7/15.
 */
public class NotificationAdapter extends ArrayAdapter<NotificationItem> {

    private static final String TAG = NotificationAdapter.class.getSimpleName();
    private List<NotificationItem> objects;
    Intent intent;

    public NotificationAdapter(Context context, int resource, List<NotificationItem> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    static class Holder {
        LinearLayout linearLayout;
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

            // Create a new spannable TODO: Unable to bold properly
            SpannableString spannable = new SpannableString(sender_username + sender_message + sender_event + action);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, sender_username.length(), 0);
            //spannable.setSpan(new StyleSpan(Typeface.NORMAL), sender_username.length(), sender_message.length(), 0);
            //spannable.setSpan(new StyleSpan(Typeface.BOLD), sender_message.length(), sender_event.length(), 0);

            // Set the text of a textView with the spannable object
            holder.linearLayout = (LinearLayout) row.findViewById(R.id.notification_layout);
            holder.action_image = (ImageView) row.findViewById(R.id.action_image);
            // TODO: Set appropriate background image
            holder.action_image.setBackgroundResource(setBackground(notificationItem.getNotificationId(), notificationItem.getImageId()));

            holder.message = (TextView) row.findViewById(R.id.message);
            holder.message.setText(spannable);


            final View inner_view = row;
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        intent = Intent.parseUri(notificationItem.getIntent(), 0);
                    } catch (URISyntaxException e) {
                        Log.e(TAG, "Unable to retrieve intent");
                    }

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("vote_item", notificationItem);
                    intent.putExtras(bundle);
                    inner_view.getContext().startActivity(intent);
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
        return -1;
    }
}
