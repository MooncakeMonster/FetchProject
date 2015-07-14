package mooncakemonster.orbitalcalendar.friendlist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;

public class FriendlistFragment extends ListFragment {

    private static final String TAG = FriendlistFragment.class.getSimpleName();
    private FriendDatabase friendDatabase;
    private List<FriendItem> allFriends;
    FriendlistAdapter adapter;
    ImageButton add_friends;

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

                alertBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: Get friend's image and replace with "redbear"
                        friendDatabase.putInformation(friendDatabase, "" + R.color.redbear, input_username.getText().toString());
                        adapter.clear();
                        adapter.addAll(friendDatabase.getAllFriendUsername(friendDatabase));
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                Dialog dialog = alertBuilder.create();
                dialog.show();
            }
        });

        // Update friend's username
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                final View dialogview = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edittext, null);
                final EditText input_username = (EditText) dialogview.findViewById(R.id.input_text);

                //Get FriendItem from ArrayAdapter
                final FriendItem friendItem = adapter.getItem(position);

                final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Update username");
                alert.setView(dialogview);
                final String previous_username = friendItem.getUsername();
                input_username.setText(previous_username);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Update latest username into SQLite database
                        friendDatabase.updateInformation(friendDatabase, friendItem.getImage(), previous_username, input_username.getText().toString());
                        adapter.clear();
                        adapter.addAll(friendDatabase.getAllFriendUsername(friendDatabase));
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
            }
        });

        // Delete friend's username
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {
                //Get FriendItem from ArrayAdapter
                final FriendItem friendItem = adapter.getItem(position);

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Delete friend");
                alert.setMessage("Are you sure you want to delete \"" + friendItem.getUsername() + "\"?");

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Delete from SQLite database
                        friendDatabase.deleteInformation(friendDatabase, friendItem.getImage(), friendItem.getUsername());
                        //Delete from ArrayAdapter & allFriends
                        adapter.remove(friendItem);
                        allFriends.remove(friendItem);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
}
