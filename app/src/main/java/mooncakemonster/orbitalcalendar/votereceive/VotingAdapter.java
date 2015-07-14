package mooncakemonster.orbitalcalendar.votereceive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.votesend.VoteItem;

/**
 * Created by BAOJUN on 20/6/15.
 */

public class VotingAdapter extends ArrayAdapter<VoteItem> {

    UserDatabase db;
    CloudantConnect cloudantConnect;
    List<VoteItem> objects;

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
        Button view_result, send_reminder, fetch_help;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
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
            holder.fetch_help = (Button) row.findViewById(R.id.expand_fetch_help);

            //Picasso.with(getContext()).load(getBackgroundResource(voteItem)).fit().noFade().into(holder.event_image);
            //holder.event_image.setBackgroundResource(R.color.redbear);
            holder.relativeLayout.setBackgroundResource(Integer.parseInt(voteItem.getImageId()));

            // Retrieve the number of particpants that cast votes
            String[] split_participants = voteItem.getEvent_participants().split(" "), split_voted_participants = {};
            String voted_participants = voteItem.getEvent_voted_participants();

            if(voted_participants != null)
                Log.d("VotingDatabaseAdapter", voted_participants);

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
                holder.event_start_end_date.setText(voteItem.getEvent_confirm_start_date() + " - " + voteItem.getEvent_confirm_end_date());
                holder.event_start_end_time.setText(voteItem.getEvent_confirm_start_time() + " - " + voteItem.getEvent_confirm_end_time());
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
                    final ArrayList<String> list = new ArrayList<String>();

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                    alertBuilder.setTitle("Send Voting Reminder").setMultiChoiceItems(split_not_voted, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if(isChecked) {
                                list.add(split_not_voted[which]);
                            } else if(list.contains(split_not_voted[which])) {
                                list.remove(split_not_voted[which]);
                            }
                        }
                    }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO: Send out to Cloudant
                            if (cloudantConnect == null)
                                cloudantConnect = new CloudantConnect(getContext(), "user");

                            db = new UserDatabase(getContext());
                            HashMap<String, String> user = db.getUserDetails();
                            String my_username = user.get("username");

                            cloudantConnect.startPullReplication();
                            cloudantConnect.sendReminderToTargetParticipants(my_username, sendReminder(list), Integer.parseInt(voteItem.getEventId()),
                                    Integer.parseInt(voteItem.getImageId()), voteItem.getEvent_title());
                            cloudantConnect.startPushReplication();

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

            holder.fetch_help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Only can help if user has not confirmed date

                }
            });

            row.setTag(holder);
        }

        return row;
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
