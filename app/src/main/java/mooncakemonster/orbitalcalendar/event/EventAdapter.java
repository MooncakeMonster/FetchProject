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

public class EventAdapter extends ArrayAdapter<Appointment> {

    private List<Appointment> objects;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd\n MMM\n yyyy");

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

        if(appointment != null) {
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
            holder.event_start_end_time.setText("" + appointment.getStartTime() + " - " + appointment.getEndTime());

            // Get date
            String[] date = appointment.getDate().split(" ");
            date[5] = date[5].replace(",", "");
            String[] splitDate = date[5].split("/");

            holder.event_day.setText(splitDate[0]);
            holder.event_month_year.setText(getMonth(splitDate[1]) + " " + splitDate[2]);

            row.setTag(holder);
        }

        return row;
    }

    private String getMonth(String month) {
        switch (month) {
            case "01": return "JAN";
            case "02": return "FEB";
            case "03": return "MAR";
            case "04": return "APR";
            case "05": return "MAY";
            case "06": return "JUN";
            case "07": return "JUL";
            case "08": return "AUG";
            case "09": return "SEP";
            case "10": return "OCT";
            case "11": return "NOV";
            case "12": return "DEC";
        }
        return "NULL";
    }
}
