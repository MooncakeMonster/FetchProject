package mooncakemonster.orbitalcalendar.friendlist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;

public class FriendlistFragment extends ListFragment {

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

        add_friends = (ImageButton) rootView.findViewById(R.id.addFriendButton);

        add_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.edittext_dialog, null);
                final EditText input_username = (EditText) view.findViewById(R.id.input_friend_username);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setTitle("Enter your friend's username:");
                alertBuilder.setView(view);

                alertBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: Get friend's image and replace with "-1"
                        friendDatabase.putInformation(friendDatabase, -1, input_username.getText().toString());
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

        return rootView;
    }
}
