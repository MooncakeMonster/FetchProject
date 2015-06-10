package mooncakemonster.orbitalcalendar.calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.roomorama.caldroid.WeekdayArrayAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.event.EventActivity;

public class CalendarFragment extends Fragment {

    // caldroid Calendar
    private CaldroidFragment caldroidFragment;
    private FragmentActivity myContext;

    // listview for Events
    private ListView listView;
    int[] imgResource = { R.mipmap.red, R.mipmap.blue, R.mipmap.green, R.mipmap.purple, R.mipmap.orange  };
    String[] eventName = { "BBQ", "Birthday", "Meeting", "Sleep", "Slack" };
    String[] eventTime = { "1pm", "2pm", "3pm", "4am", "Whole day"};

    public CalendarFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Creating Caldroid calendar here
        //Variable formatter for setting up listener later
        final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        //(1) Create calendar
        caldroidFragment = new CaldroidFragment();
        //Get today's date and time using Java's Calendar class
        final Calendar cal = Calendar.getInstance();

        //Bundle args will supply the information for caldroidFragment.setArguments(args) to build the calendar
        Bundle args = new Bundle();
        //Extract today's date to insert in bundle args
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        //Highlight today's date
        caldroidFragment.setBackgroundResourceForDate(R.color.button_green, cal.getTime());
        caldroidFragment.refreshView();
        //Make background transparent
        args.putInt(CaldroidFragment.THEME_RESOURCE, R.style.CaldroidDefaultTransparent);
        //Build caldroidFragment with the above information and setting
        caldroidFragment.setArguments(args);

        // (2) Setup listener for caldroidFragment
        final CaldroidListener listener = new CaldroidListener() {
            @Override
            public void onCaldroidViewCreated() {
                //Ensure days of the week displayed (e.g. Sun, Mon, Tues,...) are black
                WeekdayArrayAdapter.textColor = Color.WHITE;
            }
            @Override
            public void onSelectDate(final Date date, View view) {
                Toast.makeText(getActivity().getApplicationContext(), formatter.format(date), Toast.LENGTH_SHORT).show();
                //Get time to parse in long
                long time = date.getTime();
                //Open EventActivity for user to input their appointment
                Intent intent = new Intent(getActivity().getApplicationContext(), EventActivity.class);
                intent.putExtra("date_passed", time);
                startActivity(intent);
            }
            @Override
            public void onChangeMonth(int month, int year) {
                String text = "month: " + month + " year: " + year;
                Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLongClickDate(Date date, View view) {
                Toast.makeText(getActivity().getApplicationContext(), "Long click " + formatter.format(date), Toast.LENGTH_SHORT).show();
            }
        };
        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        //Ensure caldroidFragment will be attached to the activity
        android.support.v4.app.FragmentTransaction t = myContext.getSupportFragmentManager().beginTransaction();
        t.replace(R.id.cal_fragment, caldroidFragment);
        t.commit();

        //Get selected date's event list
        listView = (ListView) rootView.findViewById(R.id.eventlistView);
        EventDayAdapter adapter = new EventDayAdapter(getActivity().getApplicationContext(), R.layout.row_layout);
        listView.setAdapter(adapter);

        int i = 0;
        for(String Name: eventName) {
            EventClass event = new EventClass(imgResource[i], Name, eventTime[i]);
            adapter.add(event);
            i++;
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity)
    {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }
}
