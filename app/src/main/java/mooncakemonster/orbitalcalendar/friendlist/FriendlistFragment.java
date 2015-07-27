package mooncakemonster.orbitalcalendar.friendlist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.baoyz.widget.PullRefreshLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.database.Constant;

public class FriendlistFragment extends ListFragment implements PullRefreshLayout.OnRefreshListener {

    private static final String TAG = FriendlistFragment.class.getSimpleName();
    private FriendDatabase friendDatabase;
    private UserDatabase db;
    private ProgressDialog progressDialog;
    private List<FriendItem> allFriends;
    FriendlistAdapter adapter;
    PullRefreshLayout swipeRefreshLayout;
    ImageButton add_friends;
    CloudantConnect cloudantConnect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get all friends' username
        friendDatabase = new FriendDatabase(getActivity());
        allFriends = friendDatabase.getAllFriendUsername(friendDatabase);
        // Get adapter view
        adapter = new FriendlistAdapter(getActivity(), R.layout.row_friendlist, allFriends);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_friendlist, container, false);

        swipeRefreshLayout = (PullRefreshLayout) rootView.findViewById(R.id.swipe_refresh_friendlist);
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

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        add_friends = (ImageButton) view.findViewById(R.id.addFriendButton);
        add_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View dialogview = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edittext, null);
                final EditText input_username = (EditText) dialogview.findViewById(R.id.input_text);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setTitle("Enter your friend's username:");
                alertBuilder.setView(dialogview);

                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(false);

                alertBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        final String username = input_username.getText().toString();

                        // Send friend request
                        if (cloudantConnect == null) cloudantConnect = new CloudantConnect(getActivity(), "user");
                        // Check if friend username exist first
                        if (cloudantConnect.checkExistingItems("username", username)) {
                            progressDialog.setMessage("Sending friend request...");
                            showDialog();

                            adapter.clear();
                            adapter.addAll(friendDatabase.getAllFriendUsername(friendDatabase));
                            adapter.notifyDataSetChanged();

                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    db = new UserDatabase(getActivity());
                                    HashMap<String, String> user = db.getUserDetails();
                                    final String my_username = user.get("username");

                                    friendDatabase = new FriendDatabase(getActivity());
                                    friendDatabase.putInformation(friendDatabase, "false", Constant.retrieveCurrentTime(), my_username);

                                    cloudantConnect.sendFriendRequest(my_username, username);
                                    cloudantConnect.startPushReplication();

                                    //Toast.makeText(getActivity(), "Friend request sent successfully", Toast.LENGTH_SHORT).show();
                                    hideDialog();
                                }
                            }, 1500);
                        } else {
                            Constant.alertUser(getActivity(), "Invalid username!", "The username you have entered is invalid. Please try again.");
                        }
                        //Constant.alertUser(getActivity(), "Friend request sent", "You will be notified when \"" + username + "\" has accepted your friend request.");
                }
            }).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                @Override
                public void onClick (DialogInterface dialog,int which){
                    dialog.dismiss();
                }
            });

            Dialog dialog = alertBuilder.create();
            dialog.show();
        }
    }

    );

    // Delete friend's username
    getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick (AdapterView < ? > arg0, View view,int position, long id){
                    //Get FriendItem from ArrayAdapter
                    final FriendItem friendItem = adapter.getItem(position);

                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("Delete friend");
                    alert.setMessage("Are you sure you want to delete \"" + friendItem.getUsername() + "\" from your friend list?\n\n" +
                            "Note that you will not be able to send or receive vote request from \"" + friendItem.getUsername() + "\" anymore.");

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //Delete from SQLite database
                            friendDatabase.deleteInformation(friendDatabase, friendItem.getUsername());
                            //Delete from ArrayAdapter & allFriends
                            adapter.remove(friendItem);
                            allFriends.remove(friendItem);
                            adapter.notifyDataSetChanged();

                            // Remove user from friend's list as well to prevent user from sending vote request
                            if (cloudantConnect == null)
                                cloudantConnect = new CloudantConnect(getActivity(), "user");
                            db = new UserDatabase(getActivity());
                            HashMap<String, String> user = db.getUserDetails();
                            String my_username = user.get("username");
                            cloudantConnect.sendFriendRemoved(my_username, friendItem.getUsername());
                            cloudantConnect.startPushReplication();

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
                }
         });
}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    // This method retrieves the latest notifications from the database
    private void retrieveNotifications() {
        swipeRefreshLayout.setRefreshing(true);
        adapter.clear();
        adapter.addAll(friendDatabase.getAllFriendUsername(friendDatabase));
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    // This method shows progress dialog when not showing
    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    // This method dismiss progress dialog when required (dialog must be showing)
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onRefresh() {
        retrieveNotifications();
    }
}
