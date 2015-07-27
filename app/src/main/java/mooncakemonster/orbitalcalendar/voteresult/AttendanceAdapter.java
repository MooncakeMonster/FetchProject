package mooncakemonster.orbitalcalendar.voteresult;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.profilepicture.RoundImage;

/**
 * Created by BAOJUN on 19/7/15.
 */
public class AttendanceAdapter extends ArrayAdapter<ResultOption> {

    private List<ResultOption> objects;
    CloudantConnect cloudantConnect;

    public AttendanceAdapter(Context context, int resource, List<ResultOption> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    static class Holder {
        TextView position;
        SimpleDraweeView image;
        TextView username;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater;
        final Holder holder;

        if (row == null) {
            inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_nocheckbox, parent, false);
        }

        final ResultOption resultOption = objects.get(position);

        if (resultOption != null) {
            holder = new Holder();

            holder.position = (TextView) row.findViewById(R.id.nocheck_position);
            holder.image = (SimpleDraweeView) row.findViewById(R.id.nocheck_image);
            holder.username = (TextView) row.findViewById(R.id.nocheck_username);

            holder.position.setText((position + 1) + ". ");
            holder.username.setText(resultOption.getUsername());

            // Retrieve user image from cloudant database
            if (cloudantConnect == null) this.cloudantConnect = new CloudantConnect(getContext(), "user");
            RoundImage roundImage = new RoundImage(cloudantConnect.retrieveUserImage(resultOption.getUsername()));
            holder.image.setImageDrawable(roundImage);

            row.setTag(holder);
        }

        return row;
    }
}
