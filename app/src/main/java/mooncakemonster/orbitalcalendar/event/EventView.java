package mooncakemonster.orbitalcalendar.event;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.AppointmentController;
import mooncakemonster.orbitalcalendar.database.Constant;

/**************************************************************************************************
 * Purpose: EventView.java allows user to toggle between viewing a previously set appointment
 * and editing it.
 *
 * Access via: Click on the menu button on top left corner, then "Events", then any Appointments,
 * followed by "Edit Event"
 **************************************************************************************************/
public class EventView extends DialogFragment
{
    private Appointment eventViewAppointment;

    private EditText eventLabel, locationLabel,  notesLabel;
    private Button beginDateButton, endDateButton, beginTimeButton, endTimeButton, remindNum, remindBox, everyNum, everyBox, colourInput;
    private CheckBox reminderCheckBox, repeatAppointment;

    private static CharSequence[] everyWheel = {"day", "week", "month", "year"};
    private static CharSequence[] remindWheel = {"min", "hour", "day"};

    //AppointmentController variable to control the SQLite database
    private AppointmentController appointmentDatabase;

    //Default selected colour code for appointments
    private int selected_colour = 0;

    public EventView() {
        //Nothing
    }

    //Allow only one instance of EventView at any one time
    static EventView newInstance(Appointment appt) {
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

        Bundle bundle = getArguments();
        eventViewAppointment = (Appointment) bundle.getSerializable("appointment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        //(1) Set Title for DialogFragment
        getDialog().setTitle("View/Edit Event");
        View view = inflater.inflate(R.layout.activity_event, container, false);
        //(2) Instantiate widgets
        instantiateWidgets(view);
        //(3) Disable all interaction (i.e. Prevent users for making edits)
        toggle(false);
        //(4) Modify the create button, such that it shows "Edit" -> "Set New Appointment" when clicked on
        final Button editButton = (Button) view.findViewById(R.id.addAppointmentButton);
        editButton.setText("EDIT");
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editButton.getText().toString().equals("EDIT")) {
                    toggle(true);
                    editButton.setText("SAVE");
                    //Initialise and open database
                    appointmentDatabase = new AppointmentController(getActivity());
                    appointmentDatabase.open();
                }

                else if(editButton.getText().toString().equals("SAVE")) {
                    //Insert new appointment into database
                    //(1) Data Extraction Begins Here
                    //Get Event Name
                    final String event = eventLabel.getText().toString();
                    //Begin date and time
                    String beginD = beginDateButton.getText().toString().replace("From     ", "");
                    final String beginT = beginTimeButton.getText().toString();
                    final long beginEventMillisecond = Constant.stringToMillisecond(beginD, beginT, Constant.DATEFORMATTER, Constant.TIMEFORMATTER);
                    //Standardised format for event's starting date: YYYY-MM-DD
                    final String startProperDate = Constant.standardYearMonthDate(beginD, Constant.DATEFORMATTER, Constant.YYYYMMDD_FORMATTER);
                    //End date and time
                    final String endD = endDateButton.getText().toString().replace("To         ", "");
                    final String endT = endTimeButton.getText().toString();
                    final long endEventMillisecond = Constant.stringToMillisecond(endD, endT, Constant.DATEFORMATTER, Constant.TIMEFORMATTER);
                    //Get Event's location
                    final String location = locationLabel.getText().toString();
                    //Get any miscellaneous notes
                    final String notes = notesLabel.getText().toString();

                    //Default value for reminder
                    long remind = 0;
                    if (reminderCheckBox.isChecked()) {
                        //Get number, removing any whitespace
                        long num = Long.parseLong(remindNum.getText().toString().replaceAll("\\s+", ""));
                        //Get "quantity"
                        String value = remindBox.getText().toString();

                        switch (value) {
                            case "min before event":
                            case "mins before event":
                                num = num * Constant.MIN_IN_MILLISECOND;
                                break;
                            case "hour before event":
                            case "hours before event":
                                num = num * Constant.HOUR_IN_MILLISECOND;
                                break;
                            case "day before event":
                            case "days before event":
                                num = num * Constant.DAY_IN_MILLISECOND;
                                break;
                        }
                        //Set reminder in milliseconds
                        remind = endEventMillisecond - num;
                    }

                    //(2) Start Validity Check
                    // Ensure inputs are not of null value: (a) event
                    if (Constant.minStringLength(event, 1, eventLabel, null) == false)
                        return;
                        // Check length of input: (a) event, (b) location, (c) notes
                    else if (Constant.maxStringLength(event, Constant.EVENT_TITLE_MAX_LENGTH, eventLabel,
                            "No more than " + Constant.EVENT_TITLE_MAX_LENGTH + " characters for event.") == false)
                        return;
                    else if (Constant.maxStringLength(location, Constant.LOCATION_MAX_LENGTH, locationLabel,
                            "No more than " + Constant.LOCATION_MAX_LENGTH + " characters for location.") == false)
                        return;
                    else if (Constant.maxStringLength(notes, Constant.NOTES_MAX_LENGTH, notesLabel,
                            "No more than " + Constant.NOTES_MAX_LENGTH + " characters for notes.") == false)
                        return;
                    // Ensure the dates selected make sense: (a) startDate, (b) endDate
                    if (beginEventMillisecond > endEventMillisecond) {
                        //If starting time occurs before ending time
                        Toast.makeText(getActivity().getApplicationContext(), "Please check the starting and ending date", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //Insert into database
                    appointmentDatabase.createAppointment(event, startProperDate, beginEventMillisecond, endEventMillisecond, location, notes, remind, selected_colour);
                    //Delete current appointment
                    appointmentDatabase.deleteAppointment(eventViewAppointment);
                    //Close dialog
                    getDialog().dismiss();

                }
            }
        });

        //(7) Cancel button to be added
        final Button cancelButton = (Button) view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    private void instantiateWidgets(View view)
    {
        //(1) Extract data from eventViewAppointment
        String event = eventViewAppointment.getEvent();
        long startDate = eventViewAppointment.getStartDate();
        long endDate = eventViewAppointment.getEndDate();
        String location = eventViewAppointment.getLocation();
        String notes = eventViewAppointment.getNotes();
        long remind = eventViewAppointment.getRemind();
        selected_colour = eventViewAppointment.getColour();

        //(2) Assign TextViews, Buttons and Checkboxes through findViewById
        eventLabel = (EditText) view.findViewById(R.id.title);
        locationLabel = (EditText) view.findViewById(R.id.appointmentLocation);
        notesLabel = (EditText) view.findViewById(R.id.appointmentNotes);

        beginDateButton = (Button) view.findViewById(R.id.startD);
        endDateButton = (Button) view.findViewById(R.id.endD);
        beginTimeButton = (Button) view.findViewById(R.id.startT);
        endTimeButton = (Button) view.findViewById(R.id.endT);

        reminderCheckBox = (CheckBox) view.findViewById(R.id.remindbox);
        remindNum = (Button) view.findViewById(R.id.remindnum);
        remindBox = (Button) view.findViewById(R.id.remindweek);

        repeatAppointment = (CheckBox) view.findViewById(R.id.everybox);
        everyNum = (Button) view.findViewById(R.id.everynum);
        everyBox = (Button) view.findViewById(R.id.everyweek);

        //Set bear color
        colourInput = (Button) view.findViewById(R.id.selected_bear);

        switch (selected_colour) {
            case R.color.redbear:
                colourInput.setBackgroundResource(R.drawable.beared);
                break;
            case R.color.yellowbear:
                colourInput.setBackgroundResource(R.drawable.bearyellow);
                break;
            case R.color.greenbear:
                colourInput.setBackgroundResource(R.drawable.beargreen);
                break;
            case R.color.bluebear:
                colourInput.setBackgroundResource(R.drawable.bearblue);
                break;
            case R.color.purplebear:
                colourInput.setBackgroundResource(R.drawable.bearpurple);
                break;
            default:
                break;
        }

        //(3) Assign respective string values and settings
        eventLabel.setText(event);
        locationLabel.setText(location);
        notesLabel.setText(notes);

        Constant.setNumberDialogWheel(getActivity(), remindNum, remindBox, remindWheel, EventActivity.REMIND_TAG);
        Constant.setTypeDialog(getActivity(), remindNum, remindBox, remindWheel, EventActivity.REMIND_TAG);

        Constant.setButtonDatePicker(getActivity(), beginDateButton, startDate, "");
        Constant.setButtonDatePicker(getActivity(), endDateButton, endDate,     "");

        Constant.setButtonTimePicker(getActivity(), beginTimeButton, startDate, "");
        Constant.setButtonTimePicker(getActivity(), endTimeButton, endDate,     "");

        if (remind != 0) {
            reminderCheckBox.setChecked(true);
            //Get difference in time, in terms of millisecond
            long diff = startDate - remind;
            int setQuantity = 0;

            if (diff % Constant.MIN_IN_MILLISECOND == 0) {
                setQuantity = (int) (diff / Constant.MIN_IN_MILLISECOND);
                remindBox.setText("min(s) before event");
            } else if (diff % Constant.HOUR_IN_MILLISECOND == 0) {
                setQuantity = (int) (diff / Constant.HOUR_IN_MILLISECOND);
                remindBox.setText("hour(s) before event");
            } else if (diff % Constant.DAY_IN_MILLISECOND == 0) {
                setQuantity = (int) (diff / Constant.DAY_IN_MILLISECOND);
                remindBox.setText("day(s) before event");
            }
            remindNum.setText("" + setQuantity);
        } else {
            remindNum.setText("1");
            remindBox.setText(remindWheel[0] + EventActivity.REMIND_TAG);
        }

        //Tentatively set all repeat/non-repeat as non-repeat
        Constant.setNumberDialogWheel(getActivity(), everyNum, everyBox, everyWheel, "");
        Constant.setTypeDialog(getActivity(), everyNum, everyBox, everyWheel, "");

        everyNum.setText("1");
        everyBox.setText(everyWheel[0]);

    }

    private void toggle(boolean value) {
        eventLabel.setEnabled(value);
        locationLabel.setEnabled(value);
        notesLabel.setEnabled(value);

        beginDateButton.setEnabled(value);
        beginTimeButton.setEnabled(value);
        endDateButton.setEnabled(value);
        endTimeButton.setEnabled(value);

        reminderCheckBox.setEnabled(value);
        remindBox.setEnabled(value);
        remindNum.setEnabled(value);

        //Repeat Appointment capability will not be available here
        repeatAppointment.setEnabled(false);
        everyBox.setEnabled(false);
        everyNum.setEnabled(false);

        repeatAppointment.setVisibility(View.GONE);
        everyBox.setVisibility(View.GONE);
        everyNum.setVisibility(View.GONE);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButton:
                colourInput.setBackgroundResource(R.drawable.beared);
                selected_colour = R.color.redbear;
                break;
            case R.id.imageButton2:
                colourInput.setBackgroundResource(R.drawable.bearyellow);
                selected_colour = R.color.yellowbear;
                break;
            case R.id.imageButton3:
                colourInput.setBackgroundResource(R.drawable.beargreen);
                selected_colour = R.color.greenbear;
                break;
            case R.id.imageButton4:
                colourInput.setBackgroundResource(R.drawable.bearblue);
                selected_colour = R.color.bluebear;
                break;
            case R.id.imageButton6:
                colourInput.setBackgroundResource(R.drawable.bearpurple);
                selected_colour = R.color.purplebear;
                break;
            default:
                break;
        }
    }

    /*
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

    @Override
    public void onResume()
    {
        super.onResume();
        if(appointmentDatabase == null)
        {
            appointmentDatabase = new AppointmentController(getActivity());
            appointmentDatabase.open();
        }
    }
    */

}