package mooncakemonster.orbitalcalendar.votereceive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.voteconfirmdate.ConfirmDateAdapter;
import mooncakemonster.orbitalcalendar.voteconfirmdate.ConfirmParticipants;

/**
 * Created by BAOJUN on 10/7/15.
 */
public class ResultAdapter extends ArrayAdapter<ResultItem> {

    CloudantConnect cloudantConnect;
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
        TextView result_name;
        TextView result_total;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater;
        final Holder holder;

        if(row == null) {
            inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_vote_result, parent, false);
        }

        final ResultItem resultItem = objects.get(position);

        if(resultItem != null) {
            holder = new Holder();

            if (cloudantConnect == null)
                this.cloudantConnect = new CloudantConnect(this.getContext(), "user");

            holder.relativeLayout = (RelativeLayout) row.findViewById(R.id.confirm_send_date);
            holder.result_start_date = (TextView) row.findViewById(R.id.result_start_date);
            holder.result_end_date = (TextView) row.findViewById(R.id.result_end_date);
            holder.result_time = (TextView) row.findViewById(R.id.result_time);
            holder.result_name = (TextView) row.findViewById(R.id.result_name);
            holder.result_total = (TextView) row.findViewById(R.id.result_total);

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] split_can_make_it = resultItem.getSelected_username().split(" ");
                    String[] split_cannot_make_it = resultItem.getNot_selected_username().split(" ");
                    String[] split_rejected_voting = resultItem.getUsername_rejected().split(" ");

                    final View dialogview = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirm_date, null);

                    final ListView can_make_it = (ListView) dialogview.findViewById(R.id.can_make_it);
                    final ListView cannot_make_it = (ListView) dialogview.findViewById(R.id.cannot_make_it);
                    final ListView rejected_voting = (ListView) dialogview.findViewById(R.id.rejected_voting);

                    final ConfirmDateAdapter adapter_can_make_it = new ConfirmDateAdapter(getContext(), R.layout.row_checkbox, splitParticipants(split_can_make_it));
                    final ConfirmDateAdapter adapter_cannot_make_it = new ConfirmDateAdapter(getContext(), R.layout.row_checkbox, splitParticipants(split_cannot_make_it));
                    final ConfirmDateAdapter adapter_rejected_voting = new ConfirmDateAdapter(getContext(), R.layout.row_checkbox, splitParticipants(split_rejected_voting));

                    can_make_it.setAdapter(adapter_can_make_it);
                    cannot_make_it.setAdapter(adapter_cannot_make_it);
                    rejected_voting.setAdapter(adapter_rejected_voting);

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                    alertBuilder.setTitle("Confirm date and time");
                    alertBuilder.setView(dialogview);

                    alertBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO: Send out to Cloudant
                            String participants = confirmParticipants(adapter_can_make_it)
                                    + confirmParticipants(adapter_cannot_make_it) + confirmParticipants(adapter_rejected_voting);

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

            holder.result_start_date.setText(resultItem.getStart_date());
            holder.result_end_date.setText(resultItem.getEnd_date());
            holder.result_time.setText(resultItem.getStart_time() + " - " + resultItem.getEnd_time());
            holder.result_name.setText(resultItem.getSelected_username());
            holder.result_total.setText(resultItem.getTotal());

            row.setTag(holder);
        }

        return row;
    }

    // This method stores each participants into individual object.
    private List<ConfirmParticipants> splitParticipants(String[] split_participants) {
        List<ConfirmParticipants> list = new ArrayList<>();
        int size = split_participants.length;

        for(int i = 0; i < size; i++) {
            list.add(new ConfirmParticipants(false, split_participants[i]));
        }
        return list;
    }

    // This method checks the participants to send the date confirmed.
    private String confirmParticipants(ConfirmDateAdapter adapter) {
        String participants = "";
        int size = adapter.getCount();
        for(int i = 0; i < size; i++) {
            ConfirmParticipants confirm = adapter.getItem(i);
            if(confirm.getSend()) {
                participants += confirm.getUsername() + " ";
            }
        }

        return participants;
    }
}