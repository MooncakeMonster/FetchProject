package mooncakemonster.orbitalcalendar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppointmentController
{
    // Database fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    private String[] allColumns = { DatabaseHelper.COLUMN_ID,
                                    DatabaseHelper.SUB_ID,
                                    DatabaseHelper.ACTUAL_START_DATE,
                                    DatabaseHelper.EVENT,
                                    DatabaseHelper.STARTPROPERDATE,
                                    DatabaseHelper.STARTDATE,
                                    DatabaseHelper.ENDDATE,
                                    DatabaseHelper.LOCATION,
                                    DatabaseHelper.NOTES,
                                    DatabaseHelper.REMIND,
                                    DatabaseHelper.COLOUR,
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


    public Appointment createAppointment(long sub_id, long actualStartDate, String event, String startproperdate, long startdate, long enddate, String location, String notes, long remind, int colour) {
        ContentValues values = new ContentValues();
        //Insert key-value in ContentValues
        values.put(DatabaseHelper.SUB_ID, sub_id);
        values.put(DatabaseHelper.ACTUAL_START_DATE, actualStartDate);
        values.put(DatabaseHelper.EVENT, event);
        values.put(DatabaseHelper.STARTPROPERDATE, startproperdate);
        values.put(DatabaseHelper.STARTDATE, startdate);
        values.put(DatabaseHelper.ENDDATE, enddate);
        values.put(DatabaseHelper.LOCATION, location);
        values.put(DatabaseHelper.NOTES, notes);
        values.put(DatabaseHelper.REMIND, remind);
        values.put(DatabaseHelper.COLOUR, colour);

        long insertId = database.insert(DatabaseHelper.DATABASE_NAME, null, values);
        Cursor cursor = database.query(DatabaseHelper.DATABASE_NAME, allColumns, DatabaseHelper.COLUMN_ID + " = " + insertId, null, null, null, null);

        //Move cursor to the first row of the result, not the table
        cursor.moveToFirst();
        Appointment newAppt = cursorToAppointment(cursor);
        cursor.close();
        return newAppt;
    }

    public Appointment createAppointment(Appointment appointment)
    {
        ContentValues values = new ContentValues();
        //Insert key-value in ContentValues
        values.put(DatabaseHelper.SUB_ID, appointment.getSub_id());
        values.put(DatabaseHelper.ACTUAL_START_DATE, appointment.getActualStartDate());
        values.put(DatabaseHelper.EVENT, appointment.getEvent());
        values.put(DatabaseHelper.STARTPROPERDATE, appointment.getStartProperDate());
        values.put(DatabaseHelper.STARTDATE, appointment.getStartDate());
        values.put(DatabaseHelper.ENDDATE, appointment.getEndDate());
        values.put(DatabaseHelper.LOCATION, appointment.getLocation());
        values.put(DatabaseHelper.NOTES, appointment.getNotes());
        values.put(DatabaseHelper.REMIND, appointment.getRemind());
        values.put(DatabaseHelper.COLOUR, appointment.getColour());

        long insertId = database.insert(DatabaseHelper.DATABASE_NAME, null, values);
        Cursor cursor = database.query(DatabaseHelper.DATABASE_NAME, allColumns, DatabaseHelper.COLUMN_ID + " = " + insertId, null, null, null, null);

        //Move cursor to the first row of the result, not the table
        cursor.moveToFirst();
        cursor.close();
        return appointment;
    }


    public void deleteTargetAppointment(Appointment appointment) {
        long id = appointment.getId();
        //System.out.println("Comment deleted with id: " + id);
        database.delete(DatabaseHelper.DATABASE_NAME, DatabaseHelper.COLUMN_ID + " = " + id, null);
    }

    // Delete data from database
    public void deleteAppointment(Appointment appointment) {
        long id = appointment.getId();
        long sub_id = appointment.getSub_id();
        long start = sub_id, end = sub_id, last_day = (appointment.getEndDate() - appointment.getActualStartDate()) / Constant.DAY_IN_MILLISECOND;

        // For few days event
        if(sub_id != -1) {
            while (start >= 0 && getCount() != 0) {
                String whereClause = DatabaseHelper.COLUMN_ID + " = ? AND " + DatabaseHelper.SUB_ID + " = ? ";
                String[] whereArgs = new String[]{String.valueOf(id--), String.valueOf(start--)};
                database.delete(DatabaseHelper.DATABASE_NAME, whereClause, whereArgs);
            }

            id = appointment.getId();
            while (end < last_day && getCount() != 0) {
                String whereClause = DatabaseHelper.COLUMN_ID + " = ? AND " + DatabaseHelper.SUB_ID + " = ? ";
                String[] whereArgs = new String[]{String.valueOf(id++), String.valueOf(end++)};
                database.delete(DatabaseHelper.DATABASE_NAME, whereClause, whereArgs);
            }

        } else {
            database.delete(DatabaseHelper.DATABASE_NAME, DatabaseHelper.COLUMN_ID + " = " + id, null);
        }
    }

    // This method retrieves all appointments.
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
        Collections.sort(appointments);
        return appointments;
    }


    // This method allows retrieval of current selected date' event list.
    public List<Appointment> getSelectedDateAppointment(String date) {
        List<Appointment> appointments = new ArrayList<Appointment>();

        Cursor cursor = database.query(DatabaseHelper.DATABASE_NAME, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Appointment appointment = cursorToAppointment(cursor);
            if(appointment.getStartProperDate().equals(date)) appointments.add(appointment);
            cursor.moveToNext();
        }
        // Note! Close cursor after use
        cursor.close();
        //Sort list of appointments in descending order to show latest date first
        Collections.sort(appointments);
        return appointments;
    }

    // This method retrieves the target id and sub_id of event
    public Appointment getTargetEvent(long id, long sub_id) {
        Appointment appointment = null;
        Cursor cursor = database.query(DatabaseHelper.DATABASE_NAME, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if(cursor.getLong(0) == id && cursor.getLong(1) == sub_id) {
                appointment = cursorToAppointment(cursor);
                break;
            }
            cursor.moveToNext();
        }
        // Note! Close cursor after use
        cursor.close();

        return appointment;
    }

    // This method retrieves the target date of event
    public List<Appointment> getTargetEvent(String date) {
        List<Appointment> appointment = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.DATABASE_NAME, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String target_date = Constant.getDate(cursor.getLong(2), new SimpleDateFormat("yyyy-MM-dd"));
            if(date.equals(target_date)) {
                appointment.add(cursorToAppointment(cursor));
            }
            cursor.moveToNext();
        }
        // Note! Close cursor after use
        cursor.close();

        return appointment;
    }

    private Appointment cursorToAppointment(Cursor cursor) {
        Appointment appt = new Appointment();
        //Get values from cursor to create appointment
        appt.setId(cursor.getLong(0));
        appt.setSub_id(cursor.getLong(1));
        appt.setActualStartDate(cursor.getLong(2));
        appt.setEvent(cursor.getString(3));
        appt.setStartProperDate(cursor.getString(4));
        appt.setStartDate(cursor.getLong(5));
        appt.setEndDate(cursor.getLong(6));
        appt.setLocation(cursor.getString(7));
        appt.setNotes(cursor.getString(8));
        appt.setRemind(cursor.getInt(9));
        appt.setColour(cursor.getInt(10));

        return appt;
    }

    private int getCount() {
        Cursor cursor = database.query(DatabaseHelper.DATABASE_NAME,allColumns, null, null, null, null, null);
        int size = cursor.getCount();
        cursor.close();
        return size;
    }
}
