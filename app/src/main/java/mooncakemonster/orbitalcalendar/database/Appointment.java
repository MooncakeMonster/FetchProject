package mooncakemonster.orbitalcalendar.database;

/*
 * This class is our model and contains the data we will save in the database and show in the user interface.
 * Adapted from: http://www.vogella.com/tutorials/AndroidSQLite/article.html#sqliteoverview_sqliteopenhelper
 */

import android.support.annotation.NonNull;

public class Appointment implements Comparable<Appointment> {

    private long id;
    private String event;
    private String startProperDate;
    private long startDate;
    private long endDate;
    private String location;
    private String notes;
    private int remind;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getStartProperDate(){
        return startProperDate;
    }

    public void setStartProperDate(String startProperDate) {
        this.startProperDate = startProperDate;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate(){
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getRemind() {
        return remind;
    }

    public void setRemind(int remind) {
        this.remind = remind;
    }

    // Used by ArrayAdapter in the ListView
    @Override
    public String toString() {
        return event;
    }

    public int compareTo(@NonNull Appointment that) {
        if (this.getStartDate() < that.getStartDate()) return -1;
        if (this.getStartDate() > that.getStartDate()) return 1;

        else
            return this.getEvent().compareTo(that.getEvent());
    }
}
