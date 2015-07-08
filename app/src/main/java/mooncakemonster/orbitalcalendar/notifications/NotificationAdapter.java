package mooncakemonster.orbitalcalendar.notifications;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;

/**
 * Created by BAOJUN on 7/7/15.
 */
public class NotificationAdapter extends ArrayAdapter<NotificationItem> {
    private List<NotificationItem> objects;

    public NotificationAdapter(Context context, int resource, List<NotificationItem> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    static class Holder {
        TextView message;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater;
        Holder holder;

        if(row == null) {
            inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_notifications, parent, false);
        }

        NotificationItem notificationItem = objects.get(position);

        if(notificationItem != null) {
            holder = new Holder();

            String sender_username = notificationItem.getSender_username();
            String sender_message = notificationItem.getMessage();
            String sender_event = notificationItem.getSender_event();
            String action = notificationItem.getAction();

            Log.d("notiadapter", sender_username);

            // Create a new spannable
            SpannableString spannable = new SpannableString(sender_username + sender_message + sender_event + action);
            spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, sender_username.length(), 0);
            //spannable.setSpan(new StyleSpan(Typeface.BOLD), sender_username.length() + sender_message.length(), sender_event.length(), 0);

            // Set the text of a textView with the spannable object
            holder.message = (TextView) row.findViewById(R.id.message);
            holder.message.setText(spannable);

            row.setTag(holder);
        }

        return row;
    }
}
