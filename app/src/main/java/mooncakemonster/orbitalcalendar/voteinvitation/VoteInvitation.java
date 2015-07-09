package mooncakemonster.orbitalcalendar.voteinvitation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
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
    SelectAdapter adapter;

    // Retrieve username from SQLite
    UserDatabase db;
    NotificationItem notificationItem;

    Button reject_event;
    TextView invite_sender, invite_title, invite_location, invite_notes;
    String start_date, end_date, start_time, end_time, reject_reason;

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

        //(2) Extract data from notification
        if(notificationItem != null) {
            String notes = notificationItem.getSender_notes();

            invite_sender.setText(notificationItem.getSender_username());
            invite_title.setText(notificationItem.getSender_event());
            invite_location.setText(notificationItem.getSender_location());
            if(!notes.isEmpty()) invite_notes.setText(notes);
            else invite_notes.setText("No notes");
        }

        //(3) Get voting options from sender
        adapter = new SelectAdapter(this, R.layout.row_selected_checkbox, new ArrayList<SelectItem>());
        listView.setAdapter(adapter);
        retrieveAllOptions(notificationItem);

        reject_event = (Button) findViewById(R.id.reject_event);
        reject_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View dialogview = LayoutInflater.from(VoteInvitation.this).inflate(R.layout.edittext_dialog, null);
                final EditText input_username = (EditText) dialogview.findViewById(R.id.input_text);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(VoteInvitation.this);
                alertBuilder.setTitle("State your reason: ");
                alertBuilder.setView(dialogview);

                alertBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reject_reason = input_username.getText().toString();
                        resetAllDateTime();
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Rejected event successfully", Toast.LENGTH_SHORT).show();
                        finish();
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
    }

    // This method retrieves the voting options from sender.
    private void retrieveAllOptions(NotificationItem notificationItem) {
        String[] split_start_date = notificationItem.getStart_date().split(" ");
        String[] split_end_date = notificationItem.getEnd_date().split(" ");
        String[] split_start_time = notificationItem.getStart_time().split(" ");
        String[] split_end_time = notificationItem.getEnd_time().split(" ");

        int size = split_start_date.length;

        for(int i = 0; i < size; i++) {
            adapter.add(new SelectItem(false, split_start_date[i], split_end_date[i],
                    split_start_time[i], split_end_time[i]));
        }
    }

    // This method resets all date and time as user has rejected event.
    private void resetAllDateTime() {
        start_date = null;
        end_date = null;
        start_time = null;
        end_time = null;
    }

    // This method retrieves the selected date and time by user.
    private void collateDateTime() {
        int size = adapter.getCount();

        for (int i = 0; i < size; i++) {
            SelectItem item = adapter.getItem(i);
            if(item.getSelected_date()) {
                // Space to split all dates later when retrieving
                start_date += item.getEvent_start_date() + " ";
                end_date += item.getEvent_end_date() + " ";
                start_time += item.getEvent_start_time() + " ";
                end_time += item.getEvent_end_time() + " ";
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
                cloudantConnect.sendSelectedOptionsBackToRequester(user.get("username"), notificationItem.getSender_username(), 0,
                        notificationItem.getSender_event(), notificationItem.getSender_location(),
                        notificationItem.getSender_notes(), start_date, end_date, start_time, end_time, reject_reason);
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
