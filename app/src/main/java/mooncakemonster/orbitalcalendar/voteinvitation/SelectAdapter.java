package mooncakemonster.orbitalcalendar.voteinvitation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.text.SimpleDateFormat;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Constant;

/**
 * Created by BAOJUN on 7/7/15.
 */
public class SelectAdapter extends ArrayAdapter<SelectItem> {

    private static final String TAG = SelectAdapter.class.getSimpleName();
    private List<SelectItem> objects;

    public SelectAdapter(Context context, int resource, List<SelectItem> objects) {
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
        final Holder holder;

        inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.row_selected_checkbox, parent, false);

        final SelectItem selectItem = objects.get(position);

        if (selectItem != null) {
            holder = new Holder();

            holder.select_date = (CheckBox) row.findViewById(R.id.select_date);
            holder.select_start_date = (Button) row.findViewById(R.id.select_start_date);
            holder.select_end_date = (Button) row.findViewById(R.id.select_end_date);
            holder.select_start_time = (Button) row.findViewById(R.id.select_start_time);
            holder.select_end_time = (Button) row.findViewById(R.id.select_end_time);

            holder.select_date.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) selectItem.setSelected_date(true);
                    else selectItem.setSelected_date(false);
                }
            });

            holder.select_start_date.setText(Constant.standardYearMonthDate(selectItem.getEvent_start_date(), new SimpleDateFormat("dd/MM/yyyy"), Constant.DATEFORMATTER));
            holder.select_end_date.setText(Constant.standardYearMonthDate(selectItem.getEvent_end_date(), new SimpleDateFormat("dd/MM/yyyy"), Constant.DATEFORMATTER));
            holder.select_start_time.setText(selectItem.getEvent_start_time());
            holder.select_end_time.setText(selectItem.getEvent_end_time());

            row.setTag(holder);
        }

        return row;
    }
}
