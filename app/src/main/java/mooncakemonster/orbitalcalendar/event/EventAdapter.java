package mooncakemonster.orbitalcalendar.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;

public class EventAdapter extends ArrayAdapter<Appointment> {

    private List<Appointment> objects;

    public EventAdapter(Context context, int resources, List<Appointment> objects) {
        super(context, resources, objects);
        this.objects = objects;
    }

    static class Holder {
        TextView event_title;
        Button event_colour;
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
            holder.event_title = (TextView) row.findViewById(R.id.event_title);
            holder.event_colour = (Button) row.findViewById(R.id.event_colour);

            holder.event_title.setText(appointment.getEvent());
            holder.event_colour.setBackgroundResource(appointment.getColour());

            row.setTag(holder);
        }

        return row;
    }
}
