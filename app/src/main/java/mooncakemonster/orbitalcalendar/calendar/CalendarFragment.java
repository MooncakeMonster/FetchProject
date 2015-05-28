package mooncakemonster.orbitalcalendar.calendar;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mooncakemonster.orbitalcalendar.R;

public class CalendarFragment extends Fragment {

    // caldroid Calendar
    private CaldroidFragment caldroidFragment;
    private FragmentActivity myContext;

    public CalendarFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        //Creating Caldroid calendar here
        //Variable formatter for setting up listener later
        final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        //(1) Create calendar
        caldroidFragment = new CaldroidFragment();
        //Get today's date and time using Java's Calendar class
        Calendar cal = Calendar.getInstance();
        //Bundle args will supply the information for caldroidFragment.setArguments(args) to build the calendar
        Bundle args = new Bundle();
        //Extract today's date to insert in bundle args
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        //Set theme for Caldroid's calendar
        args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);

        //Build caldroidFragment with the above information and setting
        caldroidFragment.setArguments(args);
        //Ensure caldroidFragment will be attached to the activity
        android.support.v4.app.FragmentTransaction t = myContext.getSupportFragmentManager().beginTransaction();
        t.replace(R.id.cal_fragment, caldroidFragment);
        t.commit();

        // (2) Setup listener for caldroidFragment
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {
                    Toast.makeText(getActivity().getApplicationContext(), "Caldroid view is created", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSelectDate(Date date, View view) {
                Toast.makeText(getActivity().getApplicationContext(), formatter.format(date), Toast.LENGTH_SHORT).show();
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

        //if (savedInstanceState == null) { displayView(0); }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }
}
