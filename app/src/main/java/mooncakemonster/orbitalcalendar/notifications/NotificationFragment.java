package mooncakemonster.orbitalcalendar.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.cloudant.User;
import mooncakemonster.orbitalcalendar.voteinvitation.VoteInvitation;

public class NotificationFragment extends ListFragment {

    private static final String TAG = NotificationFragment.class.getSimpleName();
    private NotificationDatabase notificationDatabase;
    private List<NotificationItem> allNotifications;
    NotificationAdapter adapter;

    // Connect to cloudant
    UserDatabase db;
    CloudantConnect cloudantConnect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (cloudantConnect == null)
            this.cloudantConnect = new CloudantConnect(this.getActivity(), "user");

        // Retrieve own username
        db = new UserDatabase(getActivity().getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        notificationDatabase = new NotificationDatabase(getActivity());

        // (1) Retrieve latest voting request
        String my_username = user.get("username");
        Log.d(TAG, my_username);
        cloudantConnect.startPullReplication();
        User my_user = cloudantConnect.getTargetUser(my_username);

        if(cloudantConnect.checkVotingRequest(my_username)) {
            Log.d(TAG, "Voting request found");
            notificationDatabase.putInformation(notificationDatabase,
                    my_user.getOption_my_username(), " has requested you to vote for his/her event - ",
                    my_user.getOption_event_title(), ". Vote now!",  new Intent(getActivity(), VoteInvitation.class).toUri(0));

            // Reset document once data is saved in the phone
            cloudantConnect.resetVotingOptions(my_username);

        } else if(cloudantConnect.checkVotingResponse(my_username)) {
            Log.d(TAG, "Voting response found");
            notificationDatabase.putInformation(notificationDatabase,
                    my_user.getOption_my_username(), " has responded to your event - ",
                    my_user.getOption_event_title(), ". Checkout the current voting result now!", "");

            // Reset document once data is saved in the phone
            cloudantConnect.resetVotingResponse(my_username);

        } else {
            Log.d(TAG, "Nothing found");
        }

        // Get all notifications
        allNotifications = notificationDatabase.getAllNotifications(notificationDatabase);
        // Get adapter view
        adapter = new NotificationAdapter(getActivity(), R.layout.row_notifications, allNotifications);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final NotificationItem notificationItem = adapter.getItem(position);
                try {
                    startActivity(Intent.parseUri(notificationItem.getIntent(), 0));
                } catch (URISyntaxException e){
                    Log.e(TAG, "Error setting intent");
                }
            }
        });
    }
}
