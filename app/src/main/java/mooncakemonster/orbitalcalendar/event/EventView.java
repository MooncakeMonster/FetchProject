package mooncakemonster.orbitalcalendar.event;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        return eventview;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View view = null;
        return view;
    }
}
