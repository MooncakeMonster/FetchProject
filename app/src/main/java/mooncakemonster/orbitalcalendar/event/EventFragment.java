package mooncakemonster.orbitalcalendar.event;

import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.AppointmentController;

/*
 * Purpose: EventFragment.java shows the user all the appointment created in the form of a list
 * Access via: Menu tab > Events
 */

public class EventFragment extends ListFragment{

    //Set database to allow user to retrieve data to populate EventFragment.java
    private AppointmentController appointmentDatabase;
    //List to get all the appointments
    private List<Appointment> allAppointment;
    ArrayAdapter<Appointment> adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Get all the appointment
        appointmentDatabase = new AppointmentController(getActivity());
        appointmentDatabase.open();
        allAppointment = appointmentDatabase.getAllAppointment();
        //Initialise ArrayAdapter adapter for view
        adapter = new ArrayAdapter<Appointment>(getActivity(), R.layout.fragment_eventfragment, allAppointment );
        setListAdapter(adapter);
        //Add every appointment in
        adapter.addAll(allAppointment);
        adapter.notifyDataSetChanged();

        //TODO: Set onLongClick
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listfragment, container, false);
    }

    //TODO: on selection of event
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        Appointment selectedAppt = allAppointment.get(position);

        //Update ArrayAdapter
        allAppointment = appointmentDatabase.getAllAppointment();
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onPause()
    {
        super.onPause();

        if(appointmentDatabase != null)
        {
            appointmentDatabase.close();
            appointmentDatabase = null;
        }
    }

}
