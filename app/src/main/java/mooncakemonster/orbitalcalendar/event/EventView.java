package mooncakemonster.orbitalcalendar.event;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

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
    private Button colour_button, beginDateButton, endDateButton, beginTimeButton, endTimeButton, remindNum, remindBox, everyNum, everyBox, colourInput;
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
    public static EventView newInstance(Appointment appt) {
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
        // Prevent keyboard from appearing automatically
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //(1) Set Title for DialogFragment
        getDialog().setTitle("View Event");
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
                    getDialog().setTitle("Edit Event");
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
                    String beginD = beginDateButton.getText().toString();
                    final String beginT = beginTimeButton.getText().toString();
                    final long beginEventMillisecond = Constant.stringToMillisecond(beginD, beginT, Constant.DATEFORMATTER, Constant.TIMEFORMATTER);
                    //Standardised format for event's starting date: YYYY-MM-DD
                    final String startProperDate = Constant.standardYearMonthDate(beginD, Constant.DATEFORMATTER, Constant.YYYYMMDD_FORMATTER);
                    //End date and time
                    final String endD = endDateButton.getText().toString();
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
                    if(selected_colour == 0) selected_colour = R.color.redbear;
                    // Check if it is a few days event
                    long size = fewDaysAppointment(beginEventMillisecond, endEventMillisecond);
                    if(size > 1) {
                        for (int i = 0; i < size; i++) {
                            long current_day = beginEventMillisecond + (Constant.DAY_IN_MILLISECOND * i);
                            appointmentDatabase.createAppointment(i, beginEventMillisecond, event, startProperDate, current_day, endEventMillisecond, location, notes, remind, selected_colour);
                        }
                    } else {
                        appointmentDatabase.createAppointment(-1, beginEventMillisecond, event, startProperDate, beginEventMillisecond, endEventMillisecond, location, notes, remind, selected_colour);
                    }

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

    private long fewDaysAppointment(long start_milliseconds, long end_milliseconds) {
        return (end_milliseconds - start_milliseconds) / Constant.DAY_IN_MILLISECOND;
    }

    private void instantiateWidgets(View view)
    {
        //(1) Extract data from eventViewAppointment
        String event = eventViewAppointment.getEvent();
        long startDate = eventViewAppointment.getActualStartDate();
        long endDate = eventViewAppointment.getEndDate();
        String location = eventViewAppointment.getLocation();
        String notes = eventViewAppointment.getNotes();
        long remind = eventViewAppointment.getRemind();
        selected_colour = eventViewAppointment.getColour();

        //(2) Assign TextViews, Buttons and Checkboxes through findViewById
        colour_button = (Button) view.findViewById(R.id.colour_button);
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
        colourInput = (Button) view.findViewById(R.id.colour_button);
        colourInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        //(3) Assign respective string values and settings
        colour_button.setBackgroundResource(Constant.getPartyBearColour(selected_colour));
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
        colourInput.setEnabled(value);

        //Repeat Appointment capability will not be available here
        repeatAppointment.setEnabled(false);
        everyBox.setEnabled(false);
        everyNum.setEnabled(false);

        // Still set text colours to black when click is disabled
        if(!value) {
            int colour = getResources().getColor(R.color.black);
            eventLabel.setTextColor(colour);
            locationLabel.setTextColor(colour);
            notesLabel.setTextColor(colour);
            beginDateButton.setTextColor(colour);
            beginTimeButton.setTextColor(colour);
            endDateButton.setTextColor(colour);
            endTimeButton.setTextColor(colour);
            remindBox.setTextColor(colour);
            remindNum.setTextColor(colour);

            // Set line under textview
            //eventLabel.setBackgroundResource(R.drawable.textview_line);
            //locationLabel.setBackgroundResource(R.drawable.textview_line);
        }

        repeatAppointment.setVisibility(View.GONE);
        everyBox.setVisibility(View.GONE);
        everyNum.setVisibility(View.GONE);
    }


    // This method calls alert dialog to display the list of names.
    private void openDialog() {
        final View dialogview = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_colour, null);
        final SimpleDraweeView red = (SimpleDraweeView) dialogview.findViewById(R.id.imageButton1);
        final SimpleDraweeView yellow = (SimpleDraweeView) dialogview.findViewById(R.id.imageButton2);
        final SimpleDraweeView green = (SimpleDraweeView) dialogview.findViewById(R.id.imageButton3);
        final SimpleDraweeView blue = (SimpleDraweeView) dialogview.findViewById(R.id.imageButton4);
        final SimpleDraweeView purple = (SimpleDraweeView) dialogview.findViewById(R.id.imageButton5);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setTitle("Select colour");
        alertBuilder.setView(dialogview);

        // Default red
        setColour(R.color.redbear);

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColour(R.color.redbear);
                red.setBackgroundResource(R.drawable.sunred);
                yellow.setBackgroundResource(R.drawable.bearyellow);
                green.setBackgroundResource(R.drawable.beargreen);
                blue.setBackgroundResource(R.drawable.bearblue);
                purple.setBackgroundResource(R.drawable.bearpurple);
            }
        });

        yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColour(R.color.yellowbear);
                yellow.setBackgroundResource(R.drawable.sunyellow);
                red.setBackgroundResource(R.drawable.beared);
                green.setBackgroundResource(R.drawable.beargreen);
                blue.setBackgroundResource(R.drawable.bearblue);
                purple.setBackgroundResource(R.drawable.bearpurple);
            }
        });

        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColour(R.color.greenbear);
                green.setBackgroundResource(R.drawable.sungreen);
                red.setBackgroundResource(R.drawable.beared);
                yellow.setBackgroundResource(R.drawable.bearyellow);
                blue.setBackgroundResource(R.drawable.bearblue);
                purple.setBackgroundResource(R.drawable.bearpurple);
            }
        });

        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColour(R.color.bluebear);
                blue.setBackgroundResource(R.drawable.sunblue);
                red.setBackgroundResource(R.drawable.beared);
                yellow.setBackgroundResource(R.drawable.bearyellow);
                green.setBackgroundResource(R.drawable.beargreen);
                purple.setBackgroundResource(R.drawable.bearpurple);
            }
        });

        purple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColour(R.color.purplebear);
                purple.setBackgroundResource(R.drawable.sunpurple);
                red.setBackgroundResource(R.drawable.beared);
                yellow.setBackgroundResource(R.drawable.bearyellow);
                green.setBackgroundResource(R.drawable.beargreen);
                blue.setBackgroundResource(R.drawable.bearblue);
            }
        });

        alertBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                colourInput.setBackgroundResource(getPartyBear(selected_colour));
                dialog.dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog dialog = alertBuilder.create();
        dialog.show();
    }

    private void setColour(int colourInput) {
        this.selected_colour = colourInput;
    }

    private int getPartyBear(int colourInput) {
        switch(colourInput) {
            case R.color.redbear:
                return R.drawable.partyred;
            case R.color.yellowbear:
                return R.drawable.partyyellow;
            case R.color.greenbear:
                return R.drawable.partygreen;
            case R.color.bluebear:
                return R.drawable.partyblue;
            case R.color.purplebear:
                return R.drawable.partypurple;
        }

        // Should not reach here
        return -1;
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