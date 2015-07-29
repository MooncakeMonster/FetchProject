package mooncakemonster.orbitalcalendar.calendar;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.AppointmentController;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.event.EventView;

public class EventDayAdapter extends ArrayAdapter<Appointment>{

    //Set database to allow user to retrieve data to populate EventFragment.java
    private AppointmentController appointmentDatabase = new AppointmentController(getContext());
    private List<Appointment> objects;

    public EventDayAdapter(Context context, int resource, List<Appointment> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    static class Holder {
        RelativeLayout relativeLayout;
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

        final Appointment appointment = objects.get(position);

        if(appointment != null) {
            holder = new Holder();
            holder.relativeLayout = (RelativeLayout) row.findViewById(R.id.event_day_layout);
            holder.event_colour = (ImageView) row.findViewById(R.id.eventIcon);
            holder.event_title = (TextView) row.findViewById(R.id.eventName);
            holder.event_time = (TextView) row.findViewById(R.id.eventTime);

            holder.event_colour.setImageResource(Constant.getBearColour(appointment.getColour()));
            holder.event_title.setText(appointment.getEvent());

            // Get time
            String startTime = Constant.getDate(appointment.getStartDate(), "hh:mm a");
            String endTime = Constant.getDate(appointment.getEndDate(), "hh:mm a");
            holder.event_time.setText(startTime + " - " + endTime);

            // Opens view/edit dialogfragment
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Instantiate EventView.java for viewing of appointment (and editing)
                    DialogFragment dialogfragment = EventView.newInstance(appointment);
                    dialogfragment.show(((FragmentActivity) getContext()).getSupportFragmentManager(), null);
                    //objects = appointmentDatabase.getAllAppointment();
                    notifyDataSetChanged();
                }
            });

            row.setTag(holder);
        }

        return row;
    }
}
