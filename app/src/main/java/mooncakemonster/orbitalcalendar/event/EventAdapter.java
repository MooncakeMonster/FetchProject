package mooncakemonster.orbitalcalendar.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.Constant;

public class EventAdapter extends ArrayAdapter<Appointment> {

    private List<Appointment> objects;

    public EventAdapter(Context context, int resources, List<Appointment> objects) {
        super(context, resources, objects);
        this.objects = objects;
    }

    static class Holder {
        TextView event_title;
        TextView event_location;
        TextView event_start_end_time;
        TextView event_day, event_month_year;
        RelativeLayout event_colour;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_event, null);
        }

        Appointment appointment = objects.get(position);

        if (appointment != null) {
            holder = new Holder();
            holder.event_colour = (RelativeLayout) row.findViewById(R.id.event_set_colour);
            holder.event_title = (TextView) row.findViewById(R.id.event_title);
            holder.event_location = (TextView) row.findViewById(R.id.event_location);
            holder.event_start_end_time = (TextView) row.findViewById(R.id.event_start_end_time);
            holder.event_day = (TextView) row.findViewById(R.id.event_day);
            holder.event_month_year = (TextView) row.findViewById(R.id.event_month_year);

            holder.event_colour.setBackgroundResource(appointment.getColour());
            holder.event_title.setText(appointment.getEvent());
            holder.event_location.setText(appointment.getLocation());


            // Get date
            SimpleDateFormat standardFormat = new SimpleDateFormat("yyyy MM dd");
            String finalDate = Constant.standardYearMonthDate(appointment.getStartProperDate(), standardFormat, new SimpleDateFormat("yyyy MMM dd"));

            String[] date = finalDate.split(" ");
            holder.event_day.setText(date[2]);
            holder.event_month_year.setText(date[1] + " " + date[0]);

            // Get time
            String startTime = Constant.getDate(appointment.getStartDate(), "hh:mm a");
            String endTime = Constant.getDate(appointment.getEndDate(), "hh:mm a");
            holder.event_start_end_time.setText(startTime + " - " + endTime);

            row.setTag(holder);
        }

        return row;
    }


}
