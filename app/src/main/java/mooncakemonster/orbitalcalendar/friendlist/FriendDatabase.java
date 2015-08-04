package mooncakemonster.orbitalcalendar.friendlist;

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
public class FriendDatabase extends SQLiteOpenHelper {

    private static final String TAG = FriendDatabase.class.getSimpleName();

    public String query = "CREATE TABLE " + FriendData.FriendInfo.TABLE_NAME + " (" +
                                            FriendData.FriendInfo.FRIEND_ADDED + " TEXT, " +
                                            FriendData.FriendInfo.FRIEND_TIMESTAMP + " INTEGER, " +
                                            FriendData.FriendInfo.FRIEND_USERNAME + " TEXT); ";

    public FriendDatabase (Context context) {
        super(context, FriendData.FriendInfo.DATABASE_NAME, null, FriendData.FriendInfo.DATABASE_VERSION);
        // Check if database is created
        Log.d(TAG, "Friend database created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(query);
        // Create table
        Log.d(TAG, "Friend table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // This method insets information into the database
    public void putInformation(FriendDatabase data, String friend_added, long timestamp, String username) {
        // Write data into database
        SQLiteDatabase sqLiteDatabase = data.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Add value from each column into contentvalue
        contentValues.put(FriendData.FriendInfo.FRIEND_ADDED, friend_added);
        contentValues.put(FriendData.FriendInfo.FRIEND_TIMESTAMP, timestamp);
        contentValues.put(FriendData.FriendInfo.FRIEND_USERNAME, username);

        // Insert into sqlite database
        sqLiteDatabase.insert(FriendData.FriendInfo.TABLE_NAME, null, contentValues);
        Log.d(TAG, "One friend row inserted");
    }

    // Retrieve data from database
    public Cursor getInformation(FriendDatabase data) {
        // Read data from sqlite database
        SQLiteDatabase sqLiteDatabase = data.getReadableDatabase();
        String[] columns = {FriendData.FriendInfo.FRIEND_ADDED,
                FriendData.FriendInfo.FRIEND_TIMESTAMP, FriendData.FriendInfo.FRIEND_USERNAME };

        // Points to first row of table
        return sqLiteDatabase.query(FriendData.FriendInfo.TABLE_NAME, columns, null, null, null, null, null);
    }

    // Update data from database
    public void updateInformation(FriendDatabase data, String friend_added, long timestamp, String username) {
        String selection = FriendData.FriendInfo.FRIEND_USERNAME + " =? ";
        String[] args = { username };

        ContentValues values = new ContentValues();
        values.put(FriendData.FriendInfo.FRIEND_TIMESTAMP, timestamp);
        values.put(FriendData.FriendInfo.FRIEND_ADDED, friend_added);

        SQLiteDatabase sqLiteDatabase = data.getWritableDatabase();
        sqLiteDatabase.update(FriendData.FriendInfo.TABLE_NAME, values, selection, args);
    }


    // Delete data from database
    public void deleteInformation(FriendDatabase data, String username) {

        String selection = FriendData.FriendInfo.FRIEND_USERNAME + " LIKE ? ";
        String[] args = { username };

        SQLiteDatabase sqLiteDatabase = data.getWritableDatabase();
        sqLiteDatabase.delete(FriendData.FriendInfo.TABLE_NAME, selection, args);
    }

    // This method retrieves all friends' username.
    public List<FriendItem> getAllFriendUsername(FriendDatabase data) {
        List<FriendItem> friend_list = new ArrayList<>();
        Cursor cursor = getInformation(data);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            FriendItem friend = new FriendItem(cursor.getString(0), cursor.getInt(1), cursor.getString(2));
            friend_list.add(friend);
            cursor.moveToNext();
        }

        cursor.close();

        Collections.sort(friend_list);
        return friend_list;
    }

    // This method retrieves all friends' username.
    public String getAllFriendUsernameString(FriendDatabase data) {
        String friend_list = "";
        Cursor cursor = getInformation(data);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            friend_list += cursor.getString(2) + " ";
            cursor.moveToNext();
        }

        cursor.close();

        return friend_list;
    }

    // This method retrieves all friends' username.
    public List<FriendItem> getAcceptedFriendUsername(FriendDatabase data) {
        List<FriendItem> friend_list = new ArrayList<>();
        Cursor cursor = getInformation(data);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if(cursor.getString(0).equals("true")) {
                FriendItem friend = new FriendItem(cursor.getString(0), cursor.getInt(1), cursor.getString(2));
                friend_list.add(friend);
            }
            cursor.moveToNext();
        }

        cursor.close();

        Collections.sort(friend_list);
        return friend_list;
    }

    // This method checks if the usernames are user's friends
    public boolean checkUsername(FriendDatabase data, String participants) {
        String[] split_participants = participants.split(" ");
        int size = split_participants.length;
        Cursor cursor = getInformation(data);
        int count = 0;

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            for (int i = 0; i < size; i++) {
                if (cursor.getString(0).equals("true") && cursor.getString(2).equals(split_participants[i]))
                    count++;
            }
            cursor.moveToNext();
        }

        cursor.close();
        if(count == size) return true;
        return false;
    }


    // This method checks if the username is user's friends
    public boolean checkSingleUsername(FriendDatabase data, String participant) {
        Cursor cursor = getInformation(data);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            if (cursor.getString(0).equals("true") && cursor.getString(2).equals(participant)) return true;
            cursor.moveToNext();
        }

        cursor.close();
        return false;
    }
}
