package mooncakemonster.orbitalcalendar.ImportExternal;

import android.app.Activity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import mooncakemonster.orbitalcalendar.database.Appointment;

/**
 * Read all .ics extension
 *
 * Note: This parser recognizes only "vevent" components.
 *
 */
public class ImportICSParser extends Activity{

    //Date format
    private static final SimpleDateFormat ICS_PARSER_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat ICS_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public static void icsReader(String file)
    {
        int counter;

        String event = null;
        String location = null;
        long startMillisec = 0;
        long endMillisec = 0;
        String notes = null;

        //For storing appointments in
        List<Appointment> timetable = new ArrayList<Appointment>();
        Appointment tempAppt;

        //AppointmentController variable to control the SQLite database
        //AppointmentController appointmentDatabase = new AppointmentController(this.getApplicationContext());

        try
        {
            //Open resources
            FileReader dataFile = new FileReader(file);
            BufferedReader bufferedDataFile = new BufferedReader(dataFile);

            //Read and obtain the number of lines in file
            String line = bufferedDataFile.readLine();
            final int numLine = Integer.parseInt(line);

            for(counter = 0; counter < numLine ; counter++)
            {
                line = bufferedDataFile.readLine();

                String field;
                String intermediate;
                String value;

                //Formatting for DTSTART may be different, hence to accommodate for the difference
                if(line.subSequence(0,8).equals("DTSTART;") || line.subSequence(0,8).equals("DTEND;"))
                {
                    String[] split = line.split(("(;)|(:)"), 3);

                    field = split[0];
                    intermediate = split[1];
                    value = split[2];
                }
                //Otherwise, split string as per normal
                else
                {
                    String[] split = line.split(Pattern.quote(":"), 2);

                    field = split[0];
                    //Clear value of intermediate
                    intermediate = null;
                    value = split[1];
                }

                //Decide on what to do, based on the field
                switch(field)
                {
                    //Start of a new appointment
                    case "BEGIN":
                        if(value.equals("VEVENT"))
                        {
                            tempAppt = new Appointment();
                        }
                        break;

                    //Get Event Name
                    case "DESCRIPTION":case "SUMMARY":
                        event = value;
                    break;

                    //Get location
                    case "LOCATION":
                        location = value;

                    //Get starting time
                    // TODO: Find different types of formatting available
                    case "DTSTART":
                        if(intermediate.equals(""))
                        {
                            //Use a SimpleDateFormatter on value

                            //modify to startMillisec
                        }

                        else if(intermediate.equals(""))
                        {
                            //Use a different SimpleDateFormatter on value

                            //modify to startMillisec
                        }
                        break;

                    //Get ending time if any
                    case "DTEND":
                        //TODO:
                        break;

                    //Is there a repeat appointment in the future?
                    case "RRULE":
                        //TODO: Make for repeat
                        processRule(value);
                        break;

                    //TODO: Is there an reminder set for the appointment?

                    case "END":
                        if(value.equals("VEVENT"))
                        {
                            //Insert into database, with what is available

                            //Nuke tempAppt
                            tempAppt = null;
                            //Nuke all values
                            event = null;
                            location = null;
                            startMillisec = 0;
                            endMillisec = 0;
                            notes = null;
                        }
                        break;

                    default:
                        break;

                }
            }

            //Close resources
            //appointmentDatabase.close();
            bufferedDataFile.close();
            dataFile.close();

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    //Helper method for processing rules in ics
    private static void processRule(String rule)
    {

    }


    //Need a builder to take in inputs and create appointments

    //Once appointments are created, let user verify themselves personally
}
