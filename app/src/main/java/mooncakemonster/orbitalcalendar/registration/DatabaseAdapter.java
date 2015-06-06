package mooncakemonster.orbitalcalendar.registration;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This program creates or make amendments to the database for user details storage.
 */

public class DatabaseAdapter extends SQLiteOpenHelper {

    // Create table columns
    private static final String CREATE_TABLE = "CREATE TABLE " + TableData.TableInfo.TABLE_NAME + " (" + TableData.TableInfo.USER_EMAIL + " VARCHAR(255), " + TableData.TableInfo.USER_NAME + " VARCHAR(255), " + TableData.TableInfo.USER_PASS + " VARCHAR(255));";

    // Constructor
    public DatabaseAdapter(Context context) {
        super(context, TableData.TableInfo.DATABASE_NAME, null, TableData.TableInfo.DATABASE_VERSION);
        Log.d("Database operations", "Database created");
    }

    // This method creates table for user data storage. It is called when database is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);
        } catch (SQLException e){
            Log.d("Database operations", "Table not created");
        }
        Log.d("Database operations", "Table created");
    }

    // This method is called when database is upgraded; such as adding/dropping tables.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // This method inserts user information into database.
    public long insertData (DatabaseAdapter dop, String useremail, String username, String password) {
        // Write data into database
        SQLiteDatabase SQ = dop.getWritableDatabase();

        // Add each value into column
        ContentValues cv = new ContentValues();
        cv.put(TableData.TableInfo.USER_EMAIL, useremail);
        cv.put(TableData.TableInfo.USER_NAME, username);
        cv.put(TableData.TableInfo.USER_PASS, password);

        // Get row id of accounts from the table.
        long id = SQ.insert(TableData.TableInfo.TABLE_NAME, null, cv);
        Log.d("Database operations", "One row inserted");

        return id;
    }

    // This method retrieve all data from database TODO: Remove once app is up.
    public static String getAllUsers(DatabaseAdapter dop) {
        Cursor cursor = getCursor(dop);
        StringBuffer buffer = new StringBuffer();

        // Collect all database
        while(cursor.moveToNext()) {
            int email_index = cursor.getColumnIndex(TableData.TableInfo.USER_EMAIL);
            String email = cursor.getString(email_index);
            int name_index = cursor.getColumnIndex(TableData.TableInfo.USER_NAME);
            String username = cursor.getString(name_index);
            int pass_index = cursor.getColumnIndex(TableData.TableInfo.USER_PASS);
            String password = cursor.getString(pass_index);
            buffer.append("Email: " + email + "\nName: " + username + "\nPassword:" + password + "\n\n");
        }

        return buffer.toString();
    }

    // This method prevents duplicate emails and username
    public static int checkDuplicate(DatabaseAdapter dop, String user_email, String user_name) {
        Cursor cursor = getCursor(dop);

        // Ensures no duplicate email and username
        while(cursor.moveToNext()) {
            String email = cursor.getString(cursor.getColumnIndex(TableData.TableInfo.USER_EMAIL));
            String username = cursor.getString(cursor.getColumnIndex(TableData.TableInfo.USER_NAME));

            if(user_email.equals(email)) return 1;
            else if(user_name.equals(username)) return 2;
        }
        return 0;
    }

    // This method returns the cursor of the database
    public static Cursor getCursor(DatabaseAdapter dop) {
        // Get data from SQLite
        SQLiteDatabase SQ = dop.getReadableDatabase();
        String[] columns = { TableData.TableInfo.USER_EMAIL, TableData.TableInfo.USER_NAME, TableData.TableInfo.USER_PASS };

        // Get columns data
        Cursor cursor = SQ.query(TableData.TableInfo.TABLE_NAME, columns, null, null, null, null, null);

        return cursor;
    }

    // This method updates new username changed by a specific user
    public void updateUserInfo(DatabaseAdapter dop, String user_name, String user_pass, String new_user_name) {
        // TODO: Update successful only for per phone;
        SQLiteDatabase SQ = dop.getWritableDatabase();
        String selection = TableData.TableInfo.USER_NAME + " LIKE ? AND " + TableData.TableInfo.USER_PASS + " LIKE ?";
        String[] args = { user_name, user_pass };

        // Add new username into contentvalues
        ContentValues values = new ContentValues();
        values.put(TableData.TableInfo.USER_NAME, new_user_name);
        SQ.update(TableData.TableInfo.TABLE_NAME, values, selection, args);
    }
}
