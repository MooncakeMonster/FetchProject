package mooncakemonster.orbitalcalendar.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Abstract Constant class to store all helper method that will most likely be used across the app
 * as well as all the constants.
 *
 * N.B. Do not instatiate
 */
public abstract class Constant
{
    //Constants
    public static final long MIN_IN_MILLISECOND = 60000L;
    public static final long HOUR_IN_MILLISECOND = 3600000L;
    public static final long DAY_IN_MILLISECOND = 86400000L;
    public static final long WEEK_IN_MILLISECOND = 604800000;

    /*
     * Helper method to change strings to the corresponding millisecond
     */
    public static long stringToMillisecond(String date, String time, SimpleDateFormat formatterForDate, SimpleDateFormat formatterForTime)
    {
        try
        {
            //Try-catch block placed here to prevent 'Unhandled ParseException' error
            Date tempTime = formatterForDate.parse(time);
            Date tempDate = formatterForTime.parse(date);
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

    /*
     * Helper method for converting date format to YYYY-MM-DD
     */
    public static String standardYearMonthDate(String date, SimpleDateFormat formatter)
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
}
