package mooncakemonster.orbitalcalendar.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;

public class EventDayAdapter extends ArrayAdapter{
    private List list = new ArrayList();

    public EventDayAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(EventClass object) {
        list.add(object);
        super.add(object);
    }

    static class ImgHolder {
        ImageView IMG;
        TextView NAME;
        TextView TIME;
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
        ImgHolder holder;

        if(convertView == null) {
            LayoutInflater inflator = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflator.inflate(R.layout.row_layout, parent, false);
            holder = new ImgHolder();

            holder.IMG = (ImageView) row.findViewById(R.id.eventIcon);
            holder.NAME = (TextView) row.findViewById(R.id.eventName);
            holder.TIME = (TextView) row.findViewById(R.id.eventTime);

            row.setTag(holder);
        }

        else holder = (ImgHolder) row.getTag();

        EventClass event = (EventClass) getItem(position);
        holder.IMG.setImageResource(event.getEvent_Resource());
        holder.NAME.setText(event.getEvent_name());
        holder.TIME.setText(event.getEvent_time());

        return row;
    }
}
