package mooncakemonster.orbitalcalendar.calendar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private CaldroidFragmentModified caldroidFragment;
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

    //For attaching fragment to Activity
    FragmentTransaction ft;

    public CalendarFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Creating Caldroid calendar here
        //Variable formatter for setting up listener later
        formatter = new SimpleDateFormat("dd MMM yyyy");
        //(1) Create calendar
        caldroidFragment = new CaldroidFragmentModified();
        //Get today's date and time using Java's Calendar class
        cal = Calendar.getInstance();

        //Bundle args will supply the information for caldroidFragment.setArguments(args) to build the calendar
        Bundle args = new Bundle();
        //Extract today's date to insert in bundle args
        args.putInt(CaldroidFragmentModified.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragmentModified.YEAR, cal.get(Calendar.YEAR));
        //Make background transparent
        args.putInt(CaldroidFragmentModified.THEME_RESOURCE, R.style.CaldroidDefaultTransparent);
        //Build caldroidFragment with the above information and setting
        caldroidFragment.setArguments(args);

        appointmentDatabase = new AppointmentController(getActivity());
        appointmentDatabase.open();

        // (2) Setup listener for caldroidFragment
        final CaldroidListenerModified listener = new CaldroidListenerModified() {
            @Override
            public void onCaldroidViewCreated() {
                //Ensure days of the week displayed (e.g. Sun, Mon, Tues,...) are black
                WeekdayArrayAdapter.textColor = Color.BLACK;
                highlightEventDates();
                displayEventList(cal.getTime());
            }

            @Override
            public void onSelectDate(final Date date, View view) {
                displayEventList(date);
            }

            @Override
            public void onSelectDateTwice(final Date date, View view)
            {
                onSelectDate(date, view);

                //Get time to parse in long
                long time = date.getTime();
                //Open EventActivity for user to input their appointment
                Intent intent = new Intent(getActivity().getApplicationContext(), EventActivity.class);
                intent.putExtra("date_passed", time);
                startActivity(intent);

                //Reload current fragment after any possible appointments have been made
                Fragment currentFragement = myContext.getSupportFragmentManager().findFragmentByTag("caldroidFragment");
                ft.detach(currentFragement);
                ft.attach(currentFragement);
                ft.commit();
            }

            @Override
            public void onChangeMonth(int month, int year) {
            }

            @Override
            public void onLongClickDate(Date date, View view) {

            }
        };
        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);

        //Ensure caldroidFragment will be attached to the activity
        ft = myContext.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.cal_fragment, caldroidFragment);
        ft.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Get selected date
        dateDisplay = (TextView) rootView.findViewById(R.id.date_display);

        return rootView;
    }

    // This method highlights the dates with events
    private void highlightEventDates() {
        //Get all the appointment
        allAppointment = appointmentDatabase.getAllAppointment();
        adapter = new EventDayAdapter(getActivity(), R.layout.row_event_day, allAppointment);

        int size = allAppointment.size();
        for(int i = 0; i < size; i++) {
            // Change date format to the same as date in database
            Date finalDate = Constant.stringToDate(allAppointment.get(i).getStartProperDate(), new SimpleDateFormat("yyyy MM dd"));

            //Highlight dates with events
            caldroidFragment.setTextColorForDate(R.color.colorPrimary, finalDate);
            caldroidFragment.setBackgroundResourceForDate(R.drawable.array, finalDate);
        }

        //Highlight today's date (overwrite above)
        caldroidFragment.setBackgroundResourceForDate(R.drawable.circlewhite, cal.getTime());
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
        adapter = new EventDayAdapter(getActivity(), R.layout.row_event_day, allAppointment);

        // Change date format to the same as date in database
        dateFormat = new SimpleDateFormat("dd MMM yyyy, EEEE");
        finalDate = dateFormat.format(date);
        dateDisplay.setBackgroundResource(R.color.colorPrimary);
        dateDisplay.setText(finalDate);

        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity)
    {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }
}