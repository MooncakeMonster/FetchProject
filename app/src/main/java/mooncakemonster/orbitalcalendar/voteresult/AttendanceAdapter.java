package mooncakemonster.orbitalcalendar.voteresult;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;

/**
 * Created by BAOJUN on 19/7/15.
 */
public class AttendanceAdapter extends ArrayAdapter<ResultOption> {

    private List<ResultOption> objects;

    public AttendanceAdapter(Context context, int resource, List<ResultOption> objects) {
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
        LayoutInflater inflater;
        final Holder holder;

        if (row == null) {
            inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_checkbox, parent, false);
        }

        final ResultOption resultOption = objects.get(position);

        if (resultOption != null) {
            holder = new Holder();

            holder.checkBox = (CheckBox) row.findViewById(R.id.send_confirm_date);
            holder.username = (TextView) row.findViewById(R.id.participant_username);
            holder.username.setText(resultOption.getUsername());

            holder.checkBox.setChecked(true);
            holder.checkBox.setEnabled(false);

            row.setTag(holder);
        }

        return row;
    }
}
