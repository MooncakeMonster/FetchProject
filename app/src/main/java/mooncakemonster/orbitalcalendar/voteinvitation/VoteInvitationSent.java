package mooncakemonster.orbitalcalendar.voteinvitation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.notifications.NotificationItem;

/**
 * This class allows users to send a list of date
 * and time options to participants for voting an event.
 */
public class VoteInvitationSent extends ActionBarActivity {

    // List to get all the date options
    private ListView listView;
    private SelectAdapterSent adapter;
    private NotificationItem notificationItem;

    private Button reject_event;
    private TextView invite_sender, invite_title, invite_location, invite_notes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_invitation);

        // Initialise ArrayAdapter adapter for view
        View header = getLayoutInflater().inflate(R.layout.header_vote_invitation, null);
        listView = (ListView) findViewById(R.id.select_list);
        listView.addHeaderView(header);

        //(0) Instantiate layout
        invite_sender = (TextView) header.findViewById(R.id.invite_sender);
        invite_title = (TextView) header.findViewById(R.id.invite_title);
        invite_location = (TextView) header.findViewById(R.id.invite_location);
        invite_notes = (TextView) header.findViewById(R.id.invite_notes);

        getSupportActionBar().setElevation(0);

        //(1) Get intent that started this activity
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        notificationItem = (NotificationItem) bundle.getSerializable("vote_item");

        //(2) Extract data from notification
        if(notificationItem != null) {
            String notes = notificationItem.getSender_notes();

            invite_sender.setText(notificationItem.getSender_username());
            invite_title.setText(notificationItem.getSender_event());
            invite_location.setText(notificationItem.getSender_location());
            if(notes != null && !notes.isEmpty()) invite_notes.setText(notes);
            else invite_notes.setText("No notes");
        }

        //(3) Get voting options from sender
        adapter = new SelectAdapterSent(this, R.layout.row_selected_checkbox, new ArrayList<SelectItem>());
        listView.setAdapter(adapter);
        retrieveAllOptions(notificationItem);

        // Dialog edittext appears for user to state reason for rejection if they choose to reject voting
        reject_event = (Button) findViewById(R.id.reject_event);
        reject_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constant.alertUser(VoteInvitationSent.this, "Reject event", "Event is not rejected.");
            }
        });
    }

    // This method retrieves the voting options from sender.
    private void retrieveAllOptions(NotificationItem notificationItem) {
        String[] split_selected_option = notificationItem.getSelected_option().split(" ");
        String[] split_start_date = notificationItem.getStart_date().split(" ");
        String[] split_end_date = notificationItem.getEnd_date().split(" ");
        String[] split_start_time = notificationItem.getStart_time().split(" ");
        String[] split_end_time = notificationItem.getEnd_time().split(" ");

        boolean option = false;
        int size = split_start_date.length;

        for(int i = 0; i < size; i++) {
            if(split_selected_option[i].equals("true")) option = true;
            else option = false;
            adapter.add(new SelectItem(option, split_start_date[i], split_end_date[i],
                    split_start_time[i], split_end_time[i]));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cross, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_close) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
