package mooncakemonster.orbitalcalendar.votesend;

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
            VotingData.VotingInfo.EVENT_ATTENDANCE + " TEXT, " +
            VotingData.VotingInfo.START_DATE + " TEXT, " +
            VotingData.VotingInfo.END_DATE + " TEXT, " +
            VotingData.VotingInfo.START_TIME + " TEXT, " +
            VotingData.VotingInfo.END_TIME + " TEXT, " +
            VotingData.VotingInfo.CONFIRMED + " TEXT, " +
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
                               String event_participants, String event_voted_participants, String event_attendance, String start_date, String end_date, String start_time, String end_time,
                               String confirmed, String confirm_start_date, String confirm_end_date, String confirm_start_time, String confirm_end_time) {
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
        contentValues.put(VotingData.VotingInfo.EVENT_ATTENDANCE, event_attendance);
        contentValues.put(VotingData.VotingInfo.START_DATE, start_date);
        contentValues.put(VotingData.VotingInfo.END_DATE, end_date);
        contentValues.put(VotingData.VotingInfo.START_TIME, start_time);
        contentValues.put(VotingData.VotingInfo.END_TIME, end_time);
        contentValues.put(VotingData.VotingInfo.CONFIRMED, confirmed);
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
                VotingData.VotingInfo.EVENT_ATTENDANCE,
                VotingData.VotingInfo.START_DATE,
                VotingData.VotingInfo.END_DATE,
                VotingData.VotingInfo.START_TIME,
                VotingData.VotingInfo.END_TIME,
                VotingData.VotingInfo.CONFIRMED,
                VotingData.VotingInfo.CONFIRM_START_DATE,
                VotingData.VotingInfo.CONFIRM_END_DATE,
                VotingData.VotingInfo.CONFIRM_START_TIME,
                VotingData.VotingInfo.CONFIRM_END_TIME};

        // Points to first row of table
        return sqLiteDatabase.query(VotingData.VotingInfo.TABLE_NAME, columns, null, null, null, null, null);
    }

    // Update data from database
    public void updateInformation(VotingDatabase data, String event_id, String updated_voted_participants, String attendance,
                                  String confirmed, String confirmed_start_date, String confirmed_end_date, String confirmed_start_time, String confirmed_end_time) {

        Cursor cursor = getInformation(data);
        cursor.moveToFirst();

        /**************************************************************
        String selection = VotingData.VotingInfo.EVENT_ID + "=? AND " +
                VotingData.VotingInfo.EVENT_COLOUR + "=? AND " +
                VotingData.VotingInfo.EVENT_TITLE + "=? AND " +
                VotingData.VotingInfo.EVENT_LOCATION + "=? AND " +
                VotingData.VotingInfo.EVENT_PARTICIPANTS + "=? AND " +
                VotingData.VotingInfo.EVENT_VOTED_PARTICIPANTS + "=? AND " +
                VotingData.VotingInfo.EVENT_ATTENDANCE + "=? AND " +
                VotingData.VotingInfo.START_DATE + "=? AND " +
                VotingData.VotingInfo.END_DATE + "=? AND " +
                VotingData.VotingInfo.START_TIME + "=? AND " +
                VotingData.VotingInfo.END_TIME + "=? AND " +
                VotingData.VotingInfo.CONFIRM_START_DATE + "=? AND " +
                VotingData.VotingInfo.CONFIRM_END_DATE + "=? AND " +
                VotingData.VotingInfo.CONFIRM_START_TIME + "=? AND " +
                VotingData.VotingInfo.CONFIRM_END_TIME + "=?";
         ***************************************************************/

        //TODO: NOTE - Assumption: ALL EVENT_ID are unique
        //This is to ensure that ONLY the targeted row is updated.
        String selection = VotingData.VotingInfo.EVENT_ID + "=?";

        do {
            if (cursor.getString(0).equals(event_id)) {
                // Previous items
                /*******************************************************
                String[] args = {cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9),
                        cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14)};
                ********************************************************/
                String[] args = {cursor.getString(0)};

                // Update voted participants
                ContentValues values = new ContentValues();
                if (updated_voted_participants != null) {
                    String stored_username = cursor.getString(5);
                    //Cursor will return null string if field was previously empty, thus ensure it is not null before appending
                    if(stored_username == null || stored_username.isEmpty()) {
                        stored_username = updated_voted_participants + " ";
                    } else {
                        stored_username += (updated_voted_participants + " ");
                    }
                    values.put(VotingData.VotingInfo.EVENT_VOTED_PARTICIPANTS, stored_username);
                    Log.d("VotingDatabase", values.get(VotingData.VotingInfo.EVENT_VOTED_PARTICIPANTS).toString());
                }

                // Update attendance
                if (attendance != null) {
                    String stored_attendance = cursor.getString(6);
                    //Cursor will return null string if field was previously empty, thus ensure it is not null before appending
                    if(stored_attendance == null || stored_attendance.isEmpty()) {
                        stored_attendance = attendance + " ";
                    } else {
                        stored_attendance += (attendance + " ");
                    }
                    values.put(VotingData.VotingInfo.EVENT_ATTENDANCE, stored_attendance);
                }

                // Update confirmed status
                if(confirmed != null) values.put(VotingData.VotingInfo.CONFIRMED, confirmed);

                // Update confirmed dates
                if (confirmed_start_date != null) {
                    values.put(VotingData.VotingInfo.CONFIRM_START_DATE, confirmed_start_date);
                    values.put(VotingData.VotingInfo.CONFIRM_END_DATE, confirmed_end_date);
                    values.put(VotingData.VotingInfo.CONFIRM_START_TIME, confirmed_start_time);
                    values.put(VotingData.VotingInfo.CONFIRM_END_TIME, confirmed_end_time);
                }

                SQLiteDatabase sqLiteDatabase = data.getWritableDatabase();
                sqLiteDatabase.update(VotingData.VotingInfo.TABLE_NAME, values, selection, args);

                break;
            }
        } while (cursor.moveToNext());


        cursor.close();
    }

    // This method retrieves all votings.
    public List<VoteItem> getAllVotings(VotingDatabase data) {
        List<VoteItem> votings = new ArrayList<VoteItem>();
        Cursor cursor = getInformation(data);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            VoteItem voteItem = new VoteItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                    cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9),
                    cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15));
            votings.add(voteItem);
            cursor.moveToNext();
        }

        cursor.close();
        Collections.reverse(votings);

        return votings;
    }

    // This method retrieves all not confirmed date votings.
    public List<VoteItem> getNotConfirmedVotings(VotingDatabase data) {
        List<VoteItem> votings = new ArrayList<VoteItem>();
        Cursor cursor = getInformation(data);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            if(cursor.getString(11).equals("false")) {
                VoteItem voteItem = new VoteItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9),
                        cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15));
                votings.add(voteItem);
            }
            cursor.moveToNext();
        }

        cursor.close();
        Collections.reverse(votings);

        return votings;
    }

    // This method retrieves all confirmed date votings.
    public List<VoteItem> getConfirmedVotings(VotingDatabase data) {
        List<VoteItem> votings = new ArrayList<VoteItem>();
        Cursor cursor = getInformation(data);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            if(cursor.getString(11).equals("true")) {
                VoteItem voteItem = new VoteItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9),
                        cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15));
                votings.add(voteItem);
            }
            cursor.moveToNext();
        }

        cursor.close();
        Collections.reverse(votings);

        return votings;
    }

    // This method returns the voteItem of a specific vote created.
    public VoteItem getVoteItem(VotingDatabase data, String event_id) {
        VoteItem voteItem = new VoteItem();
        Cursor cursor = getInformation(data);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            if (event_id.equals(cursor.getString(0))) {
                voteItem = new VoteItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9),
                        cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15));
                break;
            }
            cursor.moveToNext();
        }

        cursor.close();
        return voteItem;
    }


    // This method returns the number of events saved.
    public int eventSize(VotingDatabase data) {
        return data.getInformation(data).getCount();
    }
}