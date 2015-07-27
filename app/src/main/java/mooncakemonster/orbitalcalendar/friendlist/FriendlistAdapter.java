package mooncakemonster.orbitalcalendar.friendlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.cloudant.User;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.profilepicture.RoundImage;

/**
 * Created by BAOJUN on 6/7/15.
 */
public class FriendlistAdapter extends ArrayAdapter<FriendItem> {

    private List<FriendItem> objects;
    private CloudantConnect cloudantConnect;

    public FriendlistAdapter(Context context, int resource, List<FriendItem> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    static class Holder {
        SimpleDraweeView friend_image;
        TextView friend_username;
        TextView friend_timestamp;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;

        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Fresco.initialize(getContext());
            row = inflater.inflate(R.layout.row_friendlist, parent, false);
        }

        FriendItem friend = objects.get(position);

        if(friend != null) {
            holder = new Holder();
            holder.friend_image = (SimpleDraweeView) row.findViewById(R.id.friend_image);
            holder.friend_username = (TextView) row.findViewById(R.id.friend_username);
            holder.friend_timestamp = (TextView) row.findViewById(R.id.friend_timestamp);

            if (cloudantConnect == null)
                cloudantConnect = new CloudantConnect(getContext(), "user");
            cloudantConnect.startPullReplication();
            User friend_user = cloudantConnect.getTargetUser(friend.getUsername());

            holder.friend_username.setText(friend.getUsername());

            if (friend.getFriend_added().equals("true")) {
                Bitmap bitmap = cloudantConnect.retrieveUserImage(friend_user.getUsername());
                RoundImage roundImage = new RoundImage(bitmap);
                holder.friend_image.setImageDrawable(roundImage);
                holder.friend_timestamp.setText("Friend request accepted " + getTimestampText(friend.getTimestamp()));
            } else {
                // Prevent users from seeing user's image if the user has not accepted his/her friend request
                holder.friend_image.setBackgroundResource(R.drawable.profile);
                holder.friend_username.setTextColor(getContext().getResources().getColor(R.color.timestamp));
                holder.friend_timestamp.setTextColor(getContext().getResources().getColor(R.color.timestamp));
                holder.friend_timestamp.setText("Friend request sent " + getTimestampText(friend.getTimestamp()));
            }

            row.setTag(holder);
        }

        return row;
    }

    // This method computes the time received the notification
    private String getTimestampText(long timestamp) {
        long current_second = Constant.retrieveCurrentTime();
        long difference = current_second - timestamp;

        // (1) Received less than 10 seconds
        if(difference <= Constant.MIN_IN_MILLISECOND / 6) return "a few seconds ago";
            // (2) Received less than a minute (< 60 secs)
        else if(difference <= Constant.MIN_IN_MILLISECOND) return (difference / Constant.SECOND_IN_MILLISECOND) + " seconds ago";
            // (3) Received less than an hour (< 60 mins)
        else if(difference <= Constant.HOUR_IN_MILLISECOND) {
            long final_time = difference / Constant.MIN_IN_MILLISECOND;
            if (final_time == 1) return "1 min ago";
            return final_time + " mins ago";
        }
        // (4) Received an hour ago
        else if(difference <= Constant.DAY_IN_MILLISECOND && ((difference / Constant.HOUR_IN_MILLISECOND) < 2)) return "about an hour ago";
            // (5) Received less than a day (< 24 hours)
        else if(difference <= Constant.DAY_IN_MILLISECOND) {
            long final_time = difference / Constant.HOUR_IN_MILLISECOND;
            if (final_time == 1) return "1 hour ago";
            return final_time + " hours ago";
        }
        // (6) Received yesterday
        else if(difference <= Constant.YESTERDAY_IN_MILLISECOND) return "yesterday at" + Constant.getDate(timestamp, Constant.TIMEFORMATTER);
            // (7) Received within this week
        else if(difference <= Constant.WEEK_IN_MILLISECOND) return "on" + Constant.getDate(timestamp, Constant.NOTIFICATION_WEEK_DATEFORMATTER);
            // (8) Received other days
        else return "on" + Constant.getDate(timestamp, Constant.NOTIFICATION_DATEFORMATTER);
    }
}
