package mooncakemonster.orbitalcalendar.votereceive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;

/**
 * Created by BAOJUN on 20/6/15.
 */
public class VotingAdapter extends ArrayAdapter<VoteItem> {

    private List<VoteItem> objects;

    public VotingAdapter(Context context, int resources, List<VoteItem> objects) {
        super(context, resources, objects);
        this.objects = objects;
    }

    static class Holder {
        TextView event_title;
        TextView event_location;
        TextView event_start_end_date;
        TextView event_start_end_time;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater;
        View row = convertView;
        Holder holder;

        if (convertView == null) {
            inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_vote_history, null);
        }

        final VoteItem voteItem = objects.get(position);

        if (voteItem != null) {
            holder = new Holder();
            holder.event_title = (TextView) row.findViewById(R.id.history_title);
            holder.event_location = (TextView) row.findViewById(R.id.history_location);
            holder.event_start_end_date = (TextView) row.findViewById(R.id.history_start_end_date);
            holder.event_start_end_time = (TextView) row.findViewById(R.id.history_start_end_time);

            holder.event_title.setText(voteItem.getEvent_title());
            holder.event_location.setText(voteItem.getEvent_location());
            holder.event_start_end_date.setText(voteItem.getEvent_start_date());
            holder.event_start_end_time.setText(voteItem.getEvent_start_time());

            row.setTag(holder);
        }

        return row;
    }
}
