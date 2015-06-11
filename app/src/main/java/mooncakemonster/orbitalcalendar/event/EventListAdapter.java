package mooncakemonster.orbitalcalendar.event;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;

/**
 * This adapter class applies data to event_list_row layout.
 */

public class EventListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater layoutInflater;
    private List<Appointment> appointmentList;
    private String[] colours;

    public EventListAdapter(Activity activity, List<Appointment> appointmentList) {
        this.activity = activity;
        this.appointmentList = appointmentList;
        colours = activity.getApplicationContext().getResources().getStringArray(R.array.event_bg);
    }

    @Override
    public int getCount() {
        return appointmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return appointmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(layoutInflater == null) layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) convertView = layoutInflater.inflate(R.layout.event_list_row, null);

        TextView eventTime = (TextView) convertView.findViewById(R.id.event_time);
        TextView eventTitle = (TextView) convertView.findViewById(R.id.event_title);

        eventTime.setText(String.valueOf(appointmentList.get(position).getStartDate()));
        eventTitle.setText(appointmentList.get(position).getEvent());

        String color = colours[position % colours.length];
        eventTime.setBackgroundColor(Color.parseColor(color));

        return convertView;
    }
}
