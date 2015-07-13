package mooncakemonster.orbitalcalendar.notifications;

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

        /*
         * case 1: Target participants receive voting request
         * case 2: Sender received voting response from target participants (either voted, or rejected voting)
         * case 3: Target participants received confirmed date and time of an event
         * case 4: Target participants gets reminder to vote for an event
         */

        if(my_user.getOption_event_title() != null) {

            Log.d(TAG, "Voting request found");
            notificationDatabase.putInformation(notificationDatabase, 1, my_user.getOption_event_id(), my_user.getOption_event_colour(),
                    my_user.getOption_my_username(), " has requested you to vote for the event - ",
                    my_user.getOption_event_title(), ", vote now!", my_user.getOption_event_location(),
                    my_user.getOption_event_notes(), my_user.getOption_start_date(), my_user.getOption_end_date(),
                    my_user.getOption_start_time(), my_user.getOption_end_time());

        } if(my_user.getSelected_event_title() != null) {

            Log.d(TAG, "Voting response found");
            int notification_id;
            String action1, action2;

            // case 1: Target participant has chosen the dates they can make it
            // case 2: Target participant has rejected the event
            if(my_user.getReject_reason() != null) {
                notification_id = 2;
                action1 = " has responded to your event - ";
                action2 = ", checkout the current voting result now!";
            } else {
                notification_id = 3;
                action1 = " has rejected your event - ";
                action2 = ", find out why!";
            }

            String event_id = "" + my_user.getSelected_event_id();
            String voted_participant = my_user.getSelected_my_username();

            notificationDatabase.putInformation(notificationDatabase, notification_id, Integer.parseInt(event_id), my_user.getSelected_event_colour(),
                    voted_participant, action1, my_user.getSelected_event_title(), action2, my_user.getSelected_event_location(),
                    my_user.getSelected_event_notes(), my_user.getSelected_start_date(), my_user.getSelected_end_date(),
                    my_user.getSelected_start_time(), my_user.getSelected_end_time());

        } if(my_user.getConfirm_event_title() != null) {

            Log.d(TAG, "Voting confirmation found");
            String start_date = my_user.getConfirm_start_date();
            String end_date = my_user.getConfirm_end_date();
            String start_time = my_user.getConfirm_start_time();
            String end_time = my_user.getConfirm_end_time();

            notificationDatabase.putInformation(notificationDatabase, 4, my_user.getConfirm_event_id(), my_user.getConfirm_event_colour(),
                    my_user.getConfirm_my_username(), " has confirmed the event - ", my_user.getConfirm_event_title(),
                    " to be from " + start_date + " to " + end_date + ", " + start_time + " - " + end_time, null, null,
                    start_date, end_date, start_time, end_time);

            //cloudantConnect.resetVotingConfirmation(my_user);
            cloudantConnect.startPushReplication();

        } if(my_user.getReminder_event_title() != null) {

            Log.d(TAG, "Voting reminder found");
            notificationDatabase.putInformation(notificationDatabase, 5, my_user.getReminder_event_id(), my_user.getReminder_event_colour(),
                    my_user.getReminder_my_username(), " reminds you to vote for the event - ", my_user.getConfirm_event_title(),
                    ", vote now!", null, null, null, null, null, null);

            //cloudantConnect.resetVotingReminder(my_user);
            cloudantConnect.startPushReplication();

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
