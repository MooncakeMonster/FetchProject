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

            if (cloudantConnect == null) cloudantConnect = new CloudantConnect(getContext(), "user");
            cloudantConnect.startPullReplication();
            User friend_user = cloudantConnect.getTargetUser(friend.getUsername());

            Bitmap bitmap = cloudantConnect.retrieveUserImage(friend_user.getUsername());
            RoundImage roundImage = new RoundImage(bitmap);
            holder.friend_image.setImageDrawable(roundImage);
            holder.friend_username.setText(friend.getUsername());
            holder.friend_timestamp.setText("Became friends on " + friend.getTimestamp());

            row.setTag(holder);
        }

        return row;
    }
}
