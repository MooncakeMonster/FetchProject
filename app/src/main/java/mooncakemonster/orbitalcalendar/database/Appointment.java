package mooncakemonster.orbitalcalendar.database;

/*
 * This class is our model and contains the data we will save in the database and show in the user interface.
 * Adapted from: http://www.vogella.com/tutorials/AndroidSQLite/article.html#sqliteoverview_sqliteopenhelper
 */

import android.support.annotation.NonNull;

import java.io.Serializable;

/*
 * Appointment's member variable will not be accessed directly.
 * Instead, getter and setter methods (e.g. getID, setID) will be used to modify values of
 * an instance of Appointment class.
 *
 * Serializable implemented to allow passing of Appointment between Activities
 * May implement Parceble in place of Serializable in optimization phrase
 */

public class Appointment implements Comparable<Appointment>, Serializable {

    private long id;
    private long sub_id;
    private long actualStartDate;
    private String event;
    private String startProperDate;
    private long startDate;
    private long endDate;
    private String location;
    private String notes;
    private long remind;
    private int colour;

    public Appointment() { }

    public Appointment(long id, long sub_id, long actualStartDate, String event, String startProperDate, long startDate, long endDate,
                       String location, String notes, long remind, int colour) {
        this.id = id;
        this.sub_id = sub_id;
        this.actualStartDate = actualStartDate;
        this.event = event;
        this.startProperDate = startProperDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.notes = notes;
        this.remind = remind;
        this.colour = colour;
    }

    //ID of the entry in SQLite table
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    //SUB_ID of the few days event
    public long getSub_id() {
        return sub_id;
    }
    public void setSub_id(long sub_id) {
        this.sub_id = sub_id;
    }

    // Save actual start date for few days event
    public long getActualStartDate() { return actualStartDate; }
    public void setActualStartDate(long actualStartDate) { this.actualStartDate = actualStartDate; }

    //Event
    public String getEvent() {
        return event;
    }
    public void setEvent(String event) {
        this.event = event;
    }

    //Proper starting date in the string format YYYY-MM-DD
    public String getStartProperDate(){
        return startProperDate;
    }
    public void setStartProperDate(String startProperDate) {
        this.startProperDate = startProperDate;
    }

    //Starting time in milliseconds
    public long getStartDate() {
        return startDate;
}
    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    //Ending time in milliseconds
    public long getEndDate(){
        return endDate;
    }
    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    //Location where the event shall be held
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    //Miscellaneous notes for the appointment
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    //Reminder; how many minutes prior to reminding user of an upcoming event
    public long getRemind() {
        return remind;
    }
    public void setRemind(long remind) {
        this.remind = remind;
    }

    //Get ID of colour bear choosen by user
    public int getColour() {
        return colour;
    }
    public void setColour(int colour) {
        this.colour = colour;
    }

    // Used by ArrayAdapter in the ListView
    @Override
    public String toString() {
        return event;
    }

    // Allow for ease of sorting, when a List of Appointment is produced.
    // Sorted according to the starting date. If two appointments carry the same starting date
    // and time, then sort the events in alphabetical order.
    public int compareTo(@NonNull Appointment that) {
        if (this.getStartDate() < that.getStartDate()) return -1;
        if (this.getStartDate() > that.getStartDate()) return 1;

        else
            return this.getEvent().compareTo(that.getEvent());
    }
}
