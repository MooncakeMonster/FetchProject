package mooncakemonster.orbitalcalendar.voteinvitation;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.notifications.NotificationDatabase;
import mooncakemonster.orbitalcalendar.notifications.NotificationItem;

/**
 * This class allows users to send a list of date
 * and time options to participants for voting an event.
 */
public class VoteInvitation extends ActionBarActivity {

    // Connect to cloudant database
    CloudantConnect cloudantConnect;

    // List to get all the appointments
    private ListView listView;
    VoteInvitationAdapter adapter;

    // Retrieve username from SQLite
    UserDatabase db;
    NotificationDatabase notificationDatabase;
    List<NotificationItem> list;
    NotificationItem notificationItem;

    TextView invite_sender, invite_title, invite_location, invite_notes;
    String start_date, end_date, start_time, end_time;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_invitation);

        //(0) Instantiate layout
        invite_sender = (TextView) findViewById(R.id.invite_sender);
        invite_title = (TextView) findViewById(R.id.invite_title);
        invite_location = (TextView) findViewById(R.id.invite_location);
        invite_notes = (TextView) findViewById(R.id.invite_notes);
        // Initialise ArrayAdapter adapter for view
        listView = (ListView) findViewById(R.id.select_list);

        if (cloudantConnect == null)
            this.cloudantConnect = new CloudantConnect(this.getApplicationContext(), "user");

        db = new UserDatabase(this);

        getSupportActionBar().setElevation(0);

        //(1) Get intent that started this activity
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        notificationItem = (NotificationItem) bundle.getSerializable("vote_item");

        //(2) Enter participants
        notificationDatabase = new NotificationDatabase(this);
        list = notificationDatabase.getAllNotifications(notificationDatabase);

        //(3) Extract data from notification
        if(notificationItem != null) {
            invite_sender.setText(notificationItem.getSender_username());
            invite_title.setText(notificationItem.getSender_event());
            invite_location.setText(notificationItem.getSender_location());
            invite_notes.setText(notificationItem.getSender_notes());
        }

        adapter = new VoteInvitationAdapter(this, R.layout.row_vote, new ArrayList<NotificationItem>());
        listView.setAdapter(adapter);
    }

    private void collateDateTime() {
        int size = adapter.getCount();

        for (int i = 0; i < size; i++) {
            NotificationItem item = adapter.getItem(i);
            if(item.getSelected_date().equals("true")) {
                // Space to split all dates later when retrieving
                start_date += item.getStart_date() + " ";
                end_date += item.getEnd_date() + " ";
                start_time += item.getStart_time() + " ";
                end_time += item.getEnd_time() + " ";
            }
        }
    }

    // This method calls alert dialog to inform users a message.
    private void alertUser(String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(VoteInvitation.this);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            collateDateTime();
            // Do not save data if no checkboxes ticked
            if (start_date.isEmpty()) {
                alertUser("Sending failed!", "Please select at least one option.");
            }

            // Save data
            else {
                // Fetch user details from sqlite
                HashMap<String, String> user = db.getUserDetails();

                // Send out to users via Cloudant
                cloudantConnect.sendSelectedOptionsBackToRequester(user.get("username"), notificationItem.getSender_username(), -1,
                        notificationItem.getSender_event(), notificationItem.getSender_location(),
                        notificationItem.getSender_notes(), start_date, end_date, start_time, end_time);
                // Push all options to other targeted participants
                cloudantConnect.startPushReplication();

                Toast.makeText(getBaseContext(), "Successfully sent selected options", Toast.LENGTH_SHORT).show();
                finish();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
