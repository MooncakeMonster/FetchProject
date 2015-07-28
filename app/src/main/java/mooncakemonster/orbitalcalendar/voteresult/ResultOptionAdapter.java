package mooncakemonster.orbitalcalendar.voteresult;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.profilepicture.RoundImage;

/**
 * Created by BAOJUN on 19/7/15.
 */
public class ResultOptionAdapter extends ArrayAdapter<ResultOption> {

    private List<ResultOption> objects;
    private CloudantConnect cloudantConnect;

    public ResultOptionAdapter(Context context, int resource, List<ResultOption> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    static class Holder {
        CheckBox checkbox;
        SimpleDraweeView image;
        TextView username;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;

        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_checkbox, parent, false);
        }

        final ResultOption resultOption = objects.get(position);

        if(resultOption != null) {
            holder = new Holder();
            holder.checkbox = (CheckBox) row.findViewById(R.id.send_confirm_date);
            holder.image = (SimpleDraweeView) row.findViewById(R.id.check_image);
            holder.username = (TextView) row.findViewById(R.id.participant_username);

            holder.username.setText(resultOption.getUsername());

            // Retrieve user image from cloudant database
            if (cloudantConnect == null) this.cloudantConnect = new CloudantConnect(getContext(), "user");
            RoundImage roundImage = new RoundImage(cloudantConnect.retrieveUserImage(resultOption.getUsername()));
            holder.image.setImageDrawable(roundImage);

            holder.checkbox.setChecked(true);

            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        resultOption.setChecked(true);
                    } else {
                        resultOption.setChecked(false);
                    }
                }
            });

            row.setTag(holder);
        }

        return row;
    }
}
