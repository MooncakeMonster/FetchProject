package mooncakemonster.orbitalcalendar.event;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
 *
 * Access via: Click on the menu button on top left corner, then Events
 ************************************************************************************************/

public class EventFragment extends ListFragment {

    //Set database to allow user to retrieve data to populate EventFragment.java
    private AppointmentController appointmentDatabase;
    //List to get all the appointments
    private List<Appointment> allAppointment;
    EventAdapter adapter;
    Appointment selected_appointment;
    Date latestDate;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get all the appointment
        appointmentDatabase = new AppointmentController(getActivity());
        appointmentDatabase.open();
        allAppointment = appointmentDatabase.getAllAppointment();
        //Initialise ArrayAdapter adapter for view
        adapter = new EventAdapter(getActivity(), R.layout.row_event, allAppointment);
        setListAdapter(new SlideExpandableListAdapter(adapter, R.id.event_layout, R.id.expandable));

        //Set first row to be today's date; if no events today, set it on the latest upcoming date
        latestDate = Calendar.getInstance().getTime();
        final long todayInMillisecond = Constant.stringToMillisecond(Constant.DATEFORMATTER.format(latestDate),
                Constant.TIMEFORMATTER.format(latestDate), Constant.DATEFORMATTER, Constant.TIMEFORMATTER);
        int size = allAppointment.size();

        for (int i = 0; i < size; i++) {
            if (adapter.getItem(i).getStartDate() >= todayInMillisecond) {
                getListView().setSelectionFromTop(i, 0);
                break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listfragment, container, false);

        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Appointment selectedAppt = adapter.getItem(position);
        selected_appointment = adapter.getItem(position);

        //Instantiate EventView.java for viewing of appointment (and editing)
        //TODO: Error occurs when user click on edit event, then cancel successively on the same list item. Resolve this bug!
        DialogFragment dialogfragment = EventView.newInstance(selectedAppt);
        dialogfragment.show(getFragmentManager(), null);

        //Update ArrayAdapter
        allAppointment = appointmentDatabase.getAllAppointment();
        adapter.notifyDataSetChanged();
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
