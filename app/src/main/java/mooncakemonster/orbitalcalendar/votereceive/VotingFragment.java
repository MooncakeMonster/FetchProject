package mooncakemonster.orbitalcalendar.votereceive;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.alexvasilkov.android.commons.utils.Views;
import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.alexvasilkov.foldablelayout.shading.GlanceFoldShading;

import java.util.HashMap;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.cloudant.User;
import mooncakemonster.orbitalcalendar.voteinvitation.VoteOptionItem;

public class VotingFragment extends BaseFragment {

    // Connect to cloudant database
    CloudantConnect cloudantConnect;
    UserDatabase db;

    // SQLite database
    ResultDatabase resultDatabase;

    // Adapter for listing out the event votes
    VotingAdapter votingAdapter;
    // Set unfoldable effect
    private View listTouchInterceptor;
    private View detailsLayout;
    private UnfoldableView unfoldableView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_voting, container, false);

        resultDatabase = new ResultDatabase(getActivity());

        votingAdapter = new VotingAdapter(getActivity(), this);
        setListAdapter(votingAdapter);

        if (cloudantConnect == null)
            this.cloudantConnect = new CloudantConnect(getActivity(), "user");

        listTouchInterceptor = rootView.findViewById(R.id.touch_interceptor_view);
        listTouchInterceptor.setClickable(false);

        detailsLayout = rootView.findViewById(R.id.details_layout);
        detailsLayout.setVisibility(View.INVISIBLE);

        unfoldableView = (UnfoldableView) rootView.findViewById(R.id.unfoldable_view);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.unfold_glance);
        unfoldableView.setFoldShading(new GlanceFoldShading(getActivity(), bitmap));

        unfoldableView.setOnFoldingListener(new UnfoldableView.SimpleFoldingListener() {
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
        });

        return rootView;

    }

    public void openDetails(View coverview, VoteOptionItem voteItem) {
        // (1) Retrieve title and location of event
        TextView title = Views.find(detailsLayout, R.id.details_title);
        TextView location = Views.find(detailsLayout, R.id.details_location);
        title.setText(voteItem.getEvent_title());
        location.setText("@ " + voteItem.getEvent_location());

        // (2) Collate all dates selected by target participants
        ListView result_listview = Views.find(detailsLayout, R.id.vote_result_list);
        saveDateSelectedByTargetParticipants();
        Log.d("ResultDatabase", "run method");
        ResultAdapter resultAdapter = new ResultAdapter(getActivity(), R.layout.row_vote_result,
                resultDatabase.getAllTargetResults(resultDatabase, voteItem.getEventId()));
        result_listview.setAdapter(resultAdapter);
        resultAdapter.notifyDataSetChanged();

        unfoldableView.unfold(coverview, detailsLayout);
    }

    // This method retrieves the selected dates from target participants via Cloudant database.
    private void saveDateSelectedByTargetParticipants() {
        // Retrieve the user's document from Cloudant
        cloudantConnect.startPullReplication();
        db = new UserDatabase(getActivity().getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        String my_username = user.get("username");
        User my_user = cloudantConnect.getTargetUser(my_username);

        if(cloudantConnect.checkVotingResponse(my_user)) {
            // Store participants according to date and time selected into SQLite
            resultDatabase.storeParticipants(resultDatabase, new ResultItem("" + my_user.getSelected_event_id(),
                    my_user.getSelected_start_date(), my_user.getSelected_end_date(),
                    my_user.getSelected_start_time(), my_user.getSelected_end_time(), "",
                    my_user.getSelected_my_username(), ""));

            // Reset document once data is saved in the phone
            // TODO: Remove comment!
            //cloudantConnect.resetVotingResponse(my_user);
            cloudantConnect.startPushReplication();
        }
    }
}
