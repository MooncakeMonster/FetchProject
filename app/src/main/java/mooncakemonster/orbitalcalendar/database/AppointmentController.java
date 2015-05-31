package mooncakemonster.orbitalcalendar.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.Collections;

public class AppointmentController
{
    // Database fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = { DatabaseHelper.COLUMN_ID,
                                    DatabaseHelper.EVENT,
                                    DatabaseHelper.DATE,
                                    DatabaseHelper.LOCATION,
                                    DatabaseHelper.NOTES,
                                    DatabaseHelper.REMIND,
                                  };

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Appointment createAppointment(String event, int date, String location, String notes, String remind) {
        ContentValues values = new ContentValues();
        //Insert key-value in ContentValues
        values.put(DatabaseHelper.EVENT, event);
        values.put(DatabaseHelper.DATE, date);
        values.put(DatabaseHelper.LOCATION, location);
        values.put(DatabaseHelper.NOTES, notes);
        values.put(DatabaseHelper.REMIND, remind);

        long insertId = database.insert(DatabaseHelper.DATABASE_NAME, null, values);
        Cursor cursor = database.query(DatabaseHelper.DATABASE_NAME, allColumns, DatabaseHelper.COLUMN_ID + " = " + insertId, null, null, null, null);

        //Move cursor to the first row of the result, not the table
        cursor.moveToFirst();
        Appointment newComment = cursorToAppointment(cursor);
        cursor.close();
        return newComment;
    }

    public void deleteComment(Appointment comment) {
        long id = comment.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(DatabaseHelper.DATABASE_NAME, DatabaseHelper.COLUMN_ID + " = " + id, null);
    }

    public List<Appointment> getAllAppointment() {
        List<Appointment> appointments = new ArrayList<Appointment>();

        Cursor cursor = database.query(DatabaseHelper.DATABASE_NAME,allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Appointment appointment = cursorToAppointment(cursor);
            appointments.add(appointment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        Collections.sort(appointments);
        return appointments;
    }

    private Appointment cursorToAppointment(Cursor cursor) {
        Appointment appt = new Appointment();
        //Get values from cursor
        appt.setId(cursor.getLong(0));
        appt.setEvent(cursor.getString(1));
        appt.setDate(cursor.getLong(2));
        appt.setLocation(cursor.getString(3));
        appt.setNotes(cursor.getString(4));
        appt.setRemind(cursor.getInt(5));

        return appt;
    }
}
