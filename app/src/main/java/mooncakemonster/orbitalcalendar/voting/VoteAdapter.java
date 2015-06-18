package mooncakemonster.orbitalcalendar.voting;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.AppointmentController;
import mooncakemonster.orbitalcalendar.database.Constant;

public class VoteAdapter extends ArrayAdapter {

    private Calendar dateTime = Calendar.getInstance();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy, EEE");
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");

    //Set database to allow user to retrieve data to populate EventFragment.java
    private AppointmentController appointmentDatabase;
    private List list = new ArrayList();
    private Context context;

    private List<VoteItem> objects;

    public VoteAdapter(Context context, int resources) {
        super(context, resources);
        this.context = context;
        appointmentDatabase = new AppointmentController(context);
        appointmentDatabase.open();
    }

    public void add(VoteItem object) {
        list.add(object);
        super.add(object);
    }

    static class Holder {
        Button vote_start_date;
        Button vote_start_time;
        ImageView remove_date;      // remove from list
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final Holder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_vote, parent, false);
        }

        VoteItem voteItem = objects.get(position);

        if (voteItem != null) {
            holder = new Holder();
            holder.vote_start_date = (Button) row.findViewById(R.id.option_date);
            holder.vote_start_time = (Button) row.findViewById(R.id.option_time);

            // (1) Retrieve latest event from database if user just created
            Appointment appointment = appointmentDatabase.getLatestEvent();

            // Set default date and time as what the user picks when they create event
            SimpleDateFormat standardFormat = new SimpleDateFormat("yyyy MM dd");
            holder.vote_start_date.setText(Constant.standardYearMonthDate(appointment.getStartProperDate(), standardFormat, new SimpleDateFormat("yyyy MMM dd")));
            holder.vote_start_time.setText(Constant.getDate(appointment.getStartDate(), "hh:mm a"));

            // Set selected date on the button
            holder.vote_start_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DatePickerDialog date = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            dateTime.set(year, monthOfYear, dayOfMonth);
                            holder.vote_start_date.setText(dateFormatter.format(dateTime.getTime()));
                        }
                    }, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH));
                    date.show();
                }
            });

            // Set selected time on the button
            holder.vote_start_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog time = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            dateTime.set(Calendar.MINUTE, minute);
                            holder.vote_start_time.setText(timeFormatter.format(dateTime.getTime()));
                        }
                    }, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), false);
                    time.show();
                }
            });

            row.setTag(holder);
        }
        return row;
    }
}
