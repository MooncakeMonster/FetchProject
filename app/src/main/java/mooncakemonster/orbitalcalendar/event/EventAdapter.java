package mooncakemonster.orbitalcalendar.event;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.AppointmentController;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.votesend.VotingActivity;

/*************************************************************************************************
 * Purpose: EventAdapter.java serves as a "holder" which contain the interface for how an appointment
 * "unit" will appear in EventFragment.java
 * <p/>
 * Moreover, by clicking on EventAdapter, users may opt to:
 * (1) Edit/View Event
 * (2) Set the appointment for Voting
 * (3) Delete appointment
 * <p/>
 * Access via: Click on the menu button on top left corner, then Events
 **************************************************************************************************/

public class EventAdapter extends BaseAdapter {

    AppointmentController appointmentDatabase;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private Context context;

    private List<Appointment> objects = new ArrayList<>();
    private TreeSet<Integer> separatorSet = new TreeSet<Integer>();

    public EventAdapter(Context context) {
        this.context = context;
        appointmentDatabase = new AppointmentController(context);
    }

    public void addItem(final Appointment appointment) {
        objects.add(appointment);
        notifyDataSetChanged();
    }

    public void addSeparatorItem(final Appointment appointment, int position) {
        separatorSet.add(position);
        objects.add(appointment);
        notifyDataSetChanged();
    }

    public void removeItem(final Appointment appointment, boolean deletion) {
        appointmentDatabase.open();

        // remove from list
        long id = appointment.getId() - appointment.getSub_id();
        long last_day = (appointment.getEndDate() - appointment.getActualStartDate()) / Constant.DAY_IN_MILLISECOND;

        // For few days event
        if(deletion) {
            for(int i = 0; i < last_day; i++)
                objects.remove(appointmentDatabase.getTargetEvent(id + i, i));
            appointmentDatabase.deleteAppointment(appointment);

        } else {
            objects.remove(appointment);
            appointmentDatabase.deleteTargetAppointment(appointment);
        }

        updateAdapter(objects);
    }

    @Override
    public int getItemViewType(int position) {
        return separatorSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Appointment getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        LayoutInflater inflater;
        View row = convertView;
        Holder holder;

        final Appointment appointment = getItem(position);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (getItemViewType(position)) {
            case TYPE_SEPARATOR:
                row = inflater.inflate(R.layout.separator_listview, null);
                break;

            case TYPE_ITEM:
                row = inflater.inflate(R.layout.row_event, null);

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
                    if(appointment.getSub_id() == -1) holder.event_title.setText(appointment.getEvent());
                    else holder.event_title.setText(appointment.getEvent() + " (Day " + appointment.getSub_id() + ")");

                    if(appointment.getLocation() == null) holder.event_location.setText("Location not available");
                    else holder.event_location.setText(appointment.getLocation());

                    // Get date
                    String finalDate = Constant.getDate(appointment.getStartDate(), new SimpleDateFormat("yyyy MMM dd"));
                    final String[] date = finalDate.split(" ");
                    holder.event_day.setText(date[2]);
                    holder.event_month_year.setText(date[1] + " " + date[0]);

                    // Get time
                    final String startTime = Constant.getDate(appointment.getStartDate(), "hh:mm a");
                    final String endTime = Constant.getDate(appointment.getEndDate(), "hh:mm a");
                    holder.event_start_end_time.setText(startTime + " - " + endTime);

                    // Lead users to edit event
                    holder.edit_event.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Instantiate EventView.java for viewing of appointment (and editing)
                            DialogFragment dialogfragment = EventView.newInstance(appointment);
                            dialogfragment.show(((FragmentActivity) context).getSupportFragmentManager(), null);
                            appointmentDatabase.open();
                            objects = appointmentDatabase.getAllAppointment();
                            notifyDataSetChanged();
                        }
                    });

                    // Lead users to vote event
                    holder.vote_event.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (appointment.getStartDate() > System.currentTimeMillis()) {
                                Intent intent = new Intent(context, VotingActivity.class);

                                Bundle bundle = new Bundle();

                                //TODO: Replace with parcable once MVP is out
                                bundle.putSerializable("appointment", appointment);

                                // Store bundle into intent
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                            } else {
                                Constant.alertUser(context, "Vote Event", "Unable to request voting as the event is already over.");
                            }
                        }
                    });

                    // Remove from list
                    holder.remove_event.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(appointment.getSub_id() == -1) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                alert.setTitle("Delete event");
                                alert.setMessage("Are you sure you want to delete \"" + appointment.toString() + "\"?");

                                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Delete from ArrayAdapter & allAppointment
                                        removeItem(appointment, false);
                                        //Remove dialog after execution of the above
                                        notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                                alert.show();
                            } else {
                                final String[] sort_type = {"Delete selected day", "Delete entire event"};

                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                                alertBuilder.setTitle("Delete event").setSingleChoiceItems(sort_type, 0, null)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                                        switch (selectedPosition) {
                                            case 0:
                                                removeItem(appointment, false);
                                                notifyDataSetChanged();
                                                break;
                                            case 1:
                                                removeItem(appointment, true);
                                                notifyDataSetChanged();
                                                break;

                                        }
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("Cancel", null);

                                Dialog dialog = alertBuilder.create();
                                dialog.show();
                            }
                        }
                    });

                    row.setTag(holder);
                }

                break;
        }
        return row;
    }

    private void updateAdapter(List<Appointment> objects) {
        this.objects = objects;
        this.notifyDataSetChanged();
    }
}
