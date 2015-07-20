package mooncakemonster.orbitalcalendar.voteresult;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.votesend.VoteItem;
import mooncakemonster.orbitalcalendar.votesend.VotingDatabase;

/**
 * Created by BAOJUN on 10/7/15.
 */
public class ResultAdapter extends ArrayAdapter<ResultItem> {

    UserDatabase db;
    CloudantConnect cloudantConnect;
    VotingDatabase votingDatabase;
    private List<ResultItem> objects;

    public ResultAdapter(Context context, int resource, List<ResultItem> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    static class Holder {
        RelativeLayout relativeLayout;
        TextView result_start_date;
        TextView result_end_date;
        TextView result_time;
        //TextView result_name;
        TextView result_total;

        TextView grey_start_date, grey_end_date, grey_time, total_text;
        Button can_make_it, cannot_make_it, rejected_vote;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater;
        final Holder holder;

        if (row == null) {
            inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_vote_result, parent, false);
        }

        final ResultItem resultItem = objects.get(position);

        if (resultItem != null) {
            holder = new Holder();

            if (cloudantConnect == null)
                this.cloudantConnect = new CloudantConnect(this.getContext(), "user");

            final String event_id = resultItem.getEvent_id();
            // Check if date and time is already confirmed
            final VoteItem confirmDateTime = confirmDateTime(event_id, resultItem.getStart_date());

            holder.relativeLayout = (RelativeLayout) row.findViewById(R.id.confirm_send_date);
            holder.result_start_date = (TextView) row.findViewById(R.id.result_start_date);
            holder.result_end_date = (TextView) row.findViewById(R.id.result_end_date);
            holder.result_time = (TextView) row.findViewById(R.id.result_time);
            //holder.result_name = (TextView) row.findViewById(R.id.result_name);
            holder.result_total = (TextView) row.findViewById(R.id.result_total);

            holder.can_make_it = (Button) row.findViewById(R.id.expand_can_make_it);
            holder.cannot_make_it = (Button) row.findViewById(R.id.expand_cannot_make_it);
            holder.rejected_vote = (Button) row.findViewById(R.id.expand_rejected);

            holder.grey_start_date = (TextView) row.findViewById(R.id.grey_start_date);
            holder.grey_end_date = (TextView) row.findViewById(R.id.grey_end_date);
            holder.grey_time = (TextView) row.findViewById(R.id.grey_time);
            holder.total_text = (TextView) row.findViewById(R.id.total_text);

            if(confirmNotDateTime(event_id, resultItem.getStart_date())) {
                int colour = getContext().getResources().getColor(R.color.timestamp);
                holder.grey_start_date.setTextColor(colour);
                holder.grey_end_date.setTextColor(colour);
                holder.grey_time.setTextColor(colour);
                holder.total_text.setTextColor(colour);
                holder.result_start_date.setTextColor(colour);
                holder.result_end_date.setTextColor(colour);
                holder.result_time.setTextColor(colour);
                holder.result_total.setTextColor(colour);
            }

            // Show list of participants who can make it
            holder.can_make_it.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String participants = resultItem.getSelected_username();
                    if (!participants.isEmpty() && confirmDateTime == null) openDialog(participants.split(" "), event_id, resultItem);
                    else if(!participants.isEmpty()) openViewDialog(participants.split(" "), event_id, resultItem);
                    else alertUser("No participant can make it on this day yet.");
                }
            });

            // Show list of participants who can't make it
            holder.cannot_make_it.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String participants = resultItem.getNot_selected_username();
                    if (!participants.isEmpty() && confirmDateTime == null) openDialog(participants.split(" "), event_id, resultItem);
                    else if(!participants.isEmpty()) openViewDialog(participants.split(" "), event_id, resultItem);
                    else alertUser("No participant cannot make it on this day yet.");
                }
            });

            // Show list of participants who rejected voting
            holder.rejected_vote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String participants = resultItem.getUsername_rejected();
                    if (!participants.isEmpty() && confirmDateTime == null) openDialog(participants.split(" "), event_id, resultItem);
                    else if(!participants.isEmpty()) openViewDialog(participants.split(" "), event_id, resultItem);
                    else alertUser("No participant has rejected voting for this event yet.");
                }
            });

            holder.result_start_date.setText(Constant.standardYearMonthDate(resultItem.getStart_date(), new SimpleDateFormat("dd/MM/yyyy"), Constant.DATEFORMATTER));
            holder.result_end_date.setText(Constant.standardYearMonthDate(resultItem.getEnd_date(), new SimpleDateFormat("dd/MM/yyyy"), Constant.DATEFORMATTER));
            holder.result_time.setText(resultItem.getStart_time() + " - " + resultItem.getEnd_time());
            //holder.result_name.setText(resultItem.getSelected_username());
            holder.result_total.setText(resultItem.getTotal());

            row.setTag(holder);
        }

        return row;
    }

    // This method checks if the event already has date and time confirmed, and the current option is not the confirmed one.
    private boolean confirmNotDateTime(String event_id, String start_date) {
        votingDatabase = new VotingDatabase(getContext());
        VoteItem voteItem = votingDatabase.getVoteItem(votingDatabase, event_id);

        if(voteItem.getEvent_confirm_start_date() != null && !voteItem.getEvent_confirm_start_date().equals(start_date)) {
            return true;
        }

        // Reach here when no date and time is confirmed yet
        return false;
    }

    // This method checks if the event already has date and time confirmed.
    private VoteItem confirmDateTime(String event_id, String start_date) {
        votingDatabase = new VotingDatabase(getContext());
        VoteItem voteItem = votingDatabase.getVoteItem(votingDatabase, event_id);

        if(voteItem.getEvent_confirm_start_date() != null && voteItem.getEvent_confirm_start_date().equals(start_date)) {
            return voteItem;
        }

        // Reach here when no date and time is confirmed yet
        return null;
    }

    // This method returns the targeted VoteItem object.
    private VoteItem retrieveVoteItem(String event_id) {
        votingDatabase = new VotingDatabase(getContext());
        return votingDatabase.getVoteItem(votingDatabase, event_id);
    }

    // This method checks the participants to send the date confirmed.
    private String confirmParticipants(ArrayList list) {
        String participants = "";
        int size = list.size();
        for (int i = 0; i < size; i++) {
            participants += list.get(i);
        }

        return participants;
    }

    // This method calls alert dialog to display the list of names.
    private void openDialog(final String[] split_participants, final String event_id, final ResultItem resultItem) {
        final View dialogview = LayoutInflater.from(getContext()).inflate(R.layout.dialog_result, null);
        final TextView input_username = (TextView) dialogview.findViewById(R.id.result_notice);
        final ListView listView = (ListView) dialogview.findViewById(R.id.result_list);
        final List<ResultOption> list = new ArrayList<>();

        input_username.setText("Event : " + retrieveVoteItem(event_id).getEvent_title() + "\nStart  : " + Constant.standardYearMonthDate(resultItem.getStart_date(), new SimpleDateFormat("dd/MM/yyyy"), Constant.DATEFORMATTER) + ", " + resultItem.getStart_time() +
                "\nEnd    : " + Constant.standardYearMonthDate(resultItem.getEnd_date(), new SimpleDateFormat("dd/MM/yyyy"), Constant.DATEFORMATTER) + ", " + resultItem.getEnd_time()
                 + "\n\nPlease select the participants you would like to confirm the event's date and time with.");

        final int size = split_participants.length;
        for(int i = 0; i < size; i++) {
            list.add(i, new ResultOption(true, split_participants[i]));
        }

        final ResultOptionAdapter adapter = new ResultOptionAdapter(getContext(), R.id.result_list, list);
        listView.setAdapter(adapter);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setTitle("Confirm date and time");
        alertBuilder.setView(dialogview);
        alertBuilder.setCancelable(true).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> final_list = new ArrayList<String>();

                for (int i = 0; i < size; i++) {
                    if (list.get(i).getChecked()) {
                        final_list.add(list.get(i).getUsername());
                    }
                }
                pushData(confirmParticipants(final_list), resultItem);
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

    // This method calls alert dialog to display the list of names.
    private void openViewDialog(final String[] split_participants, String event_id, final ResultItem resultItem) {
        final View dialogview = LayoutInflater.from(getContext()).inflate(R.layout.dialog_result, null);
        final TextView input_username = (TextView) dialogview.findViewById(R.id.result_notice);
        final ListView listView = (ListView) dialogview.findViewById(R.id.result_list);
        final List<ResultOption> list = new ArrayList<>();

        VoteItem voteItem = retrieveVoteItem(event_id);

        input_username.setText("Please note that you had confirmed the event's date and time as follows:\n\n" + "Event : " + voteItem.getEvent_title() + "\nStart  : " + Constant.standardYearMonthDate(voteItem.getEvent_confirm_start_date(), new SimpleDateFormat("dd/MM/yyyy"), Constant.DATEFORMATTER) +
                ", " + voteItem.getEvent_confirm_start_time() + "\nEnd    : " + Constant.standardYearMonthDate(voteItem.getEvent_confirm_end_date(), new SimpleDateFormat("dd/MM/yyyy"), Constant.DATEFORMATTER) + ", " + voteItem.getEvent_confirm_end_time()
                + "\n\nTo change the event's confirmed date and time to the current selected option, simply press the \"confirm\" button.");

        final int size = split_participants.length;
        for(int i = 0; i < size; i++) {
            list.add(i, new ResultOption(false, split_participants[i]));
        }

        final ResultOptionAdapter adapter = new ResultOptionAdapter(getContext(), R.id.result_list, list);
        listView.setAdapter(adapter);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setTitle("Confirm date and time");
        alertBuilder.setView(dialogview);

        alertBuilder.setCancelable(true).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> final_list = new ArrayList<String>();

                for (int i = 0; i < size; i++) {
                    if (list.get(i).getChecked()) {
                        final_list.add(list.get(i).getUsername());
                    }
                }
                pushData(confirmParticipants(final_list), resultItem);
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


    // This method calls alert dialog to inform users a message.
    private void alertUser(String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle("Confirm date and time");
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    // This method push the items in Cloudant database
    private void pushData(String participants, ResultItem resultItem) {
        String start_date = resultItem.getStart_date();
        String end_date = resultItem.getEnd_date();
        String start_time = resultItem.getStart_time();
        String end_time = resultItem.getEnd_time();
        String event_id = resultItem.getEvent_id();

        // Update confirmed date and time into SQLite database
        votingDatabase = new VotingDatabase(getContext());
        votingDatabase.updateInformation(votingDatabase, event_id, null, null, start_date, end_date, start_time, end_time);

        // Retrieve own username
        db = new UserDatabase(getContext());
        HashMap<String, String> user = db.getUserDetails();
        String my_username = user.get("username");

        // Retrieve event information
        VoteItem voteItem = votingDatabase.getVoteItem(votingDatabase, event_id);
        // Send out confirmation date and time to target participants
        cloudantConnect.sendConfirmationToTargetParticipants(my_username, participants, Integer.parseInt(event_id),
                Integer.parseInt(voteItem.getImageId()), voteItem.getEvent_title(), start_date, end_date, start_time, end_time);
        cloudantConnect.startPushReplication();
    }
}
