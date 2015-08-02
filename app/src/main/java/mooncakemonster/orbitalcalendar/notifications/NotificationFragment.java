package mooncakemonster.orbitalcalendar.notifications;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.widget.PullRefreshLayout;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;

public class NotificationFragment extends ListFragment implements PullRefreshLayout.OnRefreshListener {

    private static final String TAG = NotificationFragment.class.getSimpleName();
    private NotificationDatabase notificationDatabase;
    private List<NotificationItem> allNotifications;
    private NotificationAdapter adapter;
    private PullRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fresco.initialize(getActivity());
        notificationDatabase = new NotificationDatabase(getActivity());
        // Get all notifications
        allNotifications = notificationDatabase.getAllNotifications(notificationDatabase);
        // Get adapter view
        adapter = new NotificationAdapter(getActivity(), R.layout.row_notifications, allNotifications);
        adapter.clear();
        adapter.addAll(notificationDatabase.getAllNotifications(notificationDatabase));
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);

        swipeRefreshLayout = (PullRefreshLayout) rootView.findViewById(R.id.swipe_refresh_notification);
        swipeRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                retrieveNotifications();
            }
        }, 3000);

        return rootView;
    }

    // This method retrieves the latest notifications from the database
    private void retrieveNotifications() {
        swipeRefreshLayout.setRefreshing(true);
        adapter.clear();
        adapter.addAll(notificationDatabase.getAllNotifications(notificationDatabase));
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onRefresh() {
        retrieveNotifications();
    }
}
