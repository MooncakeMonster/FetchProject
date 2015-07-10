package mooncakemonster.orbitalcalendar.votereceive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;

/**
 * Created by BAOJUN on 10/7/15.
 */
public class ResultAdapter extends ArrayAdapter<ResultItem> {

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

            holder.relativeLayout = (RelativeLayout) row.findViewById(R.id.confirm_send_date);
            holder.result_start_date = (TextView) row.findViewById(R.id.result_start_date);
            holder.result_end_date = (TextView) row.findViewById(R.id.result_end_date);
            holder.result_time = (TextView) row.findViewById(R.id.result_time);
            holder.result_name = (TextView) row.findViewById(R.id.result_name);
            holder.result_total = (TextView) row.findViewById(R.id.result_total);

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            holder.result_start_date.setText(resultItem.getStart_date());
            holder.result_end_date.setText(resultItem.getEnd_date());
            holder.result_time.setText(resultItem.getStart_time() + " - " + resultItem.getEnd_time());
            holder.result_name.setText(resultItem.getUsername());
            holder.result_total.setText(resultItem.getTotal());

            row.setTag(holder);
        }

        return row;
    }
}
