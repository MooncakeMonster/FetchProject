package mooncakemonster.orbitalcalendar.friendlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;

/**
 * Created by BAOJUN on 6/7/15.
 */
public class FriendlistAdapter extends ArrayAdapter<FriendItem> {

    private List<FriendItem> objects;

    public FriendlistAdapter(Context context, int resource, List<FriendItem> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    static class Holder {
        TextView friend_alpha;
        TextView friend_username;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;

        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_friendlist, parent, false);
        }

        FriendItem friend = objects.get(position);

        if(friend != null) {
            holder = new Holder();
            holder.friend_alpha = (TextView) row.findViewById(R.id.friend_alpha);
            holder.friend_username = (TextView) row.findViewById(R.id.friend_username);

            //holder.friend_alpha.setBackgroundResource(friend.getImage());
            //holder.friend_alpha.setText(String.valueOf(friend.getUsername().charAt(0)));
            holder.friend_username.setText(friend.getUsername());

            row.setTag(holder);
        }

        return row;
    }
}
