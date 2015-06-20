package mooncakemonster.orbitalcalendar.event;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.AppointmentController;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.voting.VotingActivity;

public class EventAdapter extends ArrayAdapter<Appointment> {

    //Set database to allow user to retrieve data to populate EventFragment.java
    private AppointmentController appointmentDatabase  = new AppointmentController(getContext());
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
        Button edit_event, vote_event, remove_event;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void remove(Appointment object) {
        super.remove(object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater;
        View row = convertView;
        Holder holder;

        if (convertView == null) {
            inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_event, null);
        }

        final Appointment appointment = objects.get(position);
        appointmentDatabase.open();

        if (appointment != null) {
            holder = new Holder();
            holder.event_colour = (RelativeLayout) row.findViewById(R.id.event_set_colour);
            holder.event_title = (TextView) row.findViewById(R.id.event_title);
            holder.event_location = (TextView) row.findViewById(R.id.event_location);
            holder.event_start_end_time = (TextView) row.findViewById(R.id.event_start_end_time);
            holder.event_day = (TextView) row.findViewById(R.id.event_day);
            holder.event_month_year = (TextView) row.findViewById(R.id.event_month_year);

            // Child
            holder.edit_event = (Button) row.findViewById(R.id.expand_edit_event);
            holder.vote_event = (Button) row.findViewById(R.id.expand_vote_event);
            holder.remove_event = (Button) row.findViewById(R.id.remove_event);

            holder.event_colour.setBackgroundResource(appointment.getColour());
            holder.event_title.setText(appointment.getEvent());
            holder.event_location.setText(appointment.getLocation());

            // Get date
            SimpleDateFormat standardFormat = new SimpleDateFormat("yyyy MM dd");
            String finalDate = Constant.standardYearMonthDate(appointment.getStartProperDate(), standardFormat, new SimpleDateFormat("yyyy MMM dd"));
            final String[] date = finalDate.split(" ");
            holder.event_day.setText(date[2]);
            holder.event_month_year.setText(date[1] + " " + date[0]);

            // Get time
            final String startTime = Constant.getDate(appointment.getStartDate(), "hh:mm a");
            final String endTime = Constant.getDate(appointment.getEndDate(), "hh:mm a");
            holder.event_start_end_time.setText(startTime + " - " + endTime);

            final View view = row;

            // Lead users to edit event
            holder.edit_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Instantiate EventView.java for viewing of appointment (and editing)
                    DialogFragment dialogfragment = EventView.newInstance(appointment);
                    dialogfragment.show(((FragmentActivity) getContext()).getSupportFragmentManager(), null);
                    objects = appointmentDatabase.getAllAppointment();
                    notifyDataSetChanged();
                }
            });

            // Lead users to vote event
            holder.vote_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), VotingActivity.class);
                    Bundle bundle = new Bundle();

                    // Get start date in proper format
                    SimpleDateFormat standardFormat = new SimpleDateFormat("yyyy MM dd");
                    String startDate = Constant.standardYearMonthDate(appointment.getStartProperDate(), standardFormat, new SimpleDateFormat("dd/MM/yyyy, EEE"));
                    String endDate = Constant.standardYearMonthDate(Constant.getDate(appointment.getEndDate(), "yyyy MM dd"), standardFormat, new SimpleDateFormat("dd/MM/yyyy, EEE"));

                    // Storing date into bundle
                    bundle.putString("event_title", appointment.getEvent());
                    bundle.putString("event_location", appointment.getLocation());
                    bundle.putString("event_start_date", startDate);
                    bundle.putString("event_end_date", endDate);
                    bundle.putString("event_start_time", startTime);
                    bundle.putString("event_end_time", endTime);
                    bundle.putInt("event_colour", appointment.getColour());

                    // Store bundle into intent
                    intent.putExtras(bundle);
                    view.getContext().startActivity(intent);

                }
            });

            // Remove from list
            holder.remove_event.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Delete appointment");
                    alert.setMessage("Are you sure you want to delete \"" + appointment.toString() + "\"?");

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Delete from SQLite database
                            appointmentDatabase.deleteAppointment(appointment);
                            //Delete from ArrayAdapter & allAppointment
                            objects.remove(appointment);
                            remove(appointment);
                            notifyDataSetChanged();
                            //Remove dialog after execution of the above
                            dialog.dismiss();
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
            });


            row.setTag(holder);
        }


        return row;
    }
}
