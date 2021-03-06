package mooncakemonster.orbitalcalendar.authentication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class UserDatabase extends SQLiteOpenHelper {

    private static final String TAG = UserDatabase.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "fetch_api";
    private static final String TABLE_LOGIN = "login";

    // Column items
    private static final String KEY_IMAGE = "image";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";

    public UserDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_IMAGE + " TEXT, "
                + KEY_EMAIL + " TEXT,"
                + KEY_USERNAME + " TEXT " + ");";

        try {
            db.execSQL(CREATE_TABLE);
        } catch (SQLException e){
            Log.d("Database operations", "Table not created");
        }

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);

        // Create tables again
        onCreate(db);
    }

    // This method stores user details in database.
    public void addUser(String image, String email, String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE, image); // Image
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_USERNAME, username); // Username

        // Inserting Row
        long id = db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    // This method gets user details from database.
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("image", cursor.getString(0));
            user.put("email", cursor.getString(1));
            user.put("username", cursor.getString(2));
        }
        cursor.close();
        db.close();

        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    // This method checks if the user is already logged in.
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    // This method updates user information.
    public void updateUsers(String email, String previous_username, String new_username) {
        String selection = KEY_USERNAME + " =? ";
        String[] args = { previous_username };

        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, email);
        values.put(KEY_USERNAME, new_username);

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.update(TABLE_LOGIN, values, selection, args);
    }

    // This method deletes user from database.
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}