package mooncakemonster.orbitalcalendar.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.Constant;

public class EventDayAdapter extends ArrayAdapter<Appointment>{

    private List<Appointment> objects;

    public EventDayAdapter(Context context, int resource, List<Appointment> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    static class Holder {
        ImageView event_colour;
        TextView event_title;
        TextView event_time;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;

        if(row == null) {
            LayoutInflater inflator = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflator.inflate(R.layout.row_event_day, parent, false);
        }

        Appointment appointment = objects.get(position);

        if(appointment != null) {
            holder = new Holder();
            holder.event_colour = (ImageView) row.findViewById(R.id.eventIcon);
            holder.event_title = (TextView) row.findViewById(R.id.eventName);
            holder.event_time = (TextView) row.findViewById(R.id.eventTime);

            holder.event_colour.setImageResource(Constant.getBearColour(appointment.getColour()));
            holder.event_title.setText(appointment.getEvent());

            // Get time
            String startTime = Constant.getDate(appointment.getStartDate(), "hh:mm a");
            String endTime = Constant.getDate(appointment.getEndDate(), "hh:mm a");
            holder.event_time.setText(startTime + " - " + endTime);

            row.setTag(holder);
        }

        return row;
    }
}
