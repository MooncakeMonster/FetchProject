package mooncakemonster.orbitalcalendar.votereceive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexvasilkov.android.commons.adapters.ItemsAdapter;
import com.alexvasilkov.android.commons.utils.Views;
import com.squareup.picasso.Picasso;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.votesend.VotingDatabase;

/**
 * Created by BAOJUN on 20/6/15.
 */

public class VotingAdapter extends ItemsAdapter<VoteItem> implements View.OnClickListener {

    VotingFragment votingFragment;
    VotingDatabase votingDatabase = new VotingDatabase(getContext());

    public VotingAdapter(Context context, VotingFragment votingFragment) {
        super(context);
        this.votingFragment = votingFragment;
        setItemsList(votingDatabase.getAllVotings(votingDatabase));
    }

    @Override
    public void onClick(View v) {
        votingFragment.openDetails(v, (VoteItem) v.getTag());
    }

    static class Holder {
        ImageView event_image;
        TextView event_title;
        TextView event_location;
        TextView event_start_end_date;
        TextView event_start_end_time;
    }

    @Override
    protected View createView(VoteItem voteItem, int i, ViewGroup viewGroup, LayoutInflater layoutInflater) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_vote_history, viewGroup, false);
        Holder holder = new Holder();

        holder.event_image = Views.find(view, R.id.history_image);
        holder.event_title = Views.find(view, R.id.history_title);
        holder.event_location = Views.find(view, R.id.history_location);
        holder.event_start_end_date = Views.find(view, R.id.history_start_end_date);
        holder.event_start_end_time = Views.find(view, R.id.history_start_end_time);

        holder.event_image.setOnClickListener(this);
        view.setTag(holder);

        return view;
    }

    @Override
    protected void bindView(VoteItem voteItem, int i, View view) {
        Holder holder = (Holder) view.getTag();

        holder.event_image.setTag(voteItem);
        Picasso.with(view.getContext()).load(getBackgroundResource(voteItem)).fit().noFade().into(holder.event_image);
        holder.event_image.setBackgroundResource(voteItem.getImageId());
        holder.event_title.setText(voteItem.getEvent_title());
        holder.event_location.setText(voteItem.getEvent_location());

        // TODO Update if statement when possible
        // Only show final confirmed date, else show number of votes response received
        if(false) {
            holder.event_start_end_date.setText(voteItem.getEvent_start_date() + " - " + voteItem.getEvent_end_date());
            holder.event_start_end_time.setText(voteItem.getEvent_start_time() + " - " + voteItem.getEvent_end_time());
        } else {
            holder.event_start_end_date.setText("Votes received: 3");
            holder.event_start_end_time.setText("No responses yet: 7");
        }
    }

    private int getBackgroundResource(VoteItem voteItem) {
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
