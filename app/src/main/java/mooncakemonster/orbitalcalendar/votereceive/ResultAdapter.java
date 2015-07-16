package mooncakemonster.orbitalcalendar.votereceive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

            holder.relativeLayout = (RelativeLayout) row.findViewById(R.id.confirm_send_date);
            holder.result_start_date = (TextView) row.findViewById(R.id.result_start_date);
            holder.result_end_date = (TextView) row.findViewById(R.id.result_end_date);
            holder.result_time = (TextView) row.findViewById(R.id.result_time);
            //holder.result_name = (TextView) row.findViewById(R.id.result_name);
            holder.result_total = (TextView) row.findViewById(R.id.result_total);

            holder.can_make_it = (Button) row.findViewById(R.id.expand_can_make_it);
            holder.cannot_make_it = (Button) row.findViewById(R.id.expand_cannot_make_it);
            holder.rejected_vote = (Button) row.findViewById(R.id.expand_rejected);

            // Show list of participants who can make it
            holder.can_make_it.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String participants = resultItem.getSelected_username();
                    if (!participants.isEmpty()) openDialog(participants.split(" "), resultItem);
                    else alertUser("Confirm date and time", "No participant can make it on this day yet.");
                }
            });

            // Show list of participants who can't make it
            holder.cannot_make_it.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String participants = resultItem.getNot_selected_username();
                    if (!participants.isEmpty()) openDialog(participants.split(" "), resultItem);
                    else alertUser("Confirm date and time", "No participant cannot make it on this day yet.");
                }
            });

            // Show list of participants who rejected voting
            holder.rejected_vote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String participants = resultItem.getUsername_rejected();
                    if (!participants.isEmpty()) openDialog(participants.split(" "), resultItem);
                    else alertUser("Confirm date and time", "No participant has rejected voting for this event yet.");
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
    private void openDialog(final String[] split_participants, final ResultItem resultItem) {
        final ArrayList<String> list = new ArrayList<>();

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setTitle("Confirm date and time").setMultiChoiceItems(split_participants, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    list.add(split_participants[which]);
                } else if (list.contains(split_participants[which])) {
                    list.remove(split_participants[which]);
                }
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Send out to Cloudant only if there are participants to send out to
                if (list.size() > 0) {
                    pushData(confirmParticipants(list), resultItem);
                    Toast.makeText(getContext(), "Details successfully sent", Toast.LENGTH_SHORT).show();
                }
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
    private void alertUser(String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle(title);
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
