package mooncakemonster.orbitalcalendar.notifications;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by BAOJUN on 6/7/15.
 */
public class NotificationDatabase extends SQLiteOpenHelper {

    private static final String TAG = NotificationDatabase.class.getSimpleName();

    public String query = "CREATE TABLE " + NotificationData.NotificationInfo.TABLE_NAME + " (" +
            NotificationData.NotificationInfo.CLICKED + " TEXT, " +
            NotificationData.NotificationInfo.ACTION_DONE + " TEXT, " +
            NotificationData.NotificationInfo.ROW_ID + " TEXT, " +
            NotificationData.NotificationInfo.NOTIFICATION_ID + " INTEGER, " +
            NotificationData.NotificationInfo.TIMESTAMP + " INTEGER, " +
            NotificationData.NotificationInfo.EVENT_ID + " INTEGER, " +
            NotificationData.NotificationInfo.IMAGE_ID + " INTEGER, " +
            NotificationData.NotificationInfo.SENDER_USERNAME + " TEXT, " +
            NotificationData.NotificationInfo.MESSAGE + " TEXT, " +
            NotificationData.NotificationInfo.SENDER_EVENT + " TEXT, " +
            NotificationData.NotificationInfo.ACTION + " TEXT, " +
            NotificationData.NotificationInfo.SENDER_LOCATION + " TEXT, " +
            NotificationData.NotificationInfo.SENDER_NOTES + " TEXT, " +
            NotificationData.NotificationInfo.SELECTED_OPTION + " TEXT, " +
            NotificationData.NotificationInfo.START_DATE + " TEXT, " +
            NotificationData.NotificationInfo.END_DATE + " TEXT, " +
            NotificationData.NotificationInfo.START_TIME + " TEXT, " +
            NotificationData.NotificationInfo.END_TIME + " TEXT, " +
            NotificationData.NotificationInfo.REJECT_REASON + " TEXT, " +
            NotificationData.NotificationInfo.CONFIRM_ACTION + " TEXT); ";

    public NotificationDatabase (Context context) {
        super(context, NotificationData.NotificationInfo.DATABASE_NAME, null, NotificationData.NotificationInfo.DATABASE_VERSION);
        // Check if database is created
        Log.d(TAG, "Notification database created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(query);
        // Create table
        Log.d(TAG, "Notification table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // This method insets information into the database.
    public void putInformation(NotificationDatabase data, String clicked, String action_done, String row_id, int notification_id, long timestamp, int eventId, int imageId, String sender_username, String message, String sender_event, String action,
                               String sender_location, String sender_notes, String selected_option, String start_date, String end_date, String start_time, String end_time, String reject_reason, String confirm_action) {
        // Write data into database
        SQLiteDatabase sqLiteDatabase = data.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Add value from each column into contentvalue
        contentValues.put(NotificationData.NotificationInfo.CLICKED, clicked);
        contentValues.put(NotificationData.NotificationInfo.ACTION_DONE, action_done);
        contentValues.put(NotificationData.NotificationInfo.ROW_ID, row_id);
        contentValues.put(NotificationData.NotificationInfo.NOTIFICATION_ID, notification_id);
        contentValues.put(NotificationData.NotificationInfo.TIMESTAMP, timestamp);
        contentValues.put(NotificationData.NotificationInfo.EVENT_ID, eventId);
        contentValues.put(NotificationData.NotificationInfo.IMAGE_ID, imageId);
        contentValues.put(NotificationData.NotificationInfo.SENDER_USERNAME, sender_username);
        contentValues.put(NotificationData.NotificationInfo.MESSAGE, message);
        contentValues.put(NotificationData.NotificationInfo.SENDER_EVENT, sender_event);
        contentValues.put(NotificationData.NotificationInfo.ACTION, action);

        contentValues.put(NotificationData.NotificationInfo.SENDER_LOCATION, sender_location);
        contentValues.put(NotificationData.NotificationInfo.SENDER_NOTES, sender_notes);
        contentValues.put(NotificationData.NotificationInfo.SELECTED_OPTION, selected_option);
        contentValues.put(NotificationData.NotificationInfo.START_DATE, start_date);
        contentValues.put(NotificationData.NotificationInfo.END_DATE, end_date);
        contentValues.put(NotificationData.NotificationInfo.START_TIME, start_time);
        contentValues.put(NotificationData.NotificationInfo.END_TIME, end_time);

        contentValues.put(NotificationData.NotificationInfo.REJECT_REASON, reject_reason);

        contentValues.put(NotificationData.NotificationInfo.CONFIRM_ACTION, confirm_action);

        // Insert into sqlite database
        sqLiteDatabase.insert(NotificationData.NotificationInfo.TABLE_NAME, null, contentValues);
        Log.d(TAG, "One notification row inserted");
    }

    // Retrieve data from database
    public Cursor getInformation(NotificationDatabase data) {
        // Read data from sqlite database
        SQLiteDatabase sqLiteDatabase = data.getReadableDatabase();
        String[] columns = {NotificationData.NotificationInfo.CLICKED,
                            NotificationData.NotificationInfo.ACTION_DONE, NotificationData.NotificationInfo.ROW_ID,
                            NotificationData.NotificationInfo.NOTIFICATION_ID, NotificationData.NotificationInfo.TIMESTAMP,
                            NotificationData.NotificationInfo.EVENT_ID, NotificationData.NotificationInfo.IMAGE_ID,
                            NotificationData.NotificationInfo.SENDER_USERNAME, NotificationData.NotificationInfo.MESSAGE,
                            NotificationData.NotificationInfo.SENDER_EVENT, NotificationData.NotificationInfo.ACTION,
                            NotificationData.NotificationInfo.SENDER_LOCATION, NotificationData.NotificationInfo.SENDER_NOTES,
                            NotificationData.NotificationInfo.SELECTED_OPTION, NotificationData.NotificationInfo.START_DATE,
                            NotificationData.NotificationInfo.END_DATE, NotificationData.NotificationInfo.START_TIME,
                            NotificationData.NotificationInfo.END_TIME, NotificationData.NotificationInfo.REJECT_REASON,
                            NotificationData.NotificationInfo.CONFIRM_ACTION};

        // Points to first row of table
        return sqLiteDatabase.query(NotificationData.NotificationInfo.TABLE_NAME, columns, null, null, null, null, null);
    }


    // Update action to database
    public void updateInformation(NotificationDatabase data, String row_id, String clicked, String action_done, String selected_option) {
        String selection = NotificationData.NotificationInfo.ROW_ID + "=?";
        String[] args = { row_id };

        ContentValues values = new ContentValues();
        if(clicked != null) values.put(NotificationData.NotificationInfo.CLICKED, clicked);
        if(action_done != null) values.put(NotificationData.NotificationInfo.ACTION_DONE, action_done);
        if(selected_option != null) values.put(NotificationData.NotificationInfo.SELECTED_OPTION, selected_option);

        SQLiteDatabase sqLiteDatabase = data.getWritableDatabase();
        sqLiteDatabase.update(NotificationData.NotificationInfo.TABLE_NAME, values, selection, args);
    }

    // Update action to database
    public void updateUserInformation(NotificationDatabase data, String previous_username, String new_username) {
        String selection = NotificationData.NotificationInfo.SENDER_USERNAME + "=?";
        String[] args = { previous_username };
        Cursor cursor = getInformation(data);
        ContentValues values = new ContentValues();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if(cursor.getString(7).equals(previous_username)) {
                values.put(NotificationData.NotificationInfo.SENDER_USERNAME, new_username);
                SQLiteDatabase sqLiteDatabase = data.getWritableDatabase();
                sqLiteDatabase.update(NotificationData.NotificationInfo.TABLE_NAME, values, selection, args);
            }

            cursor.moveToNext();
        }

        cursor.close();
    }

    // This method retrieves all notifications. (Limit to only maximum 20 notifications)
    public List<NotificationItem> getAllNotifications(NotificationDatabase data) {
        List<NotificationItem> notificationItems = new ArrayList<>();
        Cursor cursor = getInformation(data);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if(notificationItems.size() > 20) notificationItems.remove(0);
            NotificationItem notificationItem = new NotificationItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5),
                                                cursor.getInt(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11),
                                                cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17), cursor.getString(18), cursor.getString(19));
            notificationItems.add(notificationItem);
            cursor.moveToNext();
        }

        cursor.close();
        Collections.reverse(notificationItems);
        return notificationItems;
    }

    // This method retrieves all notifications of a target user.
    public List<NotificationItem> getUserNotifications(NotificationDatabase data, String username) {
        List<NotificationItem> notificationItems = new ArrayList<>();
        Cursor cursor = getInformation(data);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if(cursor.getString(7).equals(username)) {
                NotificationItem notificationItem = new NotificationItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5),
                        cursor.getInt(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11),
                        cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17), cursor.getString(18), cursor.getString(19));
                notificationItems.add(notificationItem);
            }
            cursor.moveToNext();
        }

        cursor.close();
        Collections.reverse(notificationItems);
        return notificationItems;
    }

    // This method returns the number of notifications saved.
    public int notificationSize(NotificationDatabase data) {
        return data.getInformation(data).getCount();
    }

}
