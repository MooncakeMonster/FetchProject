package mooncakemonster.orbitalcalendar.event;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Calendar;

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
    public final static String REMIND_TAG = " before event";

    //Obtain maximm date before repetition
    long maxDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        getSupportActionBar().setElevation(0);
        // Prevent keyboard from appearing automatically
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

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
        colourInput = (Button) findViewById(R.id.colour_button);
        colourInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
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
                if(checked){
                    launchMaxDatePicker();
                }
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

    private void launchMaxDatePicker(){
        final String beginD = beginDate.getText().toString();
        final String beginT = beginTime.getText().toString();
        final long beginEventMillisecond = Constant.stringToMillisecond(beginD, beginT, Constant.DATEFORMATTER, Constant.TIMEFORMATTER);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(beginEventMillisecond);

        final DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //Set as empty as method is not called.
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        //datePicker.setTitle("Set Recurring Event's Limit");
        datePicker.getDatePicker().setMinDate(beginEventMillisecond);
        datePicker.setCancelable(false);
        datePicker.setButton(DialogInterface.BUTTON_POSITIVE, "Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                DatePicker temp = datePicker.getDatePicker();
                calendar.clear();
                calendar.set(temp.getYear(), temp.getMonth(), temp.getDayOfMonth());
                maxDate = calendar.getTimeInMillis();
                dialog.dismiss();
            }
        });
        datePicker.show();
    }

    // This method calls alert dialog to display the list of names.
    private void openDialog() {
        final View dialogview = LayoutInflater.from(this).inflate(R.layout.dialog_colour, null);
        final SimpleDraweeView red = (SimpleDraweeView) dialogview.findViewById(R.id.imageButton1);
        final SimpleDraweeView yellow = (SimpleDraweeView) dialogview.findViewById(R.id.imageButton2);
        final SimpleDraweeView green = (SimpleDraweeView) dialogview.findViewById(R.id.imageButton3);
        final SimpleDraweeView blue = (SimpleDraweeView) dialogview.findViewById(R.id.imageButton4);
        final SimpleDraweeView purple = (SimpleDraweeView) dialogview.findViewById(R.id.imageButton5);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
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

    protected boolean insertInDatabase() {
        final EditText eventInput = (EditText) findViewById(R.id.title);
        final EditText locationInput = (EditText) findViewById(R.id.appointmentLocation);
        final EditText notesInput = (EditText) findViewById(R.id.appointmentNotes);

        final CheckBox repeatAppointment = (CheckBox) findViewById(R.id.everybox);
        final CheckBox reminderCheckBox = (CheckBox) findViewById(R.id.remindbox);

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

        //(2) Start Validity Check
        // Ensure inputs are not of null value: (a) event
        if (!Constant.minStringLength(event, 1, eventInput, null))
            return false;
        if (!Constant.minStringLength(location, 1, locationInput, null))
            return false;
            // Check length of input: (a) event, (b) location, (c) notes
        else if (!Constant.maxStringLength(event, Constant.EVENT_TITLE_MAX_LENGTH, eventInput,
                "No more than " + Constant.EVENT_TITLE_MAX_LENGTH + " characters for event."))
            return false;
        else if (!Constant.maxStringLength(location, Constant.LOCATION_MAX_LENGTH, locationInput,
                "No more than " + Constant.LOCATION_MAX_LENGTH + " characters for location."))
            return false;
        else if (!Constant.maxStringLength(notes, Constant.NOTES_MAX_LENGTH, notesInput,
                "No more than " + Constant.NOTES_MAX_LENGTH + " characters for notes."))
            return false;
            // Ensure the dates selected make sense: (a) startDate, (b) endDate
        else if (beginEventMillisecond > endEventMillisecond) {
            //If starting time occurs before ending time
            Toast.makeText(this.getApplicationContext(), "Please check the starting and ending date", Toast.LENGTH_SHORT).show();
            return false;
        }

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
        }

        if (repeatAppointment.isChecked()) {
            //Get number, removing any whitespace
            int factor = Integer.parseInt(everyNum.getText().toString().replaceAll("\\s+", ""));
            Calendar start = Calendar.getInstance();
            start.setTimeInMillis(beginEventMillisecond);
            Calendar end = Calendar.getInstance();
            end.setTimeInMillis(endEventMillisecond);
            //Get "quantity"
            String value = everyBox.getText().toString();

            switch (value) {
                case "day":
                case "days":
                    while(start.getTimeInMillis() <= maxDate)
                    {
                        long startMil = start.getTimeInMillis();
                        long endMil = end.getTimeInMillis();

                        String startRepeatedProperDate = Constant.getDate(startMil, "yyyy MM dd");
                        appointmentDatabase.createAppointment(event, startRepeatedProperDate, startMil, endMil, location, notes, remind, selected_colour);

                        start.add(Calendar.DATE, factor);
                        end.add(Calendar.DATE, factor);
                    }
                    break;
                case "week":
                case "weeks":
                    factor *= 7;
                    while(start.getTimeInMillis() <= maxDate)
                    {
                        long startMil = start.getTimeInMillis();
                        long endMil = end.getTimeInMillis();
                        String startRepeatedProperDate = Constant.getDate(startMil, "yyyy MM dd");
                        appointmentDatabase.createAppointment(event, startRepeatedProperDate, startMil, endMil, location, notes, remind, selected_colour);

                        start.add(Calendar.DATE, factor);
                        end.add(Calendar.DATE, factor);
                    }
                    break;
                case "month":
                case "months":
                    while(start.getTimeInMillis() <= maxDate)
                    {
                        long startMil = start.getTimeInMillis();
                        long endMil = end.getTimeInMillis();
                        String startRepeatedProperDate = Constant.getDate(startMil, "yyyy MM dd");
                        appointmentDatabase.createAppointment(event, startRepeatedProperDate, startMil, endMil, location, notes, remind, selected_colour);

                        start.add(Calendar.MONTH, factor);
                        end.add(Calendar.MONTH, factor);
                    }
                    break;
                case "year":
                case "years":
                    while(start.getTimeInMillis() <= maxDate)
                    {
                        long startMil = start.getTimeInMillis();
                        long endMil = end.getTimeInMillis();
                        String startRepeatedProperDate = Constant.getDate(startMil, "yyyy MM dd");
                        appointmentDatabase.createAppointment(event, startRepeatedProperDate, startMil, endMil, location, notes, remind, selected_colour);

                        start.add(Calendar.YEAR, factor);
                        end.add(Calendar.YEAR, factor);
                    }
                    break;
            }



        } else {
            //Set alarm
            AlarmSetter.setAlarm(getApplicationContext(), event, location, remind);
            //Insert into database
            if(selected_colour == 0) selected_colour = R.color.redbear;
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


            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
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