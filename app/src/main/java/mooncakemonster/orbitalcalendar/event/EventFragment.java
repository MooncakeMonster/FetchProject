package mooncakemonster.orbitalcalendar.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.AppointmentController;
import mooncakemonster.orbitalcalendar.database.Constant;

/***********************************************************************************************
 * Purpose: EventFragment.java shows the user all the appointment(s) created in the form of a list
 * <p/>
 * Access via: Click on the menu button on top left corner, then Events
 ************************************************************************************************/

public class EventFragment extends ListFragment {

    //Set database to allow user to retrieve data to populate EventFragment.java
    private AppointmentController appointmentDatabase;
    //List to get all the appointments
    private List<Appointment> allAppointment;
    private EventAdapter adapter;
    private Date latestDate;
    private ImageButton addEventButton;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get all the appointment
        appointmentDatabase = new AppointmentController(getActivity());
        appointmentDatabase.open();
        allAppointment = appointmentDatabase.getAllAppointment();
        //Initialise ArrayAdapter adapter for view
        adapter = new EventAdapter(getActivity());

        //Set first row to be today's date; if no events today, set it on the latest upcoming date
        latestDate = Calendar.getInstance().getTime();
        final long todayInMillisecond = Constant.stringToMillisecond(Constant.DATEFORMATTER.format(latestDate),
                Constant.TIMEFORMATTER.format(latestDate), Constant.DATEFORMATTER, Constant.TIMEFORMATTER);
        int size = allAppointment.size();

        // Add separator into listview
        boolean separated = true;
        // Get position of latest date
        int j, latest = 0;
        for (int i = 0; i < size; i++) {
            Appointment appointment = allAppointment.get(i);

            //Check if that particular appointment is > 1 day
            long days = fewDaysAppointment(appointment.getStartDate(), appointment.getEndDate());
            for (j = 0; j < days; j++) {
                adapter.addItem(new Appointment(appointment.getId(), appointment.getEvent() + " (Day " + j + ")", appointment.getStartProperDate(),
                        appointment.getStartDate() + Constant.DAY_IN_MILLISECOND, appointment.getEndDate(), appointment.getLocation(), appointment.getNotes(),
                        appointment.getRemind(), appointment.getColour()));
            }

            i += j;

            if (separated && allAppointment.get(i).getStartDate() >= todayInMillisecond) {
                // Add separator into listview
                adapter.addSeparatorItem(i);
                adapter.addItem(appointment);
                separated = false;
            } else {
                adapter.addItem(appointment);
            }

            if (separated && allAppointment.get(i).getStartDate() >= todayInMillisecond)
                latest++;
        }

        setListAdapter(new SlideExpandableListAdapter(adapter, R.id.event_layout, R.id.expandable));
        if(latest >= 5) getListView().setSelectionFromTop(0, 0);
        else getListView().setSelectionFromTop(size - 6, 0);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listfragment, container, false);

        addEventButton = (ImageButton) rootView.findViewById(R.id.addEventButton);

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), EventActivity.class);
                intent.putExtra("date_passed", new Date(System.currentTimeMillis()));
                startActivity(intent);
            }
        });

        return rootView;
    }

    private long fewDaysAppointment(long start_milliseconds, long end_milliseconds) {
        return (end_milliseconds - start_milliseconds) / Constant.DAY_IN_MILLISECOND;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (appointmentDatabase != null) {
            appointmentDatabase.close();
            appointmentDatabase = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (appointmentDatabase == null) {
            appointmentDatabase = new AppointmentController(getActivity());
            appointmentDatabase.open();
        }
    }


}
