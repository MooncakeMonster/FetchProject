package mooncakemonster.orbitalcalendar.importexternals;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.ical.compat.jodatime.DateTimeIterator;
import com.google.ical.compat.jodatime.DateTimeIteratorFactory;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableDateTime;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Constant;

/**
 * Read all .ics extension
 * Based on RFC 2445 iCalendar specification
 * Note: This parser recognizes only "vevent" components.
 */
public class ImportICSParser extends ListActivity {

    //Date format
    private static final SimpleDateFormat ICS_PARSER_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat ICS_PARSER_DATETIME_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss");

    //Pattern to determine if string follows the format:
    // (a) FIELD;miscellaneousstring:VALUE
    // (b) FIELD:VALUE
    private static final String withSemiColon = "^([-A-Z]+);(.*):([^:]*):?";
    private static final String withColon = "^([-A-Z]+ ):([^:]*):?";

    private static Pattern stringWithSemiColon = Pattern.compile(withSemiColon);
    private static Pattern stringWithColon = Pattern.compile(withColon);

    //Pattern to process duration field
    private static Pattern durationFormat = Pattern.compile("[-+]?P([0-9]{0,2})W?([0-9]{0,2})D?T?([0-9]{0,2})H?([0-9]{0,2})M?([0-9]{0,2})S?");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_import_external_list);
        Bundle extras = getIntent().getExtras();
        List<ImportedAppointment> listOfInput = null;

        Button import_external_events = (Button) findViewById(R.id.add_imported);

        if (extras != null) {
            //Assuming user got here from ImportExternalFragment.java
            String filePath = extras.getString("filePath");
            //Get list of appointments
            listOfInput = icsReader(filePath);
        }

        ArrayAdapter adapter = new ImportExternalAdapter(this, R.layout.row_import_external, listOfInput);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ImportedAppointment appt = (ImportedAppointment) l.getItemAtPosition(position);
        appt.toggle();
    }

    public List<ImportedAppointment> icsReader(String file) {

        //For storing actual appointments in
        List<ImportedAppointment> timetable = new ArrayList<ImportedAppointment>();
        ImportedAppointment tempAppt;

        try {
            //Open resources
            FileReader dataFile = new FileReader(file);
            BufferedReader bufferedDataFile = new BufferedReader(dataFile);

            //Read and obtain the number of lines in file
            int numLine = countLines(file);

            //Variables for storing details pertaining to appointment
            String event = null;
            String location = null;
            long startMillisec = 0;
            long endMillisec = 0;
            String notes = null;
            boolean hasAlarm = false;
            //Difference from start time
            long alarmMillisec = 0;
            //Variable for RRULE
            String RRULE = "";
            String EXRULE = "";
            String RDATE = "";
            String EXDATE = "";

            for (int counter = 0; counter < numLine; counter++) {

                String line = bufferedDataFile.readLine();
                //Processing of line
                String field = null;
                String intermediate = null;
                String value = null;

                Matcher matchWithSemiColon = stringWithSemiColon.matcher(line);
                Matcher matchWithColon = stringWithColon.matcher(line);

                if (matchWithSemiColon.matches()) {
                    field = matchWithSemiColon.group(1);
                    intermediate = matchWithSemiColon.group(2);
                    value = matchWithSemiColon.group(3);
                }
                //Otherwise, split string as per normal
                else if (matchWithColon.matches()) {
                    field = matchWithSemiColon.group(1);
                    value = matchWithSemiColon.group(2);
                    //Clear value of intermediate
                    intermediate = null;
                } else {
                    Log.i("UNMATCHED", line);
                    continue;
                }

                //Decide on what to do, based on the field
                switch (field) {
                    //Start of a new appointment
                    case "BEGIN":
                        if (value.equals("VALARM")) {
                            hasAlarm = true;
                        }
                        break;

                    //Get Event Name
                    case "SUMMARY":
                        event = value;
                        break;

                    //Get Notes
                    case "DESCRIPTION":
                        notes = value;
                        break;

                    //Get location
                    case "LOCATION":
                        location = value;
                        break;

                    //Get starting time
                    case "DTSTART":
                        if (intermediate != null && intermediate.contains("VALUE=DATE")) {
                            //Use a SimpleDateFormatter on value
                            startMillisec = Constant.stringToMillisecond(value, ICS_PARSER_DATE_FORMAT);
                        } else {
                            //If(intermediate.contains("VALUE=DATE-TIME")) or by default,
                            value = value.replaceFirst("^([0-9]{8}T[0-9]{6})[.]*", "$1");
                            startMillisec = Constant.stringToMillisecond(value, ICS_PARSER_DATETIME_FORMAT);
                        }
                        break;

                    //Get ending time, if any
                    case "DTEND":
                        if (intermediate != null && intermediate.contains("VALUE=DATE")) {
                            //Use a SimpleDateFormatter on value
                            endMillisec = Constant.stringToMillisecond(value, ICS_PARSER_DATE_FORMAT);
                        } else {
                            //If(intermediate.contains("VALUE=DATE-TIME")) or by default
                            value = value.replaceFirst("^([0-9]{8}T[0-9]{6})[.]*", "$1");
                            endMillisec = Constant.stringToMillisecond(value, ICS_PARSER_DATETIME_FORMAT);
                        }
                        break;

                    case "DURATION":
                        //Assuming VALARM field has not been encountered (yet)
                        if(!hasAlarm) {
                            if (value.contains("+")) break;
                            else if (value.contains("-")) value = value.substring(1, value.length());
                            endMillisec = startMillisec + processDuration(value);
                            break;
                        }
                        break;

                    //Is there a repeat appointment in the future?
                    case "RRULE":
                        RRULE = line + "\n";
                        break;
                    case "EXRULE":
                        EXRULE = line + "\n";
                        break;
                    case "RDATE":
                        RDATE = line + "\n";
                        break;
                    case "EXDATE":
                        EXDATE = line + "\n";
                        break;

                    //Ensure alarm was turned on
                    case "TRIGGER":
                        if (hasAlarm) {
                            //Set if only alarm is made to trigger before the beginning of event commencement
                            if (value.contains("+")) break;
                            else if (value.contains("-")) value = value.substring(1, value.length());
                            alarmMillisec = processDuration(value);
                        }

                    case "END":
                        if (value.equals("VEVENT")) {
                            //Insert into database, with what is available
                            endMillisec = (endMillisec == 0) ? startMillisec : endMillisec;

                            //Get startProperDate
                            Date date = new Date(startMillisec);
                            String dateFormatted = Constant.YYYYMMDD_FORMATTER.format(date);

                            //Set value for initial appointment
                            tempAppt = new ImportedAppointment();
                            tempAppt.setEvent(event);
                            tempAppt.setStartDate(startMillisec);
                            tempAppt.setStartProperDate(dateFormatted);
                            tempAppt.setEndDate(endMillisec);
                            tempAppt.setLocation(location);
                            tempAppt.setNotes(notes);
                            tempAppt.setRemind(startMillisec - alarmMillisec);
                            tempAppt.setToImport();

                            timetable.add(tempAppt);

                            //RRULE
                            String blockLine = RRULE + EXRULE + RDATE + EXDATE;
                            ReadableDateTime jodaTime = new DateTime(startMillisec);
                            DateTimeZone tzid = DateTimeZone.getDefault();

                            try {
                                DateTimeIterator timeList = DateTimeIteratorFactory.createDateTimeIterator(blockLine, jodaTime, tzid, true);

                                while(timeList.hasNext()) {
                                    DateTime tempDate = timeList.next();
                                    long startMillisecond = tempDate.getMillis();
                                    //Get startProperDate
                                    date = new Date(startMillisecond);
                                    dateFormatted = Constant.YYYYMMDD_FORMATTER.format(date);

                                    tempAppt = new ImportedAppointment();
                                    tempAppt.setEvent(event);
                                    tempAppt.setStartDate(startMillisecond);
                                    tempAppt.setStartProperDate(dateFormatted);
                                    tempAppt.setEndDate(endMillisec);
                                    tempAppt.setLocation(location);
                                    tempAppt.setNotes(notes);
                                    tempAppt.setRemind(startMillisecond - alarmMillisec);
                                    tempAppt.setToImport();
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

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
            bufferedDataFile.close();
            dataFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(timetable);
        return timetable;
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
        Matcher match = durationFormat.matcher(duration);

        long answer = 0;

        if(match.find())
        {
            if(match.group(0) != null) {answer += Integer.parseInt(match.group(0)) * Constant.WEEK_IN_MILLISECOND;}
            if(match.group(1) != null) {answer += Integer.parseInt(match.group(1)) * Constant.DAY_IN_MILLISECOND;}
            if(match.group(2) != null) {answer += Integer.parseInt(match.group(2)) * Constant.HOUR_IN_MILLISECOND;}
            if(match.group(3) != null) {answer += Integer.parseInt(match.group(3)) * Constant.MIN_IN_MILLISECOND;}
            //Second omitted, as such precision is not required
        }

        return answer;
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
            //Open database
            //Get array from adapter
            List<ImportedAppointment> timetable = null;
            //insert in database
            for(ImportedAppointment appt: timetable) {
                if(appt.isToImport()) {

                }
            }
            //Close database
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
