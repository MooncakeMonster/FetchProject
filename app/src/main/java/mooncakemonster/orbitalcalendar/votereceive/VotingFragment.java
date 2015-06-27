package mooncakemonster.orbitalcalendar.votereceive;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.alexvasilkov.android.commons.utils.Views;
import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.alexvasilkov.foldablelayout.shading.GlanceFoldShading;

import mooncakemonster.orbitalcalendar.R;

public class VotingFragment extends BaseFragment {

    VotingAdapter votingAdapter;
    //Set unfoldable effect
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
        votingAdapter = new VotingAdapter(getActivity(), this);
        setListAdapter(votingAdapter);

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final VoteItem voteItem = votingAdapter.getItem(position);

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Delete voting result");
                alert.setMessage("Are you sure you want to delete \"" + voteItem.getEvent_title() + "\"'s voting result?");

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Delete from SQLite database TODO
                        //Delete from ArrayAdapter & allAppointment
                        votingAdapter.notifyDataSetChanged();
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

    public void openDetails(View coverview, VoteItem voteItem) {
        TextView title = Views.find(detailsLayout, R.id.details_title);
        TextView location = Views.find(detailsLayout, R.id.details_location);

        title.setText(voteItem.getEvent_title());
        location.setText(voteItem.getEvent_location());

        unfoldableView.unfold(coverview, detailsLayout);
    }

}
