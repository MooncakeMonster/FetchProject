package mooncakemonster.orbitalcalendar.event;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.Constant;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        //Set Title for DialogFragment
        getDialog().setTitle("View/Edit Event");
        //Close EventView (DialogFragment) if pressed outside
        setCancelable(true);

        View view = inflater.inflate(R.layout.activity_event, container, false);

        //Extract data from eventViewAppointment
        String event = eventViewAppointment.getEvent();
        long startDate = eventViewAppointment.getStartDate();
        long endDate = eventViewAppointment.getEndDate();
        String location = eventViewAppointment.getLocation();
        String notes = eventViewAppointment.getNotes();
        long remind = eventViewAppointment.getRemind();
        int colour = eventViewAppointment.getColour();

        //Set Text and other miscellaneous setting
        EditText eventLabel = (EditText) view.findViewById(R.id.title);
        EditText locationLabel = (EditText) view.findViewById(R.id.appointmentLocation);
        EditText notesLabel = (EditText) view.findViewById(R.id.appointmentNotes);
        CheckBox reminderCheckBox = (CheckBox) view.findViewById(R.id.remindbox);

        Button beginDateButton = (Button) view.findViewById(R.id.startD);
        Button endDateButton = (Button) view.findViewById(R.id.endD);
        Button beginTimeButton = (Button) view.findViewById(R.id.startT);
        Button endTimeButton = (Button) view.findViewById(R.id.endT);

        Button remindNum = (Button) view.findViewById(R.id.remindnum);
        Button remindBox = (Button) view.findViewById(R.id.remindweek);

        //Return values previously input
        eventLabel.setText(event);
        locationLabel.setText(location);
        notesLabel.setText(notes);

        Constant.setButtonDatePicker(getActivity(), beginDateButton, startDate, "From     ");
        Constant.setButtonDatePicker(getActivity(), endDateButton, endDate,     "To         ");

        Constant.setButtonTimePicker(getActivity(), beginTimeButton, startDate, "");
        Constant.setButtonTimePicker(getActivity(), endTimeButton, endDate,     "");

        if (remind != 0)
        {
            reminderCheckBox.setChecked(true);
            //Get difference in time, in terms of millisecond
            long diff = startDate - remind;
            int setQuantity = 0;

            if(diff % Constant.MIN_IN_MILLISECOND == 0)
            {
                setQuantity = (int) (diff / Constant.MIN_IN_MILLISECOND);
                remindBox.setText("min(s) before event");
            }

            else if(diff % Constant.HOUR_IN_MILLISECOND == 0)
            {
                setQuantity = (int) (diff / Constant.HOUR_IN_MILLISECOND);
                remindBox.setText("hour(s) before event");
            }

            else if(diff % Constant.DAY_IN_MILLISECOND == 0)
            {
                setQuantity = (int) (diff / Constant.DAY_IN_MILLISECOND);
                remindBox.setText("day(s) before event");
            }

            remindNum.setText("" + setQuantity);
        }

        //Disable all interaction
        eventLabel.setEnabled(false);
        locationLabel.setEnabled(false);
        notesLabel.setEnabled(false);

        return view;
    }

}