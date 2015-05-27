package mooncakemonster.orbitalcalendar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import mooncakemonster.orbitalcalendar.R;

/**
 * This program customize ListView.
 */

public class CustomListAdapter extends ArrayAdapter {
    private Context mContext;
    private int id;
    private String[] items;

    public CustomListAdapter(Context context, int textViewResourceId, String[] list) {
        super(context, textViewResourceId, list);
        mContext = context;
        id = textViewResourceId;
        items = list;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(id, null);
        }

        TextView text = (TextView) view.findViewById(R.id.textView);

        if (items[position] != null) {
            text.setTextColor(Color.BLACK);
            text.setText(items[position]);
        }

        return view;
    }
}

