package mooncakemonster.orbitalcalendar.database;

/*
 * This class is our model and contains the data we will save in the database and show in the user interface.
 * Adapted from: http://www.vogella.com/tutorials/AndroidSQLite/article.html#sqliteoverview_sqliteopenhelper
 */

import android.support.annotation.NonNull;

/*
 * Appointment's member variable will not be accessed directly.
 * Instead, getter and setter methods (e.g. getID, setID) will be used to modify values of
 * an instance of Appointment class.
 */

public class Appointment implements Comparable<Appointment> {

    private long id;
    private String event;
    private String startProperDate;
    private long startDate;
    private long endDate;
    private String location;
    private String notes;
    private int remind;
    private int colour;

    //TODO: Remove if redundant
    private String date;
    private String startTime, endTime;

    //ID of the entry in SQLite table
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

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
    public int getRemind() {
        return remind;
    }
    public void setRemind(int remind) {
        this.remind = remind;
    }

    //Get ID of colour bear choosen by user
    public int getColour() {
        return colour;
    }
    public void setColour(int colour) {
        this.colour = colour;
    }

    //TODO: Remove if redundant
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
