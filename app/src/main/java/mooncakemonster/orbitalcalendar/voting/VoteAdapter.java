package mooncakemonster.orbitalcalendar.voting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Constant;

public class VoteAdapter extends ArrayAdapter<VoteItem> {

    private List<VoteItem> objects;

    public VoteAdapter(Context context, int resources, List<VoteItem> objects) {
        super(context, resources, objects);
        this.objects = objects;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    static class Holder {
        Button vote_start_date, vote_end_date;
        Button vote_start_time, vote_end_time;
        Button remove_option;      // remove from list
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
        holder.vote_start_date = (Button) row.findViewById(R.id.option_start_date);
        holder.vote_end_date = (Button) row.findViewById(R.id.option_end_date);
        holder.vote_start_time = (Button) row.findViewById(R.id.option_start_time);
        holder.vote_end_time = (Button) row.findViewById(R.id.option_end_time);
        holder.remove_option = (Button) row.findViewById(R.id.remove_option);

        holder.vote_start_date.setText(voteItem.getEvent_start_date());
        holder.vote_end_date.setText(voteItem.getEvent_end_date());
        holder.vote_start_time.setText(voteItem.getEvent_start_time());
        holder.vote_end_time.setText(voteItem.getEvent_end_time());

        // Date and time picker
        Constant.setButtonDatePicker(getContext(), holder.vote_start_date, 0, "");
        Constant.setButtonDatePicker(getContext(), holder.vote_end_date, 0, "");
        Constant.setButtonTimePicker(getContext(), holder.vote_start_time, 0, "    ");
        Constant.setButtonTimePicker(getContext(), holder.vote_end_time, 0,   "    ");

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
