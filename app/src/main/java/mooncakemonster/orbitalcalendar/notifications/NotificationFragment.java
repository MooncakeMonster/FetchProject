package mooncakemonster.orbitalcalendar.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        // TODO: Find out a way to pull data from Cloudant and store in phone in real time
        cloudantConnect.startPullReplication();
        User my_user = cloudantConnect.getTargetUser(my_username);

        if(cloudantConnect.checkVotingRequest(my_username)) {
            Log.d(TAG, "Voting request found");
            notificationDatabase.putInformation(notificationDatabase,
                    my_user.getOption_my_username(), " has requested you to vote for the event - ",
                    my_user.getOption_event_title(), ", vote now!", my_user.getOption_event_location(),
                    my_user.getOption_event_notes(), "vote_request", my_user.getOption_start_date(), my_user.getOption_end_date(),
                    my_user.getOption_start_time(), my_user.getOption_end_time(), new Intent(getActivity(), VoteInvitation.class).toUri(0));

            // Reset document once data is saved in the phone
            cloudantConnect.resetVotingOptions(my_user);
            cloudantConnect.startPushReplication();

        } else if(cloudantConnect.checkVotingResponse(my_username)) {
            Log.d(TAG, "Voting response found");
            String action1, action2;


            // case 1: Target participant has chosen the dates they can make it
            // case 2: Target participant has rejected the event
            if(my_user.getSelected_start_date() != null) {
                action1 =  " has responded to your event - ";
                action2 = ", checkout the current voting result now!";
            } else {
                action1 = " has rejected your event - ";
                action2 = ", find out why!";
            }
            notificationDatabase.putInformation(notificationDatabase,
                    my_user.getOption_my_username(), action1,
                    my_user.getOption_event_title(), action2, my_user.getSelected_event_location(),
                    my_user.getSelected_event_notes(), "vote_response", my_user.getSelected_start_date(), my_user.getOption_end_date(),
                    my_user.getSelected_start_time(), my_user.getOption_end_time(), "");

            // Reset document once data is saved in the phone
            cloudantConnect.resetVotingResponse(my_user);
            cloudantConnect.startPushReplication();
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
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }
}
