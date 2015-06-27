package mooncakemonster.orbitalcalendar.event;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.AppointmentController;
import mooncakemonster.orbitalcalendar.database.Constant;


/*
 * Purpose: EventActivity.java is the interface which the user may input their
 * Access via: Clicking on any dates in the CaldroidFragment
 */

public class EventActivity extends ActionBarActivity {

    //REquest Code
    public static final int APPOINTMENT_SET = 9;
    public static final int APPOINTMENT_NOT_SET = 8;

    //Variable for extracting date from incoming intent. Default is current time.
    private Button beginDate, endDate, beginTime, endTime, everyNum, everyBox, remindNum, remindBox;

    //String value for "From" and "To" in the interface
    private String fromStringValue = "From     ";
    private String toStringValue = "To         ";

    //AppointmentController variable to control the SQLite database
    private AppointmentController appointmentDatabase;

    final Context context = this;
    NumberPicker numberPicker;
    AlertDialog.Builder alertBw1, alertBw2;
    AlertDialog alertDw1, alertDw2;

    // Select bear colours
    Button colourInput;
    int selected_colour = 0;

    CharSequence[] everyWheel = { "day event", "week event", "month event", "year event"};
    CharSequence[] remindWheel = { "min before event", "hour before event", "day before event" };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        getSupportActionBar().setElevation(0);

        //To store the date passed from intent in millisecond
        long datePassedInMillisecond = 0;
        //Extract date from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            datePassedInMillisecond = extras.getLong("date_passed", -1L);
        }

        setButtonFunction(datePassedInMillisecond);
        setCheckBoxFunction();

        //Initialise and open database
        appointmentDatabase = new AppointmentController(this);
        appointmentDatabase.open();

        //Initialise bear button
        colourInput = (Button) findViewById(R.id.selected_bear);
    }

    // This method sets selected date by user on the button.
    protected Dialog setButtonFunction(long datePassedInMillisecond) {

        // NullPointer if the following is not coded in this method:
        beginDate = (Button) findViewById(R.id.startD);
        endDate = (Button) findViewById(R.id.endD);
        beginTime = (Button) findViewById(R.id.startT);
        endTime = (Button) findViewById(R.id.endT);

        Constant.setButtonDatePicker(EventActivity.this, beginDate, datePassedInMillisecond, fromStringValue );
        Constant.setButtonDatePicker(EventActivity.this, endDate, datePassedInMillisecond,   toStringValue);

        Constant.setButtonTimePicker(EventActivity.this, beginTime, datePassedInMillisecond, "");
        Constant.setButtonTimePicker(EventActivity.this, endTime, datePassedInMillisecond,   "");

        return null;
    }

    public void setCheckBoxFunction() {
        everyNum = (Button) findViewById(R.id.everynum);
        everyBox = (Button) findViewById(R.id.everyweek);
        remindNum = (Button) findViewById(R.id.remindnum);
        remindBox = (Button) findViewById(R.id.remindweek);

        everyNum.setText("1");
        everyBox.setText("day event");
        remindNum.setText("1");
        remindBox.setText("min before event");

        everyNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlert1();
                alertDw1.show();
            }
        });

        remindNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlert2();
                alertDw2.show();
            }
        });

        everyBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder build1 = new AlertDialog.Builder(EventActivity.this);
                build1.setTitle("Select type");
                build1.setSingleChoiceItems(everyWheel, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!everyNum.getText().equals("1") && which == 0)
                            everyBox.setText("days event");
                        else if (!everyNum.getText().equals("1") && which == 1)
                            everyBox.setText("weeks event");
                        else if (!everyNum.getText().equals("1") && which == 2)
                            everyBox.setText("months event");
                        else if (!everyNum.getText().equals("1") && which == 3)
                            everyBox.setText("years event");
                        else everyBox.setText(everyWheel[which]);
                    }
                }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog d1 = build1.create();
                d1.show();
            }
        });

        remindBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder build2 = new AlertDialog.Builder(EventActivity.this);
                build2.setTitle("Select type");
                build2.setSingleChoiceItems(remindWheel, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!remindNum.getText().equals("1") && which == 0) remindBox.setText("mins before event");
                        else if (!remindNum.getText().equals("1") && which == 1) remindBox.setText("hours before event");
                        else if (!remindNum.getText().equals("1") && which == 2) remindBox.setText("days before event");
                        else remindBox.setText(remindWheel[which]);
                    }
                }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog d2 = build2.create();
                d2.show();
            }
        });
    }

    // This method opens dialog for everyNum button.
    private void setAlert1() {
        RelativeLayout relativeLayout = setNumberPicker();

        alertBw1 = new AlertDialog.Builder(context);
        alertBw1.setTitle("Select number");

        alertBw1.setView(relativeLayout).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                everyNum.setText(Integer.toString(numberPicker.getValue()));
                //Remove 's' from days, weeks, months and years when 1 is selected.
                //Conversely, append 's' to day, week, month, and year when value greater than 1 selected.
                if (everyNum.getText().toString().equals("1")) {
                    String frequencyOfAppointment = everyBox.getText().toString();
                    switch (frequencyOfAppointment) {
                        case "days event":      everyBox.setText("day event");      break;
                        case "weeks event":     everyBox.setText("week event");     break;
                        case "months event":    everyBox.setText("month event");    break;
                        case "years event":     everyBox.setText("year event");     break;
                    }
                } else if (!everyNum.getText().toString().equals("1")) {
                    String frequencyOfAppointment = everyBox.getText().toString();
                    switch (frequencyOfAppointment) {
                        case "day event":       everyBox.setText("days event");     break;
                        case "week event":      everyBox.setText("weeks event");    break;
                        case "month event":     everyBox.setText("months event");   break;
                        case "year event":      everyBox.setText("years event");    break;
                    }
                }

                dialog.dismiss();
            }
        });

        alertDw1 = alertBw1.create();
    }

    // This method opens dialog for remindNum button.
    private void setAlert2() {
        RelativeLayout relativeLayout = setNumberPicker();

        alertBw2 = new AlertDialog.Builder(context);
        alertBw2.setTitle("Select number");

        alertBw2.setView(relativeLayout).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                remindNum.setText(Integer.toString(numberPicker.getValue()));
                //Remove 's' from mins, hours, and days when 1 is selected.
                //Conversely, append 's' to min, hour, and day when value greater than 1 selected.
                if (remindNum.getText().toString().equals("1")) {
                    String reminderOption = remindBox.getText().toString();
                    switch (reminderOption) {
                        case "mins before event":   remindBox.setText("min before event");      break;
                        case "hours before event":  remindBox.setText("hour before event");     break;
                        case "days before event":   remindBox.setText("day before event");      break;
                    }
                } else if (!remindNum.getText().toString().equals("1")) {
                    String reminderOption = remindBox.getText().toString();
                    switch (reminderOption) {
                        case "min before event":    remindBox.setText("mins before event");     break;
                        case "hour before event":   remindBox.setText("hours before event");    break;
                        case "day before event":    remindBox.setText("days before event");     break;
                    }
                }

                dialog.dismiss();
            }
        });

        alertDw2 = alertBw2.create();
    }

    // This method initialize number picker.
    private RelativeLayout setNumberPicker() {
        numberPicker = new NumberPicker(context);
        numberPicker.setClickable(false);
        numberPicker.setEnabled(true);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(100);

        final RelativeLayout linearLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams numPickerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        numPickerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        linearLayout.setLayoutParams(params);
        linearLayout.addView(numberPicker, numPickerParams);
        linearLayout.isClickable();

        return linearLayout;
    }

    public void selectItem(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.everybox:
                break;
            case R.id.remindbox:
                if(checked) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 1);
                }
                break;
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
        final String beginD = beginDate.getText().toString().replace(fromStringValue, "");
        final String beginT = beginTime.getText().toString();
        final long beginEventMillisecond = Constant.stringToMillisecond(beginD, beginT, Constant.DATEFORMATTER, Constant.TIMEFORMATTER);
        //Standardised format for event's starting date: YYYY-MM-DD
        final String startProperDate = Constant.standardYearMonthDate(beginD, Constant.DATEFORMATTER, new SimpleDateFormat("yyyy MM dd"));
        //End date and time
        final String endD = endDate.getText().toString().replace(toStringValue, "");
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
            remind = endEventMillisecond - num;
        }

        //(2) Start Validity Check
        // Ensure inputs are not of null value: (a) event
        if(Constant.minStringLength(event, 1, eventInput, null) == false)
            return false;
            // Check length of input: (a) event, (b) location, (c) notes
        else if(Constant.maxStringLength(event, Constant.EVENT_TITLE_MAX_LENGTH, eventInput,
                "No more than " + Constant.EVENT_TITLE_MAX_LENGTH + " characters for event.") == false)
            return false;
        else if(Constant.maxStringLength(location, Constant.LOCATION_MAX_LENGTH, locationInput,
                "No more than " + Constant.LOCATION_MAX_LENGTH + " characters for location.") == false)
            return false;
        else if(Constant.maxStringLength(notes, Constant.NOTES_MAX_LENGTH, notesInput,
                "No more than " + Constant.NOTES_MAX_LENGTH + " characters for notes.") == false)
            return false;
        // Ensure the dates selected make sense: (a) startDate, (b) endDate
        if(beginEventMillisecond > endEventMillisecond)
        {
            //If starting time occurs before ending time
            Toast.makeText(this.getApplicationContext(), "Please check the starting and ending date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (repeatAppointment.isChecked())
        {
            //TODO: If checkbox, sent dialogbox, asking when is the limit date
            //BULK INSERT INTO DATABASE
            /*DatePickerDialog.Builder alert = new DatePickerDialog.Builder(this);
            alert.setTitle("Set Recurring Event");
            alert.setMessage("Pick the maximum date for the event: ");

            alert.show();*/
        }
        else
        {
            //Insert into database
            appointmentDatabase.createAppointment(event, startProperDate, beginEventMillisecond, endEventMillisecond, location, notes, remind, selected_colour);
        }

        return true;
    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.addAppointmentButton:
                if(insertInDatabase()) {
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

            default: break;
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