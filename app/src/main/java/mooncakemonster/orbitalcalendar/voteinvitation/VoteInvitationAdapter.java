package mooncakemonster.orbitalcalendar.voteinvitation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.notifications.NotificationItem;

/**
 * Created by BAOJUN on 7/7/15.
 */
public class VoteInvitationAdapter extends ArrayAdapter<NotificationItem> {

    private static final String TAG = VoteInvitationAdapter.class.getSimpleName();
    private List<NotificationItem> objects;

    public VoteInvitationAdapter(Context context, int resource, List<NotificationItem> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    static class Holder {
        CheckBox select_date;
        Button select_start_date;
        Button select_end_date;
        Button select_start_time;
        Button select_end_time;
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

        final NotificationItem notificationItem = objects.get(position);

        if(notificationItem != null) {
            holder = new Holder();

            holder.select_date = (CheckBox) row.findViewById(R.id.select_date);
            holder.select_start_date = (Button) row.findViewById(R.id.select_start_date);
            holder.select_end_date = (Button) row.findViewById(R.id.select_end_date);
            holder.select_start_time = (Button) row.findViewById(R.id.select_start_time);
            holder.select_end_time = (Button) row.findViewById(R.id.select_end_time);

            holder.select_start_date.setText(notificationItem.getStart_date());
            holder.select_end_date.setText(notificationItem.getEnd_date());
            holder.select_start_time.setText(notificationItem.getStart_time());
            holder.select_end_time.setText(notificationItem.getEnd_time());

            row.setTag(holder);
        }

        return row;
    }
}
