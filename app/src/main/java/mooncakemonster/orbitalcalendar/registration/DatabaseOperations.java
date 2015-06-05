package mooncakemonster.orbitalcalendar.registration;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by BAOJUN on 5/6/15.
 */
public class DatabaseOperations extends SQLiteOpenHelper {

    public static final int database_version = 1;
    public String CREATE_QUERY = "CREATE TABLE " + TableData.TableInfo.TABLE_NAME + " (" + TableData.TableInfo.USER_NAME + " TEXT," + TableData.TableInfo.USER_PASS + " TEXT );";

    public DatabaseOperations(Context context) {
        super(context, TableData.TableInfo.DATABASE_NAME, null, database_version);
        Log.d("Database operations", "Database created");
    }

    // This method creates table for user data storage.
    @Override
    public void onCreate(SQLiteDatabase sdb) {
        sdb.execSQL(CREATE_QUERY);
        Log.d("Database operations", "Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void putInformation (DatabaseOperations dop, String username, String password) {
        // Write data into database
        SQLiteDatabase SQ = dop.getWritableDatabase();

        // Add each value into column
        ContentValues cv = new ContentValues();
        cv.put(TableData.TableInfo.USER_NAME, username);
        cv.put(TableData.TableInfo.USER_PASS, password);

        long k = SQ.insert(TableData.TableInfo.TABLE_NAME, null, cv);
        Log.d("Database operations", "One row inserted");
    }

    // Retrieve data from SQLite database
    public Cursor getInformation(DatabaseOperations dop) {
        // Get data from SQLite
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String[] columns = {TableData.TableInfo.USER_NAME, TableData.TableInfo.USER_PASS };

        // Get data
        Cursor cursor = SQ.query(TableData.TableInfo.TABLE_NAME, columns, null, null, null, null, null);

        return cursor;
    }

    // Update new username changed by user
    public void updateUserInfo(DatabaseOperations dop, String user_name, String user_pass, String new_user_name) {
        SQLiteDatabase SQ = dop.getWritableDatabase();
        String selection = TableData.TableInfo.USER_NAME + " LIKE ? AND " + TableData.TableInfo.USER_PASS + " LIKE ?";
        String[] args = { user_name, user_pass };

        // Add new username into contentvalues
        ContentValues values = new ContentValues();
        values.put(TableData.TableInfo.USER_NAME, new_user_name);
        SQ.update(TableData.TableInfo.TABLE_NAME, values, selection, args);
    }
}
