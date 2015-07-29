package mooncakemonster.orbitalcalendar.friendlist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.friendsfeed.FriendsFeedActivity;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class FriendlistFragment extends Fragment {

    private static final String TAG = FriendlistFragment.class.getSimpleName();
    private FriendDatabase friendDatabase;
    private UserDatabase db;
    private ProgressDialog progressDialog;
    private List<FriendItem> allFriends;
    private StickyListHeadersListView listview;
    private TextView friendlist_empty;
    private FriendlistAdapter adapter;
    private ImageButton add_friends;
    private CloudantConnect cloudantConnect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_friendlist, container, false);

        Fresco.initialize(getActivity());
        // Get all friends' username
        friendDatabase = new FriendDatabase(getActivity());
        allFriends = friendDatabase.getAllFriendUsername(friendDatabase);
        // Get adapter view
        adapter = new FriendlistAdapter(getActivity(), R.layout.row_friendlist, allFriends);
        listview = (StickyListHeadersListView) rootView.findViewById(R.id.friend_list);
        listview.setAdapter(adapter);

        friendlist_empty = (TextView) rootView.findViewById(R.id.friendlist_empty);
        if(allFriends.size() > 0) friendlist_empty.setVisibility(View.INVISIBLE);

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

                        final String editText_username = input_username.getText().toString();

                        // Send friend request
                        if (cloudantConnect == null) cloudantConnect = new CloudantConnect(getActivity(), "user");
                        // Check if friend username exist first
                        if (cloudantConnect.checkExistingItems("username", editText_username)) {
                            progressDialog.setMessage("Sending friend request...");
                            showDialog();

                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    db = new UserDatabase(getActivity());
                                    HashMap<String, String> user = db.getUserDetails();
                                    final String my_username = user.get("username");

                                    friendDatabase = new FriendDatabase(getActivity());
                                    friendDatabase.putInformation(friendDatabase, "false", Constant.retrieveCurrentTime(), editText_username);

                                    cloudantConnect.sendFriendRequest(my_username, editText_username);
                                    cloudantConnect.startPushReplication();

                                    /*
                                    adapter.clear();
                                    adapter.addAll(friendDatabase.getAllFriendUsername(friendDatabase));
                                    adapter.notifyDataSetChanged();
                                    */

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
    });

    // Opens the notifications received from the target friend
    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Get FriendItem from ArrayAdapter
            final FriendItem friendItem = adapter.getItem(position);

            Bundle bundle = new Bundle();
            bundle.putSerializable("friend_item", friendItem);
            Intent intent = new Intent(getActivity(), FriendsFeedActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    });



    // Delete friend's username
    listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
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

            return true;
        }
    });
}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

}
