package mooncakemonster.orbitalcalendar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppointmentController
{
    // Database fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = { DatabaseHelper.COLUMN_ID,
                                    DatabaseHelper.EVENT,
                                    DatabaseHelper.STARTPROPERDATE,
                                    DatabaseHelper.STARTDATE,
                                    DatabaseHelper.ENDDATE,
                                    DatabaseHelper.LOCATION,
                                    DatabaseHelper.NOTES,
                                    DatabaseHelper.REMIND,
                                  };


    public AppointmentController(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Appointment createAppointment(String event, String startproperdate, long startdate, long enddate, String location, String notes, long remind) {
        ContentValues values = new ContentValues();
        //Insert key-value in ContentValues
        values.put(DatabaseHelper.EVENT, event);
        values.put(DatabaseHelper.STARTPROPERDATE, startproperdate);
        values.put(DatabaseHelper.STARTDATE, startdate);
        values.put(DatabaseHelper.ENDDATE, enddate);
        values.put(DatabaseHelper.LOCATION, location);
        values.put(DatabaseHelper.NOTES, notes);
        values.put(DatabaseHelper.REMIND, remind);

        long insertId = database.insert(DatabaseHelper.DATABASE_NAME, null, values);
        Cursor cursor = database.query(DatabaseHelper.DATABASE_NAME, allColumns, DatabaseHelper.COLUMN_ID + " = " + insertId, null, null, null, null);

        //Move cursor to the first row of the result, not the table
        cursor.moveToFirst();
        Appointment newAppt = cursorToAppointment(cursor);
        cursor.close();
        return newAppt;
    }

    public void deleteAppointment(Appointment appointment) {
        long id = appointment.getId();
        //System.out.println("Comment deleted with id: " + id);
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
        // Note! Close cursor after use
        cursor.close();
        //Sort list of appointments
        Collections.sort(appointments);
        return appointments;
    }

    private Appointment cursorToAppointment(Cursor cursor) {
        Appointment appt = new Appointment();
        //Get values from cursor to create appointment
        appt.setId(cursor.getLong(0));
        appt.setEvent(cursor.getString(1));
        appt.setStartProperDate(cursor.getString(2));
        appt.setStartDate(cursor.getLong(3));
        appt.setEndDate(cursor.getLong(4));
        appt.setLocation(cursor.getString(5));
        appt.setNotes(cursor.getString(6));
        appt.setRemind(cursor.getInt(7));

        return appt;
    }
}
