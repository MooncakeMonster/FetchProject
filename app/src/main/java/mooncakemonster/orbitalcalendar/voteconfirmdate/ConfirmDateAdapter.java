package mooncakemonster.orbitalcalendar.voteconfirmdate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;

/**
 * Created by BAOJUN on 11/7/15.
 */
public class ConfirmDateAdapter extends ArrayAdapter<ConfirmParticipants> {
    private List<ConfirmParticipants> objects;

    public ConfirmDateAdapter(Context context, int resource, List<ConfirmParticipants> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    static class Holder {
        CheckBox checkBox;
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

        final ConfirmParticipants confirmParticipants = objects.get(position);

        if(confirmParticipants != null) {
            holder = new Holder();
            holder.checkBox = (CheckBox) row.findViewById(R.id.send_confirm_date);
            holder.username = (TextView) row.findViewById(R.id.participant_username);

            String username = confirmParticipants.getUsername();

            if(username.isEmpty()) {
                holder.checkBox.setVisibility(View.INVISIBLE);
                holder.username.setText("None");
            } else {
                holder.username.setText(username);
                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) confirmParticipants.setSend(true);
                        else confirmParticipants.setSend(false);
                    }
                });
            }

            row.setTag(holder);
        }

        return row;
    }
}
