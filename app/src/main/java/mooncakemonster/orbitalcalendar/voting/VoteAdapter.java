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
import java.util.Calendar;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;

public class VoteAdapter extends ArrayAdapter<VoteItem> {

    private List<VoteItem> objects;

    private Calendar dateTime = Calendar.getInstance();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy, EEE");
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");

    public VoteAdapter(Context context, int resources, List<VoteItem> objects) {
        super(context, resources, objects);
        this.objects = objects;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    static class Holder {
        Button vote_start_date;
        Button vote_start_time;
        ImageView remove_option;      // remove from list
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final Holder holder;

        final VoteItem voteItem =  objects.get(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_vote, parent, false);
        }

        holder = new Holder();
        holder.vote_start_date = (Button) row.findViewById(R.id.option_date);
        holder.vote_start_time = (Button) row.findViewById(R.id.option_time);
        holder.remove_option = (ImageView) row.findViewById(R.id.remove_option);

        // Set default date and time as what the user picks when they create event
        holder.vote_start_date.setText(voteItem.getEvent_start_date());
        holder.vote_start_time.setText(voteItem.getEvent_start_time());

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

        // Remove option from list
        holder.remove_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(voteItem);
                notifyDataSetChanged();
            }
        });

        row.setTag(holder);

        return row;
    }
}
