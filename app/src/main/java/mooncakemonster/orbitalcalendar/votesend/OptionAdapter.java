package mooncakemonster.orbitalcalendar.votesend;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Constant;

public class OptionAdapter extends ArrayAdapter<OptionItem> {

    private List<OptionItem> objects;

    public OptionAdapter(Context context, int resources, List<OptionItem> objects) {
        super(context, resources, objects);
        this.objects = objects;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    static class Holder {
        Button vote_start_date, vote_end_date;
        Button vote_start_time, vote_end_time;
        //Remove from list
        Button remove_option;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        final Holder holder;

        final OptionItem optionItem = objects.get(position);

        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.row_vote, parent, false);

        if (optionItem != null) {
            holder = new Holder();
            holder.vote_start_date = (Button) row.findViewById(R.id.option_start_date);
            holder.vote_end_date = (Button) row.findViewById(R.id.option_end_date);
            holder.vote_start_time = (Button) row.findViewById(R.id.option_start_time);
            holder.vote_end_time = (Button) row.findViewById(R.id.option_end_time);
            holder.remove_option = (Button) row.findViewById(R.id.remove_option);

            // Create Date and time picker
            long startDateTimeMil = Constant.stringToMillisecond(optionItem.getEvent_start_date(), optionItem.getEvent_start_time(), Constant.DATEFORMATTER, Constant.TIMEFORMATTER);
            long endDateTimeMil = Constant.stringToMillisecond(optionItem.getEvent_end_date(),optionItem.getEvent_end_time(),Constant.DATEFORMATTER, Constant.TIMEFORMATTER);

            Constant.setButtonDatePicker(getContext(), holder.vote_start_date, startDateTimeMil, "");
            Constant.setButtonDatePicker(getContext(), holder.vote_end_date, endDateTimeMil, "");
            Constant.setButtonTimePicker(getContext(), holder.vote_start_time, startDateTimeMil , "");
            Constant.setButtonTimePicker(getContext(), holder.vote_end_time, endDateTimeMil, "");

            // Remove option from list
            holder.remove_option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(optionItem);
                    notifyDataSetChanged();
                }
            });

            //Detect changes made to the text within the button, and store these changes accordingly in the underlying data structure
            holder.vote_start_date.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void afterTextChanged(Editable s) {
                    optionItem.setEvent_start_date(s.toString());
                }
            });

            holder.vote_end_date.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void afterTextChanged(Editable s) {
                    optionItem.setEvent_end_date(s.toString());
                }
            });

            holder.vote_start_time.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    optionItem.setEvent_start_time(s.toString());
                }
            });

            holder.vote_end_time.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    optionItem.setEvent_end_time(s.toString());
                }
            });

            row.setTag(holder);
        }

        return row;
    }
}
