package mooncakemonster.orbitalcalendar.importexternals;

import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.ical.compat.jodatime.DateTimeIterator;
import com.google.ical.compat.jodatime.DateTimeIteratorFactory;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableDateTime;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.AppointmentController;
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
    private static final String withSemiColon = "^([A-Z]+);(.+):([^:]+):?";
    private static final String withColon = "^([A-Z]+):([^:]+):?";

    private static Pattern stringWithSemiColon = Pattern.compile(withSemiColon);
    private static Pattern stringWithColon = Pattern.compile(withColon);

    private static final String withTriggerRegex = "[-+]?P([0-9]{0,2})W?([0-9]{0,2})D?T?([0-9]{0,2})H?([0-9]{0,2})M?([0-9]{0,2})S?";
    //Pattern to process duration field
    private static Pattern durationFormat = Pattern.compile(withTriggerRegex);

    //List to store all imported appointments
    private List<ImportedAppointment> listOfInput = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_import_external_list);
        Bundle extras = getIntent().getExtras();

        ImageButton import_external_events = (ImageButton) findViewById(R.id.add_imported);

        if (extras != null) {
            //Assuming user got here from ImportExternalFragment.java
            //String filePath = extras.getString("filePath");
            Uri uri = extras.getParcelable("fileChosen");
            //Get list of appointments
            listOfInput = icsReader(uri);
        }

        if(listOfInput == null) {
            import_external_events.setVisibility(View.INVISIBLE);
        }

        ArrayAdapter adapter = new ImportExternalAdapter(this, R.layout.row_import_external, listOfInput);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ImportedAppointment appt = (ImportedAppointment) l.getItemAtPosition(position);
        appt.toggle();
        ((BaseAdapter)l.getAdapter()).notifyDataSetChanged();
    }

    public List<ImportedAppointment> icsReader(Uri file) {

        //For storing actual appointments in
        List<ImportedAppointment> timetable = new ArrayList<ImportedAppointment>();
        ImportedAppointment tempAppt;

        try {
            InputStream inputStream = getContentResolver().openInputStream(file);
            //Open resources
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            //Variables for storing details pertaining to appointment
            String event = null;
            String location = null;
            long startMillisec = 0;
            long endMillisec = 0;
            String notes = null;
            boolean hasAlarm = false;
            boolean hasRepeat = false;
            //Difference from start time
            long alarmMillisec = 0;
            //Variable for RRULE
            String RRULE = "";
            String EXRULE = "";
            String RDATE = "";
            String EXDATE = "";

            //String for input
            String line;

            while ( (line = bufferedReader.readLine()) != null ) {
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
                    field = matchWithColon.group(1);
                    value = matchWithColon.group(2);
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
                        } else if (value.equals("VEVENT")) {
                            //Clear all traces of previous value
                            event = null;
                            location = null;
                            startMillisec = 0;
                            endMillisec = 0;
                            notes = null;
                            hasAlarm = false;
                            alarmMillisec = 0;
                            //Nuke for RRULE's value
                            RRULE = "";
                            EXRULE = "";
                            RDATE = "";
                            EXDATE = "";
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
                        hasRepeat = true;
                        RRULE = line + "\n";
                        break;
                    case "EXRULE":
                        hasRepeat = true;
                        EXRULE = line + "\n";
                        break;
                    case "RDATE":
                        hasRepeat = true;
                        RDATE = line + "\n";
                        break;
                    case "EXDATE":
                        hasRepeat = true;
                        EXDATE = line + "\n";
                        break;

                    //Ensure alarm was turned on
                    case "TRIGGER":
                        if (hasAlarm) {
                            //Set if only alarm is made to trigger before the beginning of event commencement
                            if (value.contains("-") || value.contains("+")) value = value.substring(1, value.length());
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
                            tempAppt.setColour(Constant.getRandomColour());
                            tempAppt.setEvent(event);
                            tempAppt.setStartDate(startMillisec);
                            tempAppt.setStartProperDate(dateFormatted);
                            tempAppt.setEndDate(endMillisec);
                            tempAppt.setLocation(location);
                            tempAppt.setNotes(notes);
                            tempAppt.setRemind(startMillisec - alarmMillisec);
                            tempAppt.setToImport();

                            timetable.add(tempAppt);

                            if (hasRepeat) {
                                //RRULE
                                String blockLine = RRULE + EXRULE + RDATE + EXDATE;
                                ReadableDateTime jodaTime = new DateTime(startMillisec);
                                DateTimeZone tzid = DateTimeZone.getDefault();

                                //Set limit on recurrence; prevent infinite recurrence.
                                //Tentatively limit to 100 years from today.
                                long limit = System.currentTimeMillis() + Constant.CENTURY_IN_MILLISECOND;

                                try {
                                    DateTimeIterator timeList = DateTimeIteratorFactory.createDateTimeIterator(blockLine, jodaTime, tzid, true);
                                    //Remove first appointment, which was already added in above
                                    timeList.next();

                                    while (timeList.hasNext()) {
                                        DateTime tempDate = timeList.next();
                                        long startMillisecond = tempDate.getMillis();

                                        if (startMillisecond > limit) {
                                            break;
                                        }

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

                                        timetable.add(tempAppt);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
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
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(timetable);
        return timetable;
    }

    //Helper method for processing duration (e.g. DURATION:PT1H0M0S)
    private static long processDuration(String duration) {
        Matcher match = durationFormat.matcher(duration);

        long answer = 0;

        if(match.matches()) {
            if(match.find(1)) {answer += Integer.parseInt(match.group(1)) * Constant.WEEK_IN_MILLISECOND;}
            if(match.find(2)) {answer += Integer.parseInt(match.group(2)) * Constant.DAY_IN_MILLISECOND;}
            if(match.find(3)) {answer += Integer.parseInt(match.group(3)) * Constant.HOUR_IN_MILLISECOND;}
            if(match.find(4)) {answer += Integer.parseInt(match.group(4)) * Constant.MIN_IN_MILLISECOND;}
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

        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v){
        switch(v.getId())
        {
            case R.id.add_imported:
                //Iterate through the list and add in database, any flagged as add_imported
                //Open database
                AppointmentController appointmentDatabase = new AppointmentController(this);
                appointmentDatabase.open();
                //Insert in database
                for(ImportedAppointment appt: listOfInput) {
                    if(appt.isToImport()) {
                        appointmentDatabase.createAppointment(appt);
                    }
                }
                //Close database
                finish();
                break;
        }
    }

}
