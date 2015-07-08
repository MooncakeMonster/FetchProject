package mooncakemonster.orbitalcalendar.ImportExternal;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.AppointmentController;
import mooncakemonster.orbitalcalendar.database.Constant;

/**
 * Read all .ics extension
 * Based on RFC 2445 iCalendar specification
 * <p/>
 * Note: This parser recognizes only "vevent" components.
 */
public class ImportICSParser extends Activity {

    //Date format
    private static final SimpleDateFormat ICS_PARSER_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat ICS_PARSER_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void icsReader(String file) {
        String event = null;
        String location = null;
        long startMillisec = 0;
        long endMillisec = 0;
        String notes = null;
        boolean hasAlarm = false;
        long alarmMillisec = 0;

        //For storing appointments in
        List<Appointment> timetable = new ArrayList<Appointment>();
        Appointment tempAppt;

        //AppointmentController variable to control the SQLite database
        AppointmentController appointmentDatabase = new AppointmentController(this.getApplicationContext());

        try {
            //Open resources
            FileReader dataFile = new FileReader(file);
            BufferedReader bufferedDataFile = new BufferedReader(dataFile);

            //Read and obtain the number of lines in file
            int numLine = countLines(file);

            //Pattern to determine if string follows the format:
            // (a) FIELD;miscellaneousstring:VALUE
            // (b) FIELD:VALUE
            final String withSemiColon = "^([A-Z]*);(.*):([^:]*)";
            final String withColon = "^([A-Z]*):([^:]*)";

            Pattern stringWithSemiColon = Pattern.compile(withSemiColon);
            Pattern stringWithColon = Pattern.compile(withColon);

            for (int counter = 0; counter < numLine; counter++) {
                String line = bufferedDataFile.readLine();

                String field = null;
                String intermediate = null;
                String value = null;

                Matcher matchWithSemiColon = stringWithSemiColon.matcher(line);
                Matcher matchWithColon = stringWithColon.matcher(line);

                //Formatting for DTSTART may be different, hence to accommodate for the difference
                if (matchWithSemiColon.matches()) {
                    String[] split = stringWithSemiColon.split(line, 3);

                    field = split[0];
                    intermediate = split[1];
                    value = split[2];
                }
                //Otherwise, split string as per normal
                else if (matchWithColon.matches()) {
                    String[] split = stringWithColon.split(line, 2);

                    field = split[0];
                    value = split[1];
                    //Clear value of intermediate
                    intermediate = null;
                } else {
                    Log.i("UNMATCHED", line);
                }

                //Decide on what to do, based on the field
                switch (field) {
                    //Start of a new appointment
                    case "BEGIN":
                        if (value.equals("VEVENT")) {
                            tempAppt = new Appointment();
                        } else if (value.equals("VALARM")) {
                            hasAlarm = true;
                        }
                        break;

                    //Get Event Name
                    case "DESCRIPTION":
                    case "SUMMARY":
                        event += value + " ";
                        break;

                    //Get location
                    case "LOCATION":
                        location = value;

                        //Get starting time
                        // TODO: Find different types of formatting available
                    case "DTSTART":
                        if (intermediate.contains("DATE")) {
                            //Use a SimpleDateFormatter on value
                            startMillisec = Constant.stringToMillisecond(value, ICS_PARSER_DATE_FORMAT);
                        } else {
                            //Use a different SimpleDateFormatter on value
                            startMillisec = Constant.stringToMillisecond(value, ICS_PARSER_DATETIME_FORMAT);
                        }
                        break;

                    //Get ending time if any
                    case "DTEND":
                        if (intermediate.contains("DATE")) {
                            //Use a SimpleDateFormatter on value
                            endMillisec = Constant.stringToMillisecond(value, ICS_PARSER_DATE_FORMAT);
                        } else {
                            //Use a different SimpleDateFormatter on value
                            endMillisec = Constant.stringToMillisecond(value, ICS_PARSER_DATETIME_FORMAT);
                        }
                        break;

                    case "DURATION":


                        //Is there a repeat appointment in the future?
                    case "RRULE":
                        //TODO: Make for repeat
                        break;

                    //Ensure alarm was turned on
                    case "TRIGGER":
                        if (hasAlarm) {
                            //Set if only alarm is made to trigger before the beginning of event commencement
                        }


                    case "END":
                        if (value.equals("VEVENT")) {
                            //Insert into database, with what is available
                            endMillisec = (endMillisec == 0) ? startMillisec : endMillisec;

                            Date date = new Date(startMillisec);
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy MM dd");
                            String dateFormatted = formatter.format(date);

                            appointmentDatabase.createAppointment(event, dateFormatted, startMillisec, endMillisec, location, notes, alarmMillisec, 0);

                            //Nuke tempAppt
                            tempAppt = null;
                            //Nuke all values
                            event = null;
                            location = null;
                            startMillisec = 0;
                            endMillisec = 0;
                            notes = null;
                        } else if (value.equals("VALARM")) {
                            //Nuke hasAlarm value
                            hasAlarm = false;
                        }
                        break;

                    default:
                        break;

                }
            }

            //Close resources
            appointmentDatabase.close();
            bufferedDataFile.close();
            dataFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Helper method for counting lines in file
    public int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }

    //Helper method for processing duration (e.g. DURATION:PT1H0M0S)
    private static long processDuration(String duration) {
        //TODO
        return 0;
    }

    //Need a builder to take in inputs and create appointments

    //Once appointments are created, let user verify themselves personally
}