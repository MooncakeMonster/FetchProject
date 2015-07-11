package mooncakemonster.orbitalcalendar.notifications;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BAOJUN on 6/7/15.
 */
public class NotificationDatabase extends SQLiteOpenHelper {

    private static final String TAG = NotificationDatabase.class.getSimpleName();

    public String query = "CREATE TABLE " + NotificationData.NotificationInfo.TABLE_NAME + " (" +
            NotificationData.NotificationInfo.NOTIFICATION_ID + " INTEGER, " +
            NotificationData.NotificationInfo.EVENT_ID + " INTEGER, " +
            NotificationData.NotificationInfo.IMAGE_ID + " INTEGER, " +
            NotificationData.NotificationInfo.SENDER_USERNAME + " TEXT, " +
            NotificationData.NotificationInfo.MESSAGE + " TEXT, " +
            NotificationData.NotificationInfo.SENDER_EVENT + " TEXT, " +
            NotificationData.NotificationInfo.ACTION + " TEXT, " +
            NotificationData.NotificationInfo.SENDER_LOCATION + " TEXT, " +
            NotificationData.NotificationInfo.SENDER_NOTES + " TEXT, " +
            NotificationData.NotificationInfo.VOTE_TYPE + " TEXT, " +
            NotificationData.NotificationInfo.START_DATE + " TEXT, " +
            NotificationData.NotificationInfo.END_DATE + " TEXT, " +
            NotificationData.NotificationInfo.START_TIME + " TEXT, " +
            NotificationData.NotificationInfo.END_TIME + " TEXT, " +
            NotificationData.NotificationInfo.INTENT + " TEXT); ";

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
    public void putInformation(NotificationDatabase data, int notification_id, int eventId, int imageId, String sender_username, String message, String sender_event, String action,
                               String sender_location, String sender_notes, String vote_type,
                               String start_date, String end_date, String start_time, String end_time, String intent) {
        // Write data into database
        SQLiteDatabase sqLiteDatabase = data.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Add value from each column into contentvalue
        contentValues.put(NotificationData.NotificationInfo.NOTIFICATION_ID, notification_id);
        contentValues.put(NotificationData.NotificationInfo.EVENT_ID, eventId);
        contentValues.put(NotificationData.NotificationInfo.IMAGE_ID, imageId);
        contentValues.put(NotificationData.NotificationInfo.SENDER_USERNAME, sender_username);
        contentValues.put(NotificationData.NotificationInfo.MESSAGE, message);
        contentValues.put(NotificationData.NotificationInfo.SENDER_EVENT, sender_event);
        contentValues.put(NotificationData.NotificationInfo.ACTION, action);

        contentValues.put(NotificationData.NotificationInfo.SENDER_LOCATION, sender_location);
        contentValues.put(NotificationData.NotificationInfo.SENDER_NOTES, sender_notes);
        contentValues.put(NotificationData.NotificationInfo.VOTE_TYPE, vote_type);
        contentValues.put(NotificationData.NotificationInfo.START_DATE, start_date);
        contentValues.put(NotificationData.NotificationInfo.END_DATE, end_date);
        contentValues.put(NotificationData.NotificationInfo.START_TIME, start_time);
        contentValues.put(NotificationData.NotificationInfo.END_TIME, end_time);

        contentValues.put(NotificationData.NotificationInfo.INTENT, intent);

        // Insert into sqlite database
        sqLiteDatabase.insert(NotificationData.NotificationInfo.TABLE_NAME, null, contentValues);
        Log.d(TAG, "One notification row inserted");
    }

    // Retrieve data from database
    public Cursor getInformation(NotificationDatabase data) {
        // Read data from sqlite database
        SQLiteDatabase sqLiteDatabase = data.getReadableDatabase();
        String[] columns = {NotificationData.NotificationInfo.NOTIFICATION_ID,
                            NotificationData.NotificationInfo.EVENT_ID, NotificationData.NotificationInfo.IMAGE_ID,
                            NotificationData.NotificationInfo.SENDER_USERNAME, NotificationData.NotificationInfo.MESSAGE,
                            NotificationData.NotificationInfo.SENDER_EVENT, NotificationData.NotificationInfo.ACTION,
                            NotificationData.NotificationInfo.SENDER_LOCATION, NotificationData.NotificationInfo.SENDER_NOTES,
                            NotificationData.NotificationInfo.VOTE_TYPE, NotificationData.NotificationInfo.START_DATE,
                            NotificationData.NotificationInfo.END_DATE, NotificationData.NotificationInfo.START_TIME,
                            NotificationData.NotificationInfo.END_TIME, NotificationData.NotificationInfo.INTENT};

        // Points to first row of table
        return sqLiteDatabase.query(NotificationData.NotificationInfo.TABLE_NAME, columns, null, null, null, null, null);
    }

    // This method retrieves all notifications.
    public List<NotificationItem> getAllNotifications(NotificationDatabase data) {
        List<NotificationItem> notificationItems = new ArrayList<>();
        Cursor cursor = getInformation(data);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            NotificationItem notificationItem = new NotificationItem(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getString(3), cursor.getString(4),
                                                cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9),
                                                cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14));
            notificationItems.add(notificationItem);
            cursor.moveToNext();
        }

        cursor.close();
        return notificationItems;
    }

}
