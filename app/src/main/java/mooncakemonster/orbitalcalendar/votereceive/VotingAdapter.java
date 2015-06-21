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

    VotingDatabase votingDatabase = new VotingDatabase(getContext());

    public VotingAdapter(Context context) {
        super(context);
        setItemsList(votingDatabase.getAllVotings(votingDatabase));
    }

    @Override
    public void onClick(View v) {

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
        view.setTag(holder);

        return view;
    }

    @Override
    protected void bindView(VoteItem voteItem, int i, View view) {
        Holder holder = (Holder) view.getTag();

        holder.event_image.setTag(voteItem);
        Picasso.with(view.getContext());
        holder.event_title.setText(voteItem.getEvent_title());
        holder.event_location.setText(voteItem.getEvent_location());
        holder.event_start_end_date.setText(voteItem.getEvent_start_date() + " - " + voteItem.getEvent_end_date());
        holder.event_start_end_time.setText(voteItem.getEvent_start_time() + " - " + voteItem.getEvent_end_time());
    }
}
