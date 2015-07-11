package mooncakemonster.orbitalcalendar.votereceive;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.voteinvitation.VoteOptionItem;

/**
 * Created by BAOJUN on 20/6/15.
 */

public class VotingAdapter extends ArrayAdapter<VoteOptionItem> {

    List<VoteOptionItem> objects;

    public VotingAdapter(Context context, int resource, List<VoteOptionItem> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    static class Holder {
        ImageView event_image;
        TextView event_title;
        TextView event_location;
        TextView event_start_end_date;
        TextView event_start_end_time;
        Button view_result, send_reminder, fetch_help;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater;
        Holder holder;

        if(row == null) {
            inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_vote_history, parent, false);
        }

        final VoteOptionItem voteItem = objects.get(position);

        if(voteItem != null) {
            holder = new Holder();

            holder.event_image = (ImageView) row.findViewById(R.id.history_image);
            holder.event_title = (TextView) row.findViewById(R.id.history_title);
            holder.event_location = (TextView) row.findViewById(R.id.history_location);
            holder.event_start_end_date = (TextView) row.findViewById(R.id.history_start_end_date);
            holder.event_start_end_time = (TextView) row.findViewById(R.id.history_start_end_time);

            // Child
            holder.view_result = (Button) row.findViewById(R.id.expand_view_result);
            holder.send_reminder = (Button) row.findViewById(R.id.expand_send_reminder);
            holder.fetch_help = (Button) row.findViewById(R.id.expand_fetch_help);

            //Picasso.with(getContext()).load(getBackgroundResource(voteItem)).fit().noFade().into(holder.event_image);
            holder.event_image.setBackgroundResource(R.color.redbear);
            holder.event_title.setText(voteItem.getEvent_title());
            holder.event_location.setText("@" + voteItem.getEvent_location());

            // TODO Update if statement when possible
            // Only show final confirmed date, else show number of votes response received
            if(false) {
                holder.event_start_end_date.setText(voteItem.getEvent_start_date() + " - " + voteItem.getEvent_end_date());
                holder.event_start_end_time.setText(voteItem.getEvent_start_time() + " - " + voteItem.getEvent_end_time());
            } else {
                holder.event_start_end_date.setText("Votes received: 3");
                holder.event_start_end_time.setText("No responses yet: 7");
            }

            final View view = row;
            // Create bundle
            final Bundle bundle = new Bundle();
            bundle.putSerializable("voteItem", voteItem);

            holder.view_result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), VotingResultActivity.class);
                    intent.putExtras(bundle);
                    view.getContext().startActivity(intent);
                }
            });

            holder.send_reminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            holder.fetch_help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            row.setTag(holder);
        }

        return row;
    }

    private int getBackgroundResource(VoteOptionItem voteItem) {
        switch (voteItem.getImageId()) {
            case R.color.redbear:
                return R.drawable.backgroundred;
            case R.color.yellowbear:
                return R.drawable.backgroundyellow;
            case R.color.greenbear:
                return R.drawable.backgroundgreen;
            case R.color.bluebear:
                return R.drawable.backgroundblue;
            case R.color.purplebear:
                return R.drawable.backgroundpurple;
        }

        // Should not reach here
        return -1;
    }

}
