package mooncakemonster.orbitalcalendar.event;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import mooncakemonster.orbitalcalendar.R;

public class EventActivity extends Activity {

    //Variable for extracting date from incoming intent. Default is current time.
    private Calendar dateTime = Calendar.getInstance();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy, EEE");
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
    //Link buttons with the respective id
    private Button beginDate = (Button) findViewById(R.id.startD);
    private Button endDate = (Button) findViewById(R.id.endD);
    private Button beginTime = (Button) findViewById(R.id.startT);
    private Button endTime = (Button) findViewById(R.id.endT);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        //Extract date from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            dateTime.setTimeInMillis(extras.getLong("date_passed", -1L));
        }

        setButtonFunction();
    }

    // This method sets selected date by user on the button.
    protected Dialog setButtonFunction() {
        beginDate.setText("From     " + dateFormatter.format(dateTime.getTime()));
        endDate.setText("To         " + dateFormatter.format(dateTime.getTime()));
        beginTime.setText(timeFormatter.format(dateTime.getTime()));
        endTime.setText(timeFormatter.format(dateTime.getTime()));

        beginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog date = new DatePickerDialog(EventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateTime.set(year, monthOfYear, dayOfMonth);
                        beginDate.setText("From     " + dateFormatter.format(dateTime.getTime()));
                    }
                }, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH));
                date.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog date = new DatePickerDialog(EventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateTime.set(year, monthOfYear, dayOfMonth);
                        endDate.setText("To         " + dateFormatter.format(dateTime.getTime()));
                    }
                }, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH));
                date.show();
            }
        });

        beginTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog time = new TimePickerDialog(EventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateTime.set(Calendar.MINUTE, minute);
                        beginTime.setText(timeFormatter.format(dateTime.getTime()));
                    }
                }, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), false);
                time.show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog time = new TimePickerDialog(EventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateTime.set(Calendar.MINUTE, minute);
                        endTime.setText(timeFormatter.format(dateTime.getTime()));
                    }
                }, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), false);
                time.show();
            }
        });

        return null;
    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.addAppointmentButton:
                insertInDatabase();
        }
    }

    protected void insertInDatabase()
    {
        final EditText eventInput = (EditText) findViewById(R.id.title);
        final EditText locationInput = (EditText) findViewById(R.id.appointmentLocation);
        final EditText notesInput = (EditText) findViewById(R.id.appointmentNotes);

        //Data parsing begins here
        final String event = eventInput.getText().toString();
        //Begin date and time
        final String beginD = beginDate.getText().toString();
        final String beginT = beginTime.getText().toString();
        Log.i("TIME LOOKS LIKE THIS", "TIME LOOKS LIKE THIS: BeginD => " + beginD + "     BeginT => " + beginT);
        final long beginEvent;
        //End date and time
        final String endD = endDate.getText().toString();
        final String endT = endTime.getText().toString();
        final long endEvent;
        final String location = locationInput.getText().toString();
        final String notes = notesInput.getText().toString();

        //TODO: FIND SUITABLE REPLACMENT FOR EDITTEXT FOR CHECKBOX
        //final EditText remindInput = (EditText) findViewById(R.id.appointmentReminder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}