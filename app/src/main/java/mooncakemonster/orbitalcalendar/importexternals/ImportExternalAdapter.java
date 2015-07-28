package mooncakemonster.orbitalcalendar.importexternals;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Constant;

/**
 *
 */
public class ImportExternalAdapter extends ArrayAdapter<ImportedAppointment> {
    private List<ImportedAppointment> objects;

    public ImportExternalAdapter(Context context, int resource, List<ImportedAppointment> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    static class Holder {
        TextView import_event_day;
        TextView import_event_month_year;

        TextView import_event_title;
        TextView import_event_location;
        TextView import_event_start_end_time;

        CheckBox to_import;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        Holder holder;

        if(row == null) {
            LayoutInflater inflator = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflator.inflate(R.layout.row_import_external, null);
        }

        ImportedAppointment appointment = objects.get(position);

        if(appointment != null) {
            holder = new Holder();
            holder.import_event_day = (TextView)row.findViewById(R.id.import_event_day);
            holder.import_event_month_year = (TextView) row.findViewById(R.id.import_event_month_year);

            holder.import_event_title = (TextView) row.findViewById(R.id.import_event_title);
            holder.import_event_location = (TextView)row.findViewById(R.id.import_event_location);
            holder.import_event_start_end_time = (TextView) row.findViewById(R.id.import_event_start_end_time);

            holder.to_import = (CheckBox) row.findViewById(R.id.to_import);

            //Get event name and location
            String title;
            String location;
            if( (title = appointment.getEvent()) != null) {
                holder.import_event_title.setText(title);
            }
            if( (location = appointment.getLocation()) != null) {
                holder.import_event_location.setText(location);
            }

            // Get date
            String finalDate = Constant.standardYearMonthDate(appointment.getStartProperDate(), Constant.YYYYMMDD_FORMATTER, new SimpleDateFormat("yyyy MMM dd"));
            final String[] date = finalDate.split(" ");
            holder.import_event_day.setText(date[2]);
            holder.import_event_month_year.setText(date[1] + " " + date[0]);

            // Get time
            final String startTime = Constant.getDate(appointment.getStartDate(), "hh:mm a");
            final String endTime = Constant.getDate(appointment.getEndDate(), "hh:mm a");
            holder.import_event_start_end_time.setText(startTime + " - " + endTime);

            //Get whether to import or not
            holder.to_import.setChecked(appointment.isToImport());

            row.setTag(holder);
        }

        return row;
    }

}
