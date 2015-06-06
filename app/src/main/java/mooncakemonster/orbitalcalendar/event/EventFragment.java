package mooncakemonster.orbitalcalendar.event;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.AppointmentController;

public class EventFragment extends Fragment{

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        return rootView;
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
