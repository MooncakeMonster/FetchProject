package mooncakemonster.orbitalcalendar.voting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class creates table for database.
 */
public class VotingDatabase extends SQLiteOpenHelper {

    public String query = "CREATE TABLE " + VotingData.VotingInfo.TABLE_NAME + " (" +
                                            VotingData.VotingInfo.EVENT_TITLE + " TEXT, " +
                                            VotingData.VotingInfo.EVENT_LOCATION + " TEXT, " +
                                            VotingData.VotingInfo.EVENT_PARTICIPANTS + " TEXT, " +
                                            VotingData.VotingInfo.START_DATE + " TEXT, " +
                                            VotingData.VotingInfo.END_DATE + " TEXT, " +
                                            VotingData.VotingInfo.START_TIME + " TEXT, " +
                                            VotingData.VotingInfo.END_TIME + " TEXT); ";

    public VotingDatabase(Context context) {
        super(context, VotingData.VotingInfo.DATABASE_NAME, null, VotingData.VotingInfo.DATABASE_VERSION);
        // Check if database is created
        Log.d("Votebase operations", "Vote database created");
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table
        db.execSQL(query);
        Log.d("Votebase operations", "Vote table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // This method insets information into the database
    public void putInformation(VotingDatabase data, String event_title, String event_location, String event_participants, String start_date, String start_time, String end_date, String end_time) {
        // Write data into database
        SQLiteDatabase sqLiteDatabase = data.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Add value from each column into contentvalue
        contentValues.put(VotingData.VotingInfo.EVENT_TITLE, event_title);
        contentValues.put(VotingData.VotingInfo.EVENT_LOCATION, event_location);
        contentValues.put(VotingData.VotingInfo.EVENT_PARTICIPANTS, event_participants);
        contentValues.put(VotingData.VotingInfo.START_DATE, start_date);
        contentValues.put(VotingData.VotingInfo.END_DATE, end_date);
        contentValues.put(VotingData.VotingInfo.START_TIME, start_time);
        contentValues.put(VotingData.VotingInfo.END_TIME, end_time);

        // Insert into sqlite database
        sqLiteDatabase.insert(VotingData.VotingInfo.TABLE_NAME, null, contentValues);
        Log.d("Votebase operations", "One vote row inserted");
    }

    // Retrieve data from database
    public Cursor getInformation(VotingDatabase data) {
        // Read data from sqlite database
        SQLiteDatabase sqLiteDatabase = data.getReadableDatabase();
        String[] columns = { VotingData.VotingInfo.EVENT_TITLE,
                             VotingData.VotingInfo.EVENT_LOCATION,
                             VotingData.VotingInfo.EVENT_PARTICIPANTS,
                             VotingData.VotingInfo.START_DATE,
                             VotingData.VotingInfo.END_DATE,
                             VotingData.VotingInfo.START_TIME,
                             VotingData.VotingInfo.END_TIME };

        // Points to first row of table
        return sqLiteDatabase.query(VotingData.VotingInfo.TABLE_NAME, columns, null, null, null, null, null);
    }
}
