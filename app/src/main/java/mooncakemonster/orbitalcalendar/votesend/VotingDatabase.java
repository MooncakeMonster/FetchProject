package mooncakemonster.orbitalcalendar.votesend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This class creates table for database.
 */
public class VotingDatabase extends SQLiteOpenHelper {

    public String query = "CREATE TABLE " + VotingData.VotingInfo.TABLE_NAME + " (" +
                                            VotingData.VotingInfo.EVENT_ID + " TEXT, " +
                                            VotingData.VotingInfo.EVENT_COLOUR + " TEXT, " +
                                            VotingData.VotingInfo.EVENT_TITLE + " TEXT, " +
                                            VotingData.VotingInfo.EVENT_LOCATION + " TEXT, " +
                                            VotingData.VotingInfo.EVENT_PARTICIPANTS + " TEXT, " +
                                            VotingData.VotingInfo.EVENT_VOTED_PARTICIPANTS + " TEXT, " +
                                            VotingData.VotingInfo.START_DATE + " TEXT, " +
                                            VotingData.VotingInfo.END_DATE + " TEXT, " +
                                            VotingData.VotingInfo.START_TIME + " TEXT, " +
                                            VotingData.VotingInfo.END_TIME + " TEXT, " +
                                            VotingData.VotingInfo.CONFIRM_START_DATE + " TEXT, " +
                                            VotingData.VotingInfo.CONFIRM_END_DATE + " TEXT, " +
                                            VotingData.VotingInfo.CONFIRM_START_TIME + " TEXT, " +
                                            VotingData.VotingInfo.CONFIRM_END_TIME + " TEXT); ";

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
    public void putInformation(VotingDatabase data, String event_id, String event_colour, String event_title, String event_location,
                               String event_participants, String event_voted_participants, String start_date, String end_date, String start_time, String end_time,
                               String confirm_start_date, String confirm_end_date, String confirm_start_time, String confirm_end_time) {
        // Write data into database
        SQLiteDatabase sqLiteDatabase = data.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Add value from each column into contentvalue
        contentValues.put(VotingData.VotingInfo.EVENT_ID, event_id);
        contentValues.put(VotingData.VotingInfo.EVENT_COLOUR, event_colour);
        contentValues.put(VotingData.VotingInfo.EVENT_TITLE, event_title);
        contentValues.put(VotingData.VotingInfo.EVENT_LOCATION, event_location);
        contentValues.put(VotingData.VotingInfo.EVENT_PARTICIPANTS, event_participants);
        contentValues.put(VotingData.VotingInfo.EVENT_VOTED_PARTICIPANTS, event_voted_participants);
        contentValues.put(VotingData.VotingInfo.START_DATE, start_date);
        contentValues.put(VotingData.VotingInfo.END_DATE, end_date);
        contentValues.put(VotingData.VotingInfo.START_TIME, start_time);
        contentValues.put(VotingData.VotingInfo.END_TIME, end_time);
        contentValues.put(VotingData.VotingInfo.CONFIRM_START_DATE, confirm_start_date);
        contentValues.put(VotingData.VotingInfo.CONFIRM_END_DATE, confirm_end_date);
        contentValues.put(VotingData.VotingInfo.CONFIRM_START_TIME, confirm_start_time);
        contentValues.put(VotingData.VotingInfo.CONFIRM_END_TIME, confirm_end_time);

        // Insert into sqlite database
        sqLiteDatabase.insert(VotingData.VotingInfo.TABLE_NAME, null, contentValues);
        Log.d("Votebase operations", "One vote row inserted");
    }

    // Retrieve data from database
    public Cursor getInformation(VotingDatabase data) {
        // Read data from sqlite database
        SQLiteDatabase sqLiteDatabase = data.getReadableDatabase();
        String[] columns = {VotingData.VotingInfo.EVENT_ID,
                             VotingData.VotingInfo.EVENT_COLOUR,
                             VotingData.VotingInfo.EVENT_TITLE,
                             VotingData.VotingInfo.EVENT_LOCATION,
                             VotingData.VotingInfo.EVENT_PARTICIPANTS,
                             VotingData.VotingInfo.EVENT_VOTED_PARTICIPANTS,
                             VotingData.VotingInfo.START_DATE,
                             VotingData.VotingInfo.END_DATE,
                             VotingData.VotingInfo.START_TIME,
                             VotingData.VotingInfo.END_TIME,
                             VotingData.VotingInfo.CONFIRM_START_DATE,
                             VotingData.VotingInfo.CONFIRM_END_DATE,
                             VotingData.VotingInfo.CONFIRM_START_TIME,
                             VotingData.VotingInfo.CONFIRM_END_TIME};

        // Points to first row of table
        return sqLiteDatabase.query(VotingData.VotingInfo.TABLE_NAME, columns, null, null, null, null, null);
    }

    // Update data from database TODO: Problem with updating latest voted participants into database
    public void updateInformation(VotingDatabase data, String event_id, String updated_voted_participants,
                                  String confirmed_start_date, String confirmed_end_date, String confirmed_start_time, String confirmed_end_time) {

        SQLiteDatabase sqLiteDatabase = data.getWritableDatabase();

        VoteItem voteItem = getTargetVoting(data, event_id);
        Log.d("VotingDatabase", voteItem.getEventId());
        Log.d("VotingDatabase", voteItem.getImageId());
        Log.d("VotingDatabase", voteItem.getEvent_title());
        Log.d("VotingDatabase", voteItem.getEvent_location());
        Log.d("VotingDatabase", voteItem.getEvent_participants());
        //Log.d("VotingDatabase", voteItem.getEvent_voted_participants());
        Log.d("VotingDatabase", voteItem.getEvent_start_date());
        Log.d("VotingDatabase", voteItem.getEvent_end_date());
        Log.d("VotingDatabase", voteItem.getEvent_start_time());
        Log.d("VotingDatabase", voteItem.getEvent_end_time());

        String selection = VotingData.VotingInfo.EVENT_ID + " LIKE ? AND " +
                VotingData.VotingInfo.EVENT_COLOUR + " LIKE ? AND " +
                VotingData.VotingInfo.EVENT_TITLE + " LIKE ? AND " +
                VotingData.VotingInfo.EVENT_LOCATION + " LIKE ? AND " +
                VotingData.VotingInfo.EVENT_PARTICIPANTS + " LIKE ? AND " +
                VotingData.VotingInfo.EVENT_VOTED_PARTICIPANTS + " LIKE ? AND " +
                VotingData.VotingInfo.START_DATE + " LIKE ? AND " +
                VotingData.VotingInfo.END_DATE + " LIKE ? AND " +
                VotingData.VotingInfo.START_TIME + " LIKE ? AND " +
                VotingData.VotingInfo.END_TIME + " LIKE ? AND " +
                VotingData.VotingInfo.CONFIRM_START_DATE + " LIKE ? AND " +
                VotingData.VotingInfo.CONFIRM_END_DATE + " LIKE ? AND " +
                VotingData.VotingInfo.CONFIRM_START_TIME + " LIKE ? AND " +
                VotingData.VotingInfo.CONFIRM_END_TIME + " LIKE ? ";

        // Previous items
        String[] args = {voteItem.getEventId(), voteItem.getImageId(), voteItem.getEvent_title(), voteItem.getEvent_location(),
                voteItem.getEvent_participants(), voteItem.getEvent_voted_participants(), voteItem.getEvent_start_date(),
                voteItem.getEvent_end_date(), voteItem.getEvent_start_time(), voteItem.getEvent_end_time(),
                voteItem.getEvent_confirm_start_date(), voteItem.getEvent_confirm_end_date(),
                voteItem.getEvent_confirm_start_time(), voteItem.getEvent_confirm_end_time()};

        // Update voted participants
        ContentValues values = new ContentValues();
        if(updated_voted_participants != null) {
            String current_voted_participants = voteItem.getEvent_voted_participants();
            if(current_voted_participants == null) current_voted_participants = "";
            values.put(VotingData.VotingInfo.EVENT_VOTED_PARTICIPANTS,
                    current_voted_participants + " " + updated_voted_participants);
            Log.d("VotingDatabase", values.get(VotingData.VotingInfo.EVENT_VOTED_PARTICIPANTS).toString());
        }

        // Update confirmed dates
        if(confirmed_start_date != null) {
            values.put(VotingData.VotingInfo.CONFIRM_START_DATE, confirmed_start_date);
            values.put(VotingData.VotingInfo.CONFIRM_END_DATE, confirmed_end_date);
            values.put(VotingData.VotingInfo.CONFIRM_START_TIME, confirmed_start_time);
            values.put(VotingData.VotingInfo.CONFIRM_END_TIME, confirmed_end_time);
        }

        sqLiteDatabase.update(VotingData.VotingInfo.TABLE_NAME, values, selection, args);
    }

    // This method retrieves the targeted voting.
    public VoteItem getTargetVoting(VotingDatabase data, String eventId) {
        VoteItem voteItem = new VoteItem();
        Cursor cursor = getInformation(data);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            if(cursor.getString(0).equals(eventId)) {
                voteItem = new VoteItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9),
                        cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13));
                break;
            } else {
                cursor.moveToNext();
            }
        }

        cursor.close();
        return voteItem;
    }

    // This method retrieves all votings.
    public List<VoteItem> getAllVotings(VotingDatabase data) {
        List<VoteItem> votings = new ArrayList<VoteItem>();
        Cursor cursor = getInformation(data);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            VoteItem voteItem = new VoteItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                    cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9),
                    cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13));
            votings.add(voteItem);
            cursor.moveToNext();
        }

        cursor.close();
        return votings;
    }

    // This method returns the number of events saved.
    public int eventSize(VotingDatabase data) {
        return getInformation(data).getCount();
    }
}
