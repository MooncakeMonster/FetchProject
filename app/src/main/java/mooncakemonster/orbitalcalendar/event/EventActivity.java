package mooncakemonster.orbitalcalendar.event;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.alarm.AlarmSetter;
import mooncakemonster.orbitalcalendar.database.AppointmentController;
import mooncakemonster.orbitalcalendar.database.Constant;

/*************************************************************************************************
 * Purpose: EventActivity.java is the interface which the user may input their appointments.
 *
 * Parameters of appointment are as follows:
 * (a) Title       (Mandatory Field)
 * (b) Location
 * (c) From Time   (Mandatory Field) (Format: dd/MM/yyyy, EEE    hh:mm a)
 * (d) To Time     (Mandatory Field) (Format: dd/MM/yyyy, EEE    hh:mm a)
 * (e) Option to set the same appointment(s) periodically     (e.g. daily, weekly, monthly, or yearly)
 * (f) Option to set alarm prior to appointment               (e.g. N minutes, hours, or days)
 * (g) Notes
 * (h) Colour code appointment
 *
 * Access via: Double clicking on any dates in CaldroidFragment
 * **************************************************************************************************/

public class EventActivity extends ActionBarActivity {

    //Request Code: Inform CalendarFragment if appointment was set, and whether to refresh CaldroidFragment
    public static final int APPOINTMENT_SET = 9;
    public static final int APPOINTMENT_NOT_SET = 8;

    //Buttons corresponding to R.layout.activity_event.xml
    private Button beginDate, endDate, beginTime, endTime, everyNum, everyBox, remindNum, remindBox, colourInput;
    //Default selected colour code for appointments
    private int selected_colour = 0;

    //AppointmentController to control/access SQLite database
    private AppointmentController appointmentDatabase;

    private static CharSequence[] everyWheel = {"day", "week", "month", "year"};
    private static CharSequence[] remindWheel = {"min", "hour", "day"};
    private final static String REMIND_TAG = " before event";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        getSupportActionBar().setElevation(0);

        //Store date passed from intent in millisecond. If unavailable, default to current time.
        long datePassedInMillisecond = 0;
        //Extract date from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            datePassedInMillisecond = extras.getLong("date_passed", -1L);
        }
        setDateFunction(datePassedInMillisecond);
        setCheckBoxFunction();

        //Initialise and open database
        appointmentDatabase = new AppointmentController(this);
        appointmentDatabase.open();

        //Initialise bear button
        colourInput = (Button) findViewById(R.id.selected_bear);
    }

    //Helper Method: Initialise date and time from intent
    private void setDateFunction(long datePassedInMillisecond) {
        //Assign widgets in R.layout.activity_event.xml to buttons
        beginDate = (Button) findViewById(R.id.startD);
        endDate = (Button) findViewById(R.id.endD);
        beginTime = (Button) findViewById(R.id.startT);
        endTime = (Button) findViewById(R.id.endT);

        //Set default value for date and time field
        Constant.setButtonDatePicker(EventActivity.this, beginDate, datePassedInMillisecond, "");
        Constant.setButtonDatePicker(EventActivity.this, endDate, datePassedInMillisecond, "");

        Constant.setButtonTimePicker(EventActivity.this, beginTime, datePassedInMillisecond, "");
        Constant.setButtonTimePicker(EventActivity.this, endTime, datePassedInMillisecond, "");
    }

    private void setCheckBoxFunction() {
        //Assign widgets in R.layout.activity_event.xml to buttons
        everyNum = (Button) findViewById(R.id.everynum);
        everyBox = (Button) findViewById(R.id.everyweek);
        remindNum = (Button) findViewById(R.id.remindnum);
        remindBox = (Button) findViewById(R.id.remindweek);

        //Set default value for repeat appointment and reminder field
        everyNum.setText("1");
        remindNum.setText("1");
        everyBox.setText(everyWheel[0]);
        remindBox.setText(remindWheel[0] + REMIND_TAG);

        Constant.setNumberDialogWheel(EventActivity.this, everyNum, everyBox, everyWheel, "");
        Constant.setNumberDialogWheel(EventActivity.this, remindNum, remindBox, remindWheel, REMIND_TAG);

        Constant.setTypeDialog(EventActivity.this, everyNum, everyBox, everyWheel, "");
        Constant.setTypeDialog(EventActivity.this, remindNum, remindBox, remindWheel, REMIND_TAG);
    }

    public void selectItem(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.everybox:
            case R.id.remindbox:
                break;

            //Set alarm sound
            /*
            case R.id.remindbox:
                if(checked) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 1);
                }
                break;
            */
        }
    }

    protected boolean insertInDatabase() {
        final EditText eventInput = (EditText) findViewById(R.id.title);
        final EditText locationInput = (EditText) findViewById(R.id.appointmentLocation);
        final EditText notesInput = (EditText) findViewById(R.id.appointmentNotes);

        final CheckBox repeatAppointment = (CheckBox) findViewById(R.id.everybox);
        final CheckBox reminderCheckBox = (CheckBox) findViewById(R.id.remindbox);

        if (beginDate == null || beginTime == null || endDate == null || endTime == null) {
            beginDate = (Button) findViewById(R.id.startD);
            endDate = (Button) findViewById(R.id.endD);
            beginTime = (Button) findViewById(R.id.startT);
            endTime = (Button) findViewById(R.id.endT);
        }

        //(1) Data Extraction Begins Here
        //Get Event Name
        final String event = eventInput.getText().toString();
        //Begin date and time
        final String beginD = beginDate.getText().toString();
        final String beginT = beginTime.getText().toString();
        final long beginEventMillisecond = Constant.stringToMillisecond(beginD, beginT, Constant.DATEFORMATTER, Constant.TIMEFORMATTER);
        //Standardised format for event's starting date: YYYY-MM-DD
        final String startProperDate = Constant.standardYearMonthDate(beginD, Constant.DATEFORMATTER, Constant.YYYYMMDD_FORMATTER);
        //End date and time
        final String endD = endDate.getText().toString();
        final String endT = endTime.getText().toString();
        final long endEventMillisecond = Constant.stringToMillisecond(endD, endT, Constant.DATEFORMATTER, Constant.TIMEFORMATTER);
        //Get Event's location
        final String location = locationInput.getText().toString();
        //Get any miscellaneous notes
        final String notes = notesInput.getText().toString();

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
            remind = beginEventMillisecond - num;

            //Set alarm
            AlarmSetter.setAlarm(getApplicationContext(), event, location, remind);
        }

        //(2) Start Validity Check
        // Ensure inputs are not of null value: (a) event
        if (Constant.minStringLength(event, 1, eventInput, null) == false)
            return false;
            // Check length of input: (a) event, (b) location, (c) notes
        else if (Constant.maxStringLength(event, Constant.EVENT_TITLE_MAX_LENGTH, eventInput,
                "No more than " + Constant.EVENT_TITLE_MAX_LENGTH + " characters for event.") == false)
            return false;
        else if (Constant.maxStringLength(location, Constant.LOCATION_MAX_LENGTH, locationInput,
                "No more than " + Constant.LOCATION_MAX_LENGTH + " characters for location.") == false)
            return false;
        else if (Constant.maxStringLength(notes, Constant.NOTES_MAX_LENGTH, notesInput,
                "No more than " + Constant.NOTES_MAX_LENGTH + " characters for notes.") == false)
            return false;
        // Ensure the dates selected make sense: (a) startDate, (b) endDate
        if (beginEventMillisecond > endEventMillisecond) {
            //If starting time occurs before ending time
            Toast.makeText(this.getApplicationContext(), "Please check the starting and ending date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (repeatAppointment.isChecked()) {
            //TODO: If checkbox, sent dialogbox, asking when is the limit date
            //BULK INSERT INTO DATABASE
            /*
            final DatePickerDialog.Builder alert = new DatePickerDialog.Builder(this);
            alert.setTitle("Set Recurring Event");
            alert.setMessage("Pick the maximum date for the event: ");

            alert.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Get date and start setting appointments
                    dialog.dismiss();
                }
            });

            alert.show();
            */

        } else {
            //Insert into database
            appointmentDatabase.createAppointment(event, startProperDate, beginEventMillisecond, endEventMillisecond, location, notes, remind, selected_colour);
        }

        return true;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addAppointmentButton:
                if (insertInDatabase()) {
                    //Inform user that appointment has been created and return to previous activity
                    Toast.makeText(this.getApplicationContext(), "Appointment set successfully.", Toast.LENGTH_SHORT).show();

                    if (getParent() == null) {
                        setResult(EventActivity.APPOINTMENT_SET);
                    } else {
                        getParent().setResult(EventActivity.APPOINTMENT_SET);
                    }

                    finish();
                }
                break;

            case R.id.cancelButton:

                if (getParent() == null) {
                    setResult(EventActivity.APPOINTMENT_NOT_SET);
                } else {
                    getParent().setResult(EventActivity.APPOINTMENT_NOT_SET);
                }

                finish();
                break;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cross, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    @Override
    protected void onPause()
    {
        super.onPause();
        if(appointmentDatabase != null)
        {
            appointmentDatabase.close();
            appointmentDatabase = null;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(appointmentDatabase == null)
        {
            appointmentDatabase = new AppointmentController(this);
            appointmentDatabase.open();
        }
    }
    */
}