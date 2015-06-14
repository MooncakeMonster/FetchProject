package mooncakemonster.orbitalcalendar.event;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;

/**
 * Purpose: EventView.java allows user to toggle between viewing an appointment and editing it
 * Access via: Clicking on any list item in EventFragment.java
 */
public class EventView extends DialogFragment
{
    //Allow only one instance of EventView at any one time
    static EventView newInstance(Appointment appt)
    {
        EventView eventview = new EventView();
        //TODO: Import all the data into an xml file that is uneditable
        // Have data
        // Put in textview -> java to xml (how?)
        // hint @ textView.setText
        // set as uneditable
        //TODO: Tentatively use the available xml file and set everything as toggle off
        // (1) Can one inherit the xml file and then add an "edit" button inside?
        // (20 on clicking the edit button, toggle again
        // (3) if save, delete current appointment and replace with the other one in SQLite

        return eventview;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_event, container, false);

        //Get all the data from


        return view;
    }
}
