package mooncakemonster.orbitalcalendar.votereceive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

import java.util.Collections;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.votesend.VoteItem;
import mooncakemonster.orbitalcalendar.votesend.VotingDatabase;

public class VotingFragment extends ListFragment {

    // SQLite database
    private VotingDatabase votingDatabase;

    // Adapter for listing out the event votes
    private VotingAdapter votingAdapter;
    private List<VoteItem> list;

    private ImageButton sort_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_voting, container, false);

        votingDatabase = new VotingDatabase(getActivity());
        List<VoteItem> arrangeVotingList = combineList(votingDatabase.getNotConfirmedVotings(votingDatabase), votingDatabase.getConfirmedVotings(votingDatabase));
        votingAdapter = new VotingAdapter(getActivity(), R.layout.row_vote_history, arrangeVotingList);
        setListAdapter(new SlideExpandableListAdapter(votingAdapter, R.id.history_layout, R.id.expandable_vote));

        sort_button = (ImageButton) rootView.findViewById(R.id.sort_button);
        sort_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] sort_type = {"Sort by event name", "Highest vote on top", "Recent date on top", "Confirmed events on top", "Confirmed events below"};
                list = votingDatabase.getAllVotings(votingDatabase);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setTitle("Sort Voting Result").setSingleChoiceItems(sort_type, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // Sort according to event name
                                Collections.sort(list, VoteItem.eventNameComparator);
                                votingAdapter.clear();
                                votingAdapter.addAll(list);
                                votingAdapter.notifyDataSetChanged();
                                break;
                            case 1:
                                // (1) Sort according to total votes
                                Collections.sort(list, VoteItem.totalComparator);
                                votingAdapter.clear();
                                votingAdapter.addAll(list);
                                votingAdapter.notifyDataSetChanged();
                                break;
                            case 2:
                                // (2) Sort according to date and time
                                Collections.sort(list, VoteItem.dateComparator);
                                votingAdapter.clear();
                                votingAdapter.addAll(list);
                                votingAdapter.notifyDataSetChanged();
                                break;
                            case 3:
                                // (3) Sort according to confirmed event (top)
                                list = combineList(votingDatabase.getConfirmedVotings(votingDatabase), votingDatabase.getNotConfirmedVotings(votingDatabase));
                                votingAdapter.clear();
                                votingAdapter.addAll(list);
                                votingAdapter.notifyDataSetChanged();
                                break;
                            case 4:
                                // (4) Sort according to confirmed event (below)
                                list = combineList(votingDatabase.getNotConfirmedVotings(votingDatabase), votingDatabase.getConfirmedVotings(votingDatabase));
                                votingAdapter.clear();
                                votingAdapter.addAll(list);
                                votingAdapter.notifyDataSetChanged();
                                break;
                        }
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", null);

                Dialog dialog = alertBuilder.create();
                dialog.show();
            }
        });

        return rootView;
    }

    private List<VoteItem> combineList(List<VoteItem> notConfirmed, List<VoteItem> confirmed) {
        int size = confirmed.size();

        for(int i = 0; i < size; i++) {
            notConfirmed.add(confirmed.get(i));
        }
        return notConfirmed;
    }
}
