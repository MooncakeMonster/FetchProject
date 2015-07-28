package mooncakemonster.orbitalcalendar.votereceive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.voteresult.ResultOption;
import mooncakemonster.orbitalcalendar.voteresult.ResultOptionAdapter;
import mooncakemonster.orbitalcalendar.votesend.VoteItem;

/*************************************************************************************************
 * Purpose: VotingAdapter.java serves as a "holder" which contain the interface for how a voting
 * "unit" will appear in VotingFragment.java
 *
 * VotingAdapter.java will display:
 * (a) Number of votes casted, compared to expected votes.
 * (b) Event name
 * (c) Confirmation status of both date and time
 *
 * Access via: Click on the menu button on top left corner, then Voting Results
 **************************************************************************************************/

public class VotingAdapter extends ArrayAdapter<VoteItem> {

    private UserDatabase db;
    private CloudantConnect cloudantConnect;
    private List<VoteItem> objects;

    public VotingAdapter(Context context, int resource, List<VoteItem> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    static class Holder {
        RelativeLayout relativeLayout;
        TextView vote_total;
        TextView total;
        TextView event_title;
        TextView event_location;
        TextView event_start_end_date;
        TextView event_start_end_time;
        Button view_result, send_reminder, attendance;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        LayoutInflater inflater;
        Holder holder;

        if(row == null) {
            inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_vote_history, parent, false);
        }

        final VoteItem voteItem = objects.get(position);

        if(voteItem != null) {
            holder = new Holder();

            holder.relativeLayout = (RelativeLayout) row.findViewById(R.id.history_set_colour);
            holder.vote_total = (TextView) row.findViewById(R.id.history_vote_total);
            holder.total = (TextView) row.findViewById(R.id.history_total);
            holder.event_title = (TextView) row.findViewById(R.id.history_title);
            holder.event_location = (TextView) row.findViewById(R.id.history_location);
            holder.event_start_end_date = (TextView) row.findViewById(R.id.history_start_end_date);
            holder.event_start_end_time = (TextView) row.findViewById(R.id.history_start_end_time);

            // Child
            holder.view_result = (Button) row.findViewById(R.id.expand_view_result);
            holder.send_reminder = (Button) row.findViewById(R.id.expand_send_reminder);
            holder.attendance = (Button) row.findViewById(R.id.expand_attendance);

            //Picasso.with(getContext()).load(getBackgroundResource(voteItem)).fit().noFade().into(holder.event_image);
            //holder.event_image.setBackgroundResource(R.color.redbear);
            holder.relativeLayout.setBackgroundResource(Integer.parseInt(voteItem.getImageId()));

            // Retrieve the number of particpants that cast votes
            String[] split_participants = voteItem.getEvent_participants().split(" ");
            String[] split_voted_participants = {};
            final String voted_participants = voteItem.getEvent_voted_participants();

            if(voted_participants != null) {
                split_voted_participants = voted_participants.split(" ");
            }

            holder.vote_total.setText("" + split_voted_participants.length);
            holder.total.setText("/" + split_participants.length);

            // Set event title and location
            holder.event_title.setText(voteItem.getEvent_title());
            holder.event_location.setText(voteItem.getEvent_location());

            // Display final confirmed date
            if(voteItem.getEvent_confirm_start_date() != null) {
                holder.event_start_end_date.setText("Start : " + voteItem.getEvent_confirm_start_date() + ", " + voteItem.getEvent_confirm_start_time());
                holder.event_start_end_time.setText("End   : " + voteItem.getEvent_confirm_end_date() + ", " + voteItem.getEvent_confirm_end_time());
            } else {
                holder.event_start_end_date.setText("Date not confirmed");
                holder.event_start_end_time.setText("Time not confirmed");
            }

            final View view = row;
            // Create bundle
            final Bundle bundle = new Bundle();
            bundle.putSerializable("voteItem", voteItem);

            holder.view_result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), VotingResultActivity.class);
                    intent.putExtras(bundle);
                    view.getContext().startActivity(intent);
                }
            });

            holder.send_reminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final String[] split_not_voted = checkNotVoted(voteItem.getEvent_voted_participants(), voteItem.getEvent_participants());
                    if(split_not_voted.length > 0) openReminderDialog(split_not_voted, voteItem);
                    else Constant.alertUser(getContext(), "Send reminder", "No participant needs to be reminded to vote yet.");
                }
            });

            holder.attendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String attendance = voteItem.getEvent_attendance();
                    if(attendance != null) Constant.openAttendanceDialog(getContext(), attendance.split(" "));
                    else Constant.alertUser(getContext(), "Attendance", "No participant has confirmed their attendance for this event yet.");
                }
            });

            row.setTag(holder);
        }

        return row;
    }

    // This method calls alert dialog to display the list of names that had not cast votes.
    private void openReminderDialog(final String[] split_participants, final VoteItem voteItem) {
        final View dialogview = LayoutInflater.from(getContext()).inflate(R.layout.dialog_result, null);
        final View header = LayoutInflater.from(getContext()).inflate(R.layout.header_result, null);
        final TextView input_username = (TextView) header.findViewById(R.id.result_notice);
        final ListView listView = (ListView) dialogview.findViewById(R.id.result_list);
        listView.addHeaderView(header);
        final List<ResultOption> list = new ArrayList<>();

        input_username.setText("Select the participants to remind them to vote for this event:");

        final int size = split_participants.length;
        for(int i = 0; i < size; i++) {
            list.add(i, new ResultOption(true, split_participants[i]));
        }

        final ResultOptionAdapter adapter = new ResultOptionAdapter(getContext(), R.id.result_list, list);
        listView.setAdapter(adapter);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setTitle("Send reminder");
        alertBuilder.setView(dialogview);
        alertBuilder.setCancelable(true).setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> final_list = new ArrayList<String>();

                for (int i = 0; i < size; i++) {
                    if (list.get(i).getChecked()) {
                        final_list.add(list.get(i).getUsername());
                    }
                }
                if (cloudantConnect == null)
                    cloudantConnect = new CloudantConnect(getContext(), "user");

                db = new UserDatabase(getContext());
                HashMap<String, String> user = db.getUserDetails();
                String my_username = user.get("username");

                cloudantConnect.sendReminderToTargetParticipants(my_username, sendReminder(final_list), Integer.parseInt(voteItem.getEventId()),
                        Integer.parseInt(voteItem.getImageId()), voteItem.getEvent_title());
                cloudantConnect.startPushReplication();

                Toast.makeText(getContext(), "Details successfully sent", Toast.LENGTH_SHORT).show();
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

    // This method checks which participant has not cast vote.
    private String[] checkNotVoted(String voted_participants, String participants) {
        String[] split_voted_participants = {};

        if(voted_participants != null) split_voted_participants = voted_participants.split(" ");
        int size = split_voted_participants.length;

        for(int i = 0; i < size; i++) {
            participants = participants.replace(split_voted_participants[i], "");
        }

        return participants.split(" ");
    }

    // This method checks the participants to send reminder to.
    private String sendReminder(ArrayList<String> list) {
        String send_participants = "";
        int size = list.size();

        for(int i = 0; i < size; i++) {
           send_participants += list.get(i) + " ";
        }
        return send_participants;
    }

}
