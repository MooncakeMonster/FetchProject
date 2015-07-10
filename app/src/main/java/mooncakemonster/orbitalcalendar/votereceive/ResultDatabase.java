package mooncakemonster.orbitalcalendar.votereceive;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BAOJUN on 10/7/15.
 */
public class ResultDatabase extends SQLiteOpenHelper {
    public String query = "CREATE TABLE " + ResultData.ResultInfo.TABLE_NAME + " (" +
            ResultData.ResultInfo.EVENT_ID + " INTEGER, " +
            ResultData.ResultInfo.START_DATE + " TEXT, " +
            ResultData.ResultInfo.END_DATE + " TEXT, " +
            ResultData.ResultInfo.START_TIME + " TEXT, " +
            ResultData.ResultInfo.END_TIME + " TEXT, " +
            ResultData.ResultInfo.EVENT_PARTICIPANTS + " TEXT, " +
            ResultData.ResultInfo.SELECT_PARTICIPANTS + " TEXT, " +
            ResultData.ResultInfo.TOTAL + " INTEGER); ";

    public ResultDatabase(Context context) {
        super(context, ResultData.ResultInfo.DATABASE_NAME, null, ResultData.ResultInfo.DATABASE_VERSION);
        // Check if database is created
        Log.d("Resultbase operations", "Result database created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table
        db.execSQL(query);
        Log.d("Resultbase operations", "Vote table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // This method insets information into the database
    public void putInformation(ResultDatabase data, int event_id, String start_date, String end_date,
                               String start_time, String end_time, String event_participants, String select_participants, int total) {
        // Write data into database
        SQLiteDatabase sqLiteDatabase = data.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Add value from each column into contentvalue
        contentValues.put(ResultData.ResultInfo.EVENT_ID, event_id);
        contentValues.put(ResultData.ResultInfo.START_DATE, start_date);
        contentValues.put(ResultData.ResultInfo.END_DATE, end_date);
        contentValues.put(ResultData.ResultInfo.START_TIME, start_time);
        contentValues.put(ResultData.ResultInfo.END_TIME, end_time);
        contentValues.put(ResultData.ResultInfo.EVENT_PARTICIPANTS, event_participants);
        contentValues.put(ResultData.ResultInfo.SELECT_PARTICIPANTS, select_participants);
        contentValues.put(ResultData.ResultInfo.TOTAL, total);

        // Insert into sqlite database
        sqLiteDatabase.insert(ResultData.ResultInfo.TABLE_NAME, null, contentValues);
        Log.d("Votebase operations", "One vote row inserted");
    }

    // Retrieve data from database
    public Cursor getInformation(ResultDatabase data) {
        // Read data from sqlite database
        SQLiteDatabase sqLiteDatabase = data.getReadableDatabase();
        String[] columns = {ResultData.ResultInfo.EVENT_ID,
                ResultData.ResultInfo.START_DATE,
                ResultData.ResultInfo.END_DATE,
                ResultData.ResultInfo.START_TIME,
                ResultData.ResultInfo.END_TIME,
                ResultData.ResultInfo.EVENT_PARTICIPANTS,
                ResultData.ResultInfo.SELECT_PARTICIPANTS,
                ResultData.ResultInfo.TOTAL};

        // Points to first row of table
        return sqLiteDatabase.query(ResultData.ResultInfo.TABLE_NAME, columns, null, null, null, null, null);
    }

    // This method stores the participants according to the date and time they had selected.
    public void storeParticipants(ResultDatabase data, ResultItem resultItem) {
        Cursor cursor = getInformation(data);

        String split_start_date = resultItem.getStart_date();
        String split_end_date = resultItem.getEnd_date();
        String split_start_time = resultItem.getStart_time();
        String split_end_time = resultItem.getEnd_time();

        int size = split_start_date.length();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (resultItem.getEvent_id() == cursor.getInt(0)) {

                for (int i = 0; i < size; i++) {
                    String selection = ResultData.ResultInfo.EVENT_ID + " LIKE ? AND " +
                            ResultData.ResultInfo.START_DATE + " LIKE ? AND " +
                            ResultData.ResultInfo.END_DATE + " LIKE ? AND " +
                            ResultData.ResultInfo.START_TIME + " LIKE ? AND " +
                            ResultData.ResultInfo.END_TIME + " LIKE ? AND " +
                            ResultData.ResultInfo.EVENT_PARTICIPANTS + " LIKE ? AND " +
                            ResultData.ResultInfo.SELECT_PARTICIPANTS + " LIKE ? AND " +
                            ResultData.ResultInfo.TOTAL + " LIKE ? ";

                    String stored_username = cursor.getString(6);
                    String[] args = {stored_username};
                    stored_username += resultItem.getUsername();

                    ContentValues values = new ContentValues();
                    values.put(ResultData.ResultInfo.SELECT_PARTICIPANTS, stored_username);

                    SQLiteDatabase sqLiteDatabase = data.getWritableDatabase();
                    sqLiteDatabase.update(ResultData.ResultInfo.TABLE_NAME, values, selection, args);
                }
            }
            cursor.moveToNext();
        }
        cursor.close();
    }

    // This method retrieves all target votings.
    public List<ResultItem> getAllTargetResults(ResultDatabase data, int eventId) {
        List<ResultItem> result = new ArrayList<ResultItem>();
        Cursor cursor = getInformation(data);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            if (eventId == cursor.getInt(0)) {
                ResultItem resultItem = new ResultItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7));
                result.add(resultItem);
            } else if (eventId < cursor.getInt(0)) {
                break;
            }
            cursor.moveToNext();
        }

        cursor.close();
        return result;
    }
}
