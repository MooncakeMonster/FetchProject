package mooncakemonster.orbitalcalendar.votereceive;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.votesend.VotingDatabase;

public class VotingFragment extends ListFragment {

    // SQLite database
    VotingDatabase votingDatabase;

    // Adapter for listing out the event votes
    VotingAdapter votingAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_voting, container, false);

        votingDatabase = new VotingDatabase(getActivity());
        votingAdapter = new VotingAdapter(getActivity(), R.layout.row_vote_history, votingDatabase.getAllVotings(votingDatabase));
        setListAdapter(new SlideExpandableListAdapter(votingAdapter, R.id.history_layout, R.id.expandable_vote));

        return rootView;

    }
}
