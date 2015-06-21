package mooncakemonster.orbitalcalendar.votereceive;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexvasilkov.android.commons.utils.Views;
import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.alexvasilkov.foldablelayout.shading.GlanceFoldShading;
import com.squareup.picasso.Picasso;

import mooncakemonster.orbitalcalendar.R;

public class VotingFragment extends ListFragment {

    private VotingAdapter adapter;
    //Set unfoldable effect
    private View listTouchInterceptor;
    private View detailsLayout;
    private View rootView;
    private UnfoldableView unfoldableView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_voting, container, false);

        adapter = new VotingAdapter(getActivity());
        setListAdapter(adapter);

        listTouchInterceptor = rootView.findViewById(R.id.touch_interceptor_view);
        listTouchInterceptor.setClickable(false);

        detailsLayout = rootView.findViewById(R.id.details_layout);
        detailsLayout.setVisibility(View.INVISIBLE);

        unfoldableView = (UnfoldableView) rootView.findViewById(R.id.unfoldable_view);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.unfold_glance);
        unfoldableView.setFoldShading(new GlanceFoldShading(getActivity(), bitmap));


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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView image = Views.find(detailsLayout, R.id.details_image);
                TextView title = Views.find(detailsLayout, R.id.details_title);
                TextView location = Views.find(detailsLayout, R.id.details_location);

                Picasso.with(getActivity()).load(adapter.getItem(position).getImageId()).into(image);
                title.setText(adapter.getItem(position).getEvent_title());
                location.setText(adapter.getItem(position).getEvent_location());

                unfoldableView.unfold(parent, detailsLayout);
            }
        });
    }
}
