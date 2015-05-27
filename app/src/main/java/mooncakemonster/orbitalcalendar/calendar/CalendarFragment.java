package mooncakemonster.orbitalcalendar.calendar;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.event.EventActivity;

public class CalendarFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter<String> listAdapter;
    private String[] upcomingEvents = {"Mooncake", "Monster", "Hello", "Bye", "Mooncake",
            "Monster", "Hello", "Bye", "Mooncake", "Monster", "Hello", "Bye", "Mooncake", "Monster",
            "Hello", "Bye", "Mooncake", "Monster", "Hello", "Bye"};             // Testing if scrolling works

    public CalendarFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        listAdapter = new CustomListAdapter(getActivity().getApplicationContext(), R.layout.custom_list, upcomingEvents);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getActivity().getApplicationContext(), EventActivity.class));
            }
        });
        return rootView;
    }
}
