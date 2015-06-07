package mooncakemonster.orbitalcalendar.event;

import android.app.ListFragment;
import android.widget.ArrayAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.AppointmentController;

/**
 * EventFragment.java shows the user all the appointment set
 */
public class EventFragment extends ListFragment{

    //Set database to allow user to retrieve data to populate EventFragment.java
    private AppointmentController appointmentDatabase;
    //List to get all the appointments
    private List<Appointment> allAppointment;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //Get all the appointment
        appointmentDatabase = new AppointmentController(getActivity());
        appointmentDatabase.open();
        allAppointment = appointmentDatabase.getAllAppointment();

        ArrayAdapter<Appointment> adapter = new ArrayAdapter<Appointment>(getActivity(), R.layout.fragment_eventfragment, allAppointment );
        setListAdapter(adapter);

        //Add every appointment in
        adapter.addAll(allAppointment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onPause()
    {
        if(appointmentDatabase != null)
        {
            appointmentDatabase.close();
            appointmentDatabase = null;
        }
    }

}
