package mooncakemonster.orbitalcalendar.event;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;

/**
 * Purpose: EventView.java allows user to toggle between viewing an appointment and editing it
 * Access via: Clicking on any list item in EventFragment.java
 */
public class EventView extends DialogFragment
{
    private Appointment eventViewAppointment;

    public EventView()
    {
        //Nothing
    }

    //Allow only one instance of EventView at any one time
    static EventView newInstance(Appointment appt)
    {
        EventView eventview = new EventView();

        Bundle bundle = new Bundle();
        //TODO: Replace with parcable interface once app becomes viable
        bundle.putSerializable("appointment", appt);

        eventview.setArguments(bundle);

        //TODO: Tentatively use the available xml file and set everything as toggle off
        // (1) Can one inherit the xml file and then add an "edit" button inside?
        // (20 on clicking the edit button, toggle again
        // (3) if save, delete current appointment and replace with the other one in SQLite

        return eventview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Reference: http://stackoverflow.com/questions/7041621/fragments-oncreateview-bundle-where-does-it-come-from
        Bundle bundle = getArguments();
        eventViewAppointment = (Appointment) bundle.getSerializable("appointment");
    }

    //TODO: Implement onCreateView properly
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        //Set Title for DialogFragment
        getDialog().setTitle("View/Edit Event");

        View view = inflater.inflate(R.layout.activity_event, container, false);

        //Extract data from eventViewAppointment
        String event = eventViewAppointment.getEvent();
        long startDate = eventViewAppointment.getStartDate();
        long endDate = eventViewAppointment.getEndDate();
        String location = eventViewAppointment.getLocation();
        String notes = eventViewAppointment.getNotes();
        int remind = eventViewAppointment.getRemind();
        int colour = eventViewAppointment.getColour();

        //Set Text and other miscellaneous setting
        EditText eventLabel = (EditText) view.findViewById(R.id.title);
        EditText locationLabel = (EditText) view.findViewById(R.id.appointmentLocation);
        EditText notesLabel = (EditText) view.findViewById(R.id.appointmentNotes);
        CheckBox reminderCheckBox = (CheckBox) view.findViewById(R.id.remindbox);

        //Return values previously input
        eventLabel.setText(event);
        locationLabel.setText(location);
        notesLabel.setText(notes);

        return view;
    }
}
