package mooncakemonster.orbitalcalendar.database;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/****************************************************************************************************
 * Abstract Constant class to store all helper method that will most likely be used across the app
 * as well as all the constants.
 *
 * N.B. Do not instantiate
 ****************************************************************************************************/
public abstract class Constant
{
    /****************************************************************************************************
     * (1) CONSTANTS
     ****************************************************************************************************/
    public static final long MIN_IN_MILLISECOND = 60000L;
    public static final long HOUR_IN_MILLISECOND = 3600000L;
    public static final long DAY_IN_MILLISECOND = 86400000L;
    public static final long WEEK_IN_MILLISECOND = 604800000L;

    public static final int EVENT_TITLE_MAX_LENGTH = 50;
    public static final int LOCATION_MAX_LENGTH = 50;
    public static final int NOTES_MAX_LENGTH = 200;

    public static SimpleDateFormat DATEFORMATTER = new SimpleDateFormat("dd/MM/yyyy, EEE");
    public static SimpleDateFormat TIMEFORMATTER = new SimpleDateFormat("hh:mm a");

    private static Calendar calendar = Calendar.getInstance();

    /****************************************************************************************************
     * (2) CONVERSION METHODS
     ****************************************************************************************************/

    /****************************************************************************************************
     * Change strings (date and time) to corresponding millisecond
     ****************************************************************************************************/
    public static long stringToMillisecond(String date, String time, SimpleDateFormat formatterForDate, SimpleDateFormat formatterForTime)
    {
        try
        {
            //Try-catch block placed here to prevent 'Unhandled ParseException' error
            Date tempTime = formatterForTime.parse(time);
            Date tempDate = formatterForDate.parse(date);

            long timeMillisecond = (tempTime.getHours() * HOUR_IN_MILLISECOND) + (tempTime.getMinutes() * MIN_IN_MILLISECOND);
            long dateMillisecond = tempDate.getTime();

            return (dateMillisecond + timeMillisecond);
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }

        //Should not reach here
        return -1;
    }

    /****************************************************************************************************
     * Convert millisecond back to strings
     ****************************************************************************************************/
    public static String getDate(long milliSeconds, String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        calendar.clear();
        calendar.setTimeInMillis(milliSeconds);

        return formatter.format(calendar.getTime());
    }

    public static String getDate(long milliSeconds, SimpleDateFormat formatter)
    {
        calendar.clear();
        calendar.setTimeInMillis(milliSeconds);

        return formatter.format(calendar.getTime());
    }

    /****************************************************************************************************
     * Convert any string date to standardised string date format: YYYY-MM-DD
     ****************************************************************************************************/
    public static String standardYearMonthDate(String date, SimpleDateFormat formatter, SimpleDateFormat standardFormat)
    {
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

    /****************************************************************************************************
     * Convert string date to date object
     ****************************************************************************************************/
    public static Date stringToDate(String date, SimpleDateFormat standardFormat)
    {
        try
        {
            return standardFormat.parse(date);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        //Should not reach here
        return new Date();
    }

    /****************************************************************************************************
     * (3) VALIDITY CHECK
     ****************************************************************************************************/

    /****************************************************************************************************
     * Ensure string extracted from an EditText widget is less than a certain length.
     * Otherwise, warn user through a custom error message, or a default error message.
     ****************************************************************************************************/
    public static boolean maxStringLength(String input, int length, EditText edittext, String errorMessage)
    {
        if(length <= 0){
            //Erronous input
            return false;
        }
        if(input.length() > length) {
            if (errorMessage == null && input.length() > length) {
                edittext.setError("" + length + " characters or less.");
                return false;
            } else
            {
                edittext.setError(errorMessage);
                return false;
            }
        }

        return true;
    }

    /****************************************************************************************************
     * Ensure string extracted from an EditText widget is more than a certain length.
     * Otherwise, warn user through a custom error message, or a default error message.
     ****************************************************************************************************/
    public static boolean minStringLength(String input, int length, EditText edittext, String errorMessage)
    {
        if(length <= 0){
            return false;
        }

        else if((input.length() == 0 || input == null)) {
            edittext.setError("Please do not leave this empty.");
            return false;
        }

        else if(input.length() < length){
            if(errorMessage == null) {
                edittext.setError("Please key in at least " + length + " characters.");
                return false;
            }
            else{
                edittext.setError(errorMessage);
                return false;
            }
        }

        return true;
    }

    /****************************************************************************************************
     * (4) WIDGET INTERACTIVITY
     ****************************************************************************************************/

    /****************************************************************************************************
     * Set Button as DatePicker
     ****************************************************************************************************/
    public static void setButtonDatePicker(final Context context, final Button button, final long timeInMillisecond, final String additionalString)
    {
        if(timeInMillisecond > 0) {
            calendar.setTimeInMillis(timeInMillisecond);
        }
        else
        {
            calendar.setTimeInMillis(System.currentTimeMillis());
        }

        final String dateFormat = DATEFORMATTER.format(calendar.getTime());
        button.setText(additionalString + dateFormat);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog date = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        button.setText(additionalString + DATEFORMATTER.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                date.show();
            }
        });
    }

    /****************************************************************************************************
     * Set Button as TimePicker
     ****************************************************************************************************/
    public static void setButtonTimePicker(final Context context, final Button button, final long timeInMillisecond, final String additionalString)
    {
        if(timeInMillisecond > 0) {
            calendar.setTimeInMillis(timeInMillisecond);
        }
        else
        {
            calendar.setTimeInMillis(System.currentTimeMillis());
        }

        final String timeFormat = TIMEFORMATTER.format(calendar.getTime());
        button.setText(additionalString + timeFormat);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog time = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        button.setText(additionalString + TIMEFORMATTER.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                time.show();
            }
        });
    }

}
