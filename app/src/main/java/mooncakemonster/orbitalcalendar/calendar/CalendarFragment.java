package mooncakemonster.orbitalcalendar.calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.roomorama.caldroid.WeekdayArrayAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.AppointmentController;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.event.EventActivity;

public class CalendarFragment extends ListFragment {

    //Caldroid Calendar
    private CaldroidFragment caldroidFragment;
    private FragmentActivity myContext;
    private SimpleDateFormat formatter;
    private Calendar cal;

    //List to get all the appointments
    private List<Appointment> allAppointment;
    //Retrieves events from database
    private AppointmentController appointmentDatabase;
    private EventDayAdapter adapter;
    //Display selected date
    private TextView dateDisplay;

    public CalendarFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Creating Caldroid calendar here
        //Variable formatter for setting up listener later
        formatter = new SimpleDateFormat("dd MMM yyyy");
        //(1) Create calendar
        caldroidFragment = new CaldroidFragment();
        //Get today's date and time using Java's Calendar class
        cal = Calendar.getInstance();

        //Bundle args will supply the information for caldroidFragment.setArguments(args) to build the calendar
        Bundle args = new Bundle();
        //Extract today's date to insert in bundle args
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        //Make background transparent
        args.putInt(CaldroidFragment.THEME_RESOURCE, R.style.CaldroidDefaultTransparent);
        //Build caldroidFragment with the above information and setting
        caldroidFragment.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Get selected date
        dateDisplay = (TextView) rootView.findViewById(R.id.date_display);
        appointmentDatabase = new AppointmentController(getActivity());
        appointmentDatabase.open();

        // Highlight all dates with events
        highlightEventDates();

        // (2) Setup listener for caldroidFragment
        final CaldroidListener listener = new CaldroidListener() {
            @Override
            public void onCaldroidViewCreated() {
                //Ensure days of the week displayed (e.g. Sun, Mon, Tues,...) are black
                WeekdayArrayAdapter.textColor = Color.BLACK;
                displayEventList(cal.getTime());
            }
            @Override
            public void onSelectDate(final Date date, View view) {
                Toast.makeText(getActivity().getApplicationContext(), formatter.format(date), Toast.LENGTH_SHORT).show();
                displayEventList(date);
            }
            @Override
            public void onChangeMonth(int month, int year) {
                String text = "month: " + month + " year: " + year;
                Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLongClickDate(Date date, View view) {
                Toast.makeText(getActivity().getApplicationContext(), "Long click " + formatter.format(date), Toast.LENGTH_SHORT).show();
                //Get time to parse in long
                long time = date.getTime();
                //Open EventActivity for user to input their appointment
                Intent intent = new Intent(getActivity().getApplicationContext(), EventActivity.class);
                intent.putExtra("date_passed", time);
                startActivity(intent);
            }
        };
        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);

        //Ensure caldroidFragment will be attached to the activity
        android.support.v4.app.FragmentTransaction t = myContext.getSupportFragmentManager().beginTransaction();
        t.replace(R.id.cal_fragment, caldroidFragment);
        t.commit();

        return rootView;
    }

    // This method highlights the dates with events
    private void highlightEventDates() {
        //Get all the appointment
        allAppointment = appointmentDatabase.getAllAppointment();
        adapter = new EventDayAdapter(getActivity(), R.layout.row_layout, allAppointment);

        for(int i = 0; i < allAppointment.size(); i++) {
            // Change date format to the same as date in database
            Date finalDate = Constant.stringToDate(allAppointment.get(i).getStartProperDate(), new SimpleDateFormat("yyyy MM dd"));

            // Only display date when list is not empty
            if (!allAppointment.isEmpty()) {
                //Highlight dates with events
                caldroidFragment.setBackgroundResourceForDate(R.drawable.flower, finalDate);
            }
        }

        //Highlight today's date (overwrite above)
        caldroidFragment.setBackgroundResourceForDate(R.drawable.flowercoloured, cal.getTime());
        caldroidFragment.refreshView();
    }

    // This method displays the list of events for selected date
    // If date is not selected, set default as current date
    private void displayEventList(Date date) {
        // Change date format to the same as date in database
        DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd");
        String finalDate = dateFormat.format(date);

        //Get all the appointment
        allAppointment = appointmentDatabase.getSelectedDateAppointment(finalDate);
        adapter = new EventDayAdapter(getActivity(), R.layout.row_layout, allAppointment);

        // Only display date when list is not empty
        if(!allAppointment.isEmpty()) {
            // Change date format to the same as date in database
            dateFormat = new SimpleDateFormat("dd MMM yyyy, EEEE");
            finalDate = dateFormat.format(date);
            dateDisplay.setBackgroundResource(R.color.colorPrimary);
            dateDisplay.setText(finalDate);
        } else {
            dateDisplay.setBackgroundResource(R.color.caldroid_transparent);
            dateDisplay.setText("");
        }
        setListAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity)
    {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }
}