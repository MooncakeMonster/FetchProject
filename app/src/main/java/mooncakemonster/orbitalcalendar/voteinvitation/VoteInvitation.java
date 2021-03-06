package mooncakemonster.orbitalcalendar.voteinvitation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.notifications.NotificationDatabase;
import mooncakemonster.orbitalcalendar.notifications.NotificationItem;

/**
 * This class allows users to send a list of date
 * and time options to participants for voting an event.
 */
public class VoteInvitation extends ActionBarActivity {

    // Connect to cloudant database
    private CloudantConnect cloudantConnect;

    // List to get all the date options
    private ListView listView;
    private SelectAdapter adapter;

    // Retrieve username from SQLite
    private UserDatabase db;
    private NotificationDatabase notificationDatabase;
    private NotificationItem notificationItem;

    private Button reject_event;
    private TextView invite_sender, invite_title, invite_location, invite_notes;
    private String my_username = "", selected_option = "", start_date = "", end_date = "", start_time = "", end_time = "",
            not_start_date = "", not_end_date = "", not_start_time = "", not_end_time = "", reject_reason = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_invitation);

        // Get intent that started this activity
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        notificationItem = (NotificationItem) bundle.getSerializable("vote_item");

        // Get voting options from sender
        adapter = new SelectAdapter(this, R.layout.row_selected_checkbox, new ArrayList<SelectItem>());
        View header = getLayoutInflater().inflate(R.layout.header_vote_invitation, null);
        listView = (ListView) findViewById(R.id.select_list);
        listView.addHeaderView(header);
        listView.setAdapter(adapter);
        retrieveAllOptions(notificationItem);

        // Instantiate layout
        invite_sender = (TextView) header.findViewById(R.id.invite_sender);
        invite_title = (TextView) header.findViewById(R.id.invite_title);
        invite_location = (TextView) header.findViewById(R.id.invite_location);
        invite_notes = (TextView) header.findViewById(R.id.invite_notes);

        if (cloudantConnect == null)
            this.cloudantConnect = new CloudantConnect(this.getApplicationContext(), "user");

        db = new UserDatabase(this);
        // Fetch user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        my_username = user.get("username");

        getSupportActionBar().setElevation(0);

        // Extract data from notification
        if(notificationItem != null) {
            String notes = notificationItem.getSender_notes();

            invite_sender.setText(notificationItem.getSender_username());
            invite_title.setText(notificationItem.getSender_event());
            invite_location.setText(notificationItem.getSender_location());
            if(notes != null && !notes.isEmpty()) invite_notes.setText(notes);
            else invite_notes.setText("No notes");
        }

        // Dialog edittext appears for user to state reason for rejection if they choose to reject voting
        reject_event = (Button) findViewById(R.id.reject_event);
        reject_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View dialogview = LayoutInflater.from(VoteInvitation.this).inflate(R.layout.dialog_edittext, null);
                final EditText input_username = (EditText) dialogview.findViewById(R.id.input_text);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(VoteInvitation.this);
                alertBuilder.setTitle("State your reason: ");
                alertBuilder.setView(dialogview);

                alertBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetAllDateTime();
                        pushItem(my_username, notificationItem.getSender_username(), notificationItem.getEventId(), notificationItem.getImageId(),
                                notificationItem.getSender_event(), notificationItem.getSender_location(),
                                notificationItem.getSender_notes(), start_date, end_date, start_time, end_time, input_username.getText().toString());
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
        not_start_date = null;
        not_end_date = null;
        not_start_time = null;
        not_end_time = null;
    }

    // This method retrieves the selected date and time by user if user did not reject the event.
    private void collateDateTime() {
        int size = adapter.getCount();
        Log.d("VoteInvitation", "" + size);

        for (int i = 0; i < size; i++) {
            SelectItem item = adapter.getItem(i);
            if(item.getSelected_date()) {
                Log.d("VoteInvitation", "pass");
                // Space to split all dates later when retrieving
                selected_option += "true ";
                start_date += item.getEvent_start_date() + " ";
                end_date += item.getEvent_end_date() + " ";
                start_time += item.getEvent_start_time() + " ";
                end_time += item.getEvent_end_time() + " ";
            } else {
                // Space to split all dates later when retrieving
                selected_option += "false ";
                not_start_date += item.getEvent_start_date() + " ";
                not_end_date += item.getEvent_end_date() + " ";
                not_start_time += item.getEvent_start_time() + " ";
                not_end_time += item.getEvent_end_time() + " ";
            }
        }
    }

    // This method push items into Cloudant database.
    private void pushItem(String my_username, String sender_username, int eventId, int imageId, String event, String location, String notes,
                          String start_date, String end_date, String start_time, String end_time, String reject_reason) {

        Log.d("VoteInvitation", notificationItem.getSender_username());
        // Send out to users via Cloudant
        cloudantConnect.sendSelectedOptionsBackToRequester(my_username, sender_username, eventId, imageId, event, location, notes,
                                        start_date, end_date, start_time, end_time, not_start_date, not_end_date,
                                        not_start_time, not_end_time, reject_reason);

        // Push all options to other targeted participants
        cloudantConnect.startPushReplication();
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
                Constant.alertUser(getApplicationContext(), "Sending failed!", "Please select at least one option.");
            }

            // Save data
            else {
                notificationDatabase = new NotificationDatabase(this);
                notificationDatabase.updateInformation(notificationDatabase, notificationItem.getRow_id(), null, "true", selected_option);

                pushItem(my_username, notificationItem.getSender_username(), notificationItem.getEventId(), notificationItem.getImageId(),
                        notificationItem.getSender_event(), notificationItem.getSender_location(),
                        notificationItem.getSender_notes(), start_date, end_date, start_time, end_time, reject_reason);

                Toast.makeText(getBaseContext(), "Successfully sent selected options", Toast.LENGTH_SHORT).show();
                finish();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
