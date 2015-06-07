package mooncakemonster.orbitalcalendar.event;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.AppointmentController;

public class EventActivity extends Activity {

    //Variable for extracting date from incoming intent. Default is current time.
    private Calendar dateTime = Calendar.getInstance();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy, EEE");
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
    private Button beginDate, endDate, beginTime, endTime, everyNum, everyBox, remindNum, remindBox;

    //AppointmentController variable to control the SQLite database
    private AppointmentController appointmentDatabase;

    final Context context = this;
    NumberPicker numberPicker;
    AlertDialog.Builder alertBw1, alertBw2;
    AlertDialog alertDw1, alertDw2;

    CharSequence[] everyWheel = { "day event", "week event", "month event", "year event"};
    CharSequence[] remindWheel = { "min before event", "hour before event", "day before event" };

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
        setCheckBoxFunction();
    }

    // This method sets selected date by user on the button.
    protected Dialog setButtonFunction() {

        // NullPointer if the following is not coded in this method:
        beginDate = (Button) findViewById(R.id.startD);
        endDate = (Button) findViewById(R.id.endD);
        beginTime = (Button) findViewById(R.id.startT);
        endTime = (Button) findViewById(R.id.endT);

        final String dateFormat = dateFormatter.format(dateTime.getTime());
        final String timeFormat = timeFormatter.format(dateTime.getTime());

        beginDate.setText("From     " + dateFormat);
        endDate.setText("To         " + dateFormat);
        beginTime.setText(timeFormat);
        endTime.setText(timeFormat);

        beginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog date = new DatePickerDialog(EventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateTime.set(year, monthOfYear, dayOfMonth);
                        beginDate.setText("From     " + dateFormat);
                    }
                }, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH));
                date.setTitle(dateFormat);
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
                        endDate.setText("To         " + dateFormat);
                    }
                }, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH));
                date.setTitle(dateFormat);
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
                        beginTime.setText(timeFormat);
                    }
                }, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), false);
                time.setTitle(timeFormat);
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
                        endTime.setText(timeFormat);
                    }
                }, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), false);
                time.setTitle(timeFormat);
                time.show();
            }
        });

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
                        if (!remindNum.getText().equals("1") && which == 0)
                            remindBox.setText("mins before event");
                        else if (!remindNum.getText().equals("1") && which == 1)
                            remindBox.setText("hours before event");
                        else if (!remindNum.getText().equals("1") && which == 2)
                            remindBox.setText("days before event");
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
        //TODO: Reduce similar codes as compared to setAlert2()
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

        alertBw1 = new AlertDialog.Builder(context);
        alertBw1.setTitle("Select number");

        alertBw1.setView(linearLayout).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                everyNum.setText(Integer.toString(numberPicker.getValue()));
                //TODO: When user choose 1, remove 's' doesn't work
                if (everyNum.equals("1") && everyBox.getText().equals("days event"))
                    everyBox.setText("day event");
                else if (everyNum.equals("1") && everyBox.getText().equals("weeks event"))
                    everyBox.setText("week event");
                else if (everyNum.equals("1") && everyBox.getText().equals("months event"))
                    everyBox.setText("month event");
                else if (everyNum.equals("1") && everyBox.getText().equals("years event"))
                    everyBox.setText("year event");

                else if (!everyNum.equals("1") && everyBox.getText().equals("day event"))
                    everyBox.setText("days event");
                else if (!everyNum.equals("1") && everyBox.getText().equals("week event"))
                    everyBox.setText("weeks event");
                else if (!everyNum.equals("1") && everyBox.getText().equals("month event"))
                    everyBox.setText("months event");
                else if (!everyNum.equals("1") && everyBox.getText().equals("year event"))
                    everyBox.setText("years event");
                dialog.dismiss();
            }
        });

        alertDw1 = alertBw1.create();
    }

    // This method opens dialog for remindNum button.
    private void setAlert2() {
        //TODO: Reduce similar codes as compared to setAlert1()
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

        alertBw2 = new AlertDialog.Builder(context);
        alertBw2.setTitle("Select number");

        alertBw2.setView(linearLayout).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                remindNum.setText(Integer.toString(numberPicker.getValue()));
                //TODO: When user choose 1, remove 's' doesn't work
                if (remindNum.equals("1") && remindBox.getText().equals("mins before event"))
                    remindBox.setText("min before event");
                else if (remindNum.equals("1") && remindBox.getText().equals("hours before event"))
                    remindBox.setText("hour before event");
                else if (remindNum.equals("1") && remindBox.getText().equals("days before event"))
                    remindBox.setText("day before event");

                else if (!remindNum.equals("1") && remindBox.getText().equals("min before event"))
                    remindBox.setText("mins before event");
                else if (!remindNum.equals("1") && remindBox.getText().equals("hour before event"))
                    remindBox.setText("hours before event");
                else if (!remindNum.equals("1") && remindBox.getText().equals("day before event"))
                    remindBox.setText("days before event");
                dialog.dismiss();
            }
        });

        alertDw2 = alertBw2.create();
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

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.addAppointmentButton:
                appointmentDatabase.open();
                insertInDatabase();
                //Close AppointmentController properly
                appointmentDatabase.close();
                appointmentDatabase = null;
                //Inform user that appointment has been created and return to previous activity
                Toast.makeText(this.getApplicationContext(), "Appointment set successfully.", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    protected void insertInDatabase()
    {
        final EditText eventInput = (EditText) findViewById(R.id.title);
        final EditText locationInput = (EditText) findViewById(R.id.appointmentLocation);
        final EditText notesInput = (EditText) findViewById(R.id.appointmentNotes);

        if(beginDate == null || beginTime == null || endDate == null || endTime == null)
        {
            beginDate = (Button) findViewById(R.id.startD);
            endDate = (Button) findViewById(R.id.endD);
            beginTime = (Button) findViewById(R.id.startT);
            endTime = (Button) findViewById(R.id.endT);
        }

        //Data parsing begins here
        final String event = eventInput.getText().toString();
        //Formatter for use later
        final SimpleDateFormat formatter;
        //Begin date and time. Format as e.g. 08 Jun 2015, Mon & 09:00 PM respectively
        final String beginD = beginDate.getText().toString();
        final String beginT = beginTime.getText().toString();
        final long beginEventMillisecond = stringToMillisecond(beginD, beginT);

        final String startProperDate = standardYearMonthDate(beginD, dateFormatter);

        //End date and time
        final String endD = endDate.getText().toString();
        final String endT = endTime.getText().toString();
        final long endEventMillisecond = stringToMillisecond(endD, endT);

        final String location = locationInput.getText().toString();
        final String notes = notesInput.getText().toString();

        //TODO: EVERY N DAY/WEEK/MONTH/YEAR
        //BULK INSERT INTO DATABASE

        //TODO: REMINDER - PARSE INTO DATE (NEXT: SET "DAEMON" FOR REMINDER)
        int remind = 5;

        //Insert into database
        appointmentDatabase.createAppointment(event, startProperDate, beginEventMillisecond, endEventMillisecond, location, notes, remind);
    }

    //Helper Method to convert text to millisecond
    //Helper method to change strings to the corresponding millisecond
    private long stringToMillisecond(String date, String time)
    {
        try
        {
            //Try-catch block placed here to prevent 'Unhandled ParseException' error
            Date tempTime = timeFormatter.parse(time);
            Date tempDate = dateFormatter.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.set(
                    tempDate.getYear(), tempDate.getMonth(), tempDate.getDay(),
                    tempTime.getHours(), tempTime.getMinutes(), tempTime.getSeconds()
            );

            return cal.getTimeInMillis();
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }

        //Should not reach here
        return -1;
    }

    //Helper method for converting date format to YYYY-MM-DD
    private String standardYearMonthDate(String date, SimpleDateFormat formatter)
    {
        SimpleDateFormat standardFormat = new SimpleDateFormat("yyyy MM dd");
        try
        {
            Date tempDate = formatter.parse(date);
            return standardFormat.format(tempDate);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        //Should not reach here
        return "";
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
}