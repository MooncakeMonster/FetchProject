package mooncakemonster.orbitalcalendar.votereceive;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.alexvasilkov.foldablelayout.UnfoldableView;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.votesend.VotingDatabase;

public class VotingFragment extends ListFragment {

    //Set database to allow user to retrieve data
    private VotingDatabase votingDatabase;
    //List to get all the appointments
    private List<VoteItem> allVoteItem;
    //Set unfoldable effect
    private View listTouchInterceptor;
    private FrameLayout detailsLayout;
    private UnfoldableView unfoldableView;
    VotingAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        votingDatabase = new VotingDatabase(getActivity());
        allVoteItem = votingDatabase.getAllVotings(votingDatabase);
        //Initialise ArrayAdapter adapter for view
        adapter = new VotingAdapter(getActivity(), R.layout.row_vote_history, allVoteItem);
        setListAdapter(adapter);

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
                //Get Appointment from ArrayAdapter
                final VoteItem voteItem = adapter.getItem(position);

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Delete vote result");
                alert.setMessage("Are you sure you want to delete \"" + voteItem.getEvent_title() + "\"?");

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Delete from SQLite database
                        //appointmentDatabase.deleteAppointment(appointmentToDelete);
                        //Delete from ArrayAdapter & allAppointment
                        adapter.remove(voteItem);
                        allVoteItem.remove(voteItem);
                        adapter.notifyDataSetChanged();
                        //Remove dialog after execution of the above
                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                alert.show();
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_voting, container, false);

        listTouchInterceptor = rootView.findViewById(R.id.touch_interceptor_view);
        listTouchInterceptor.setClickable(false);

        detailsLayout = (FrameLayout) rootView.findViewById(R.id.details_layout);
        detailsLayout.setVisibility(View.INVISIBLE);

        unfoldableView = (UnfoldableView) rootView.findViewById(R.id.unfoldable_view);

        unfoldableView.setOnFoldingListener(new UnfoldableView.OnFoldingListener() {
            @Override
            public void onUnfolding(UnfoldableView unfoldableView) {
                listTouchInterceptor.setClickable(true);
                detailsLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onUnfolded(UnfoldableView unfoldableView) {
                listTouchInterceptor.setClickable(false);
            }

            @Override
            public void onFoldingBack(UnfoldableView unfoldableView) {
                listTouchInterceptor.setClickable(true);
            }

            @Override
            public void onFoldedBack(UnfoldableView unfoldableView) {
                listTouchInterceptor.setClickable(false);
                detailsLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFoldProgress(UnfoldableView unfoldableView, float v) {

            }
        });


        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //super.onListItemClick(l, v, position, id);
        //unfoldableView.unfold(v, detailsLayout);
    }

}