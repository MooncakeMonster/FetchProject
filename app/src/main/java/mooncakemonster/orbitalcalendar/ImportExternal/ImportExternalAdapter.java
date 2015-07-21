package mooncakemonster.orbitalcalendar.ImportExternal;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import mooncakemonster.orbitalcalendar.database.Appointment;

/**
 *
 */
public class ImportExternalAdapter extends ArrayAdapter<Appointment> {
    private List<Appointment> objects;

    public ImportExternalAdapter(Context context, int resource, List<Appointment> objects) {
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

        }

        Appointment appointment = objects.get(position);

        if(appointment != null) {
            holder = new Holder();

            row.setTag(holder);
        }

        return row;
    }

}
