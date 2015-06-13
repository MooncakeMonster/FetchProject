package mooncakemonster.orbitalcalendar.picoftheday;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class creates table for database.
 */
public class TableDatabase extends SQLiteOpenHelper {

    public String query = "CREATE TABLE " + TableData.TableInfo.TABLE_NAME + " ( " +
                                            TableData.TableInfo.SMILEY + " INTEGER NOT NULL, " +
                                            TableData.TableInfo.TITLE + " TEXT, " +
                                            TableData.TableInfo.DATE + " TEXT, " +
                                            TableData.TableInfo.CAPTION + " TEXT, " +
                                            TableData.TableInfo.IMAGE + " TEXT ); ";

    public TableDatabase(Context context) {
        super(context, TableData.TableInfo.DATABASE_NAME, null, TableData.TableInfo.DATABASE_VERSION);
        // Check if database is created
        Log.d("Database operations", "Database created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table
        db.execSQL(query);
        Log.d("Database operations", "Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Insert user information into the database
    public long putInformation(TableDatabase data, int smiley, String title, String date, String caption, String image) {
        // Write data into database
        SQLiteDatabase sqLiteDatabase = data.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Add value from each column into contentvalue
        contentValues.put(TableData.TableInfo.SMILEY, smiley);
        contentValues.put(TableData.TableInfo.TITLE, title);
        contentValues.put(TableData.TableInfo.DATE, date);
        contentValues.put(TableData.TableInfo.CAPTION, caption);
        contentValues.put(TableData.TableInfo.IMAGE, image);

        // Insert into sqlite database
        long id = sqLiteDatabase.insert(TableData.TableInfo.TABLE_NAME, null, contentValues);
        Log.d("Database operations", "One row inserted");

        return id;
    }

    // Retrieve data from database
    public Cursor getInformation(TableDatabase data) {
        // Read data from sqlite database
        SQLiteDatabase sqLiteDatabase = data.getReadableDatabase();
        String[] columns = { TableData.TableInfo.SMILEY, TableData.TableInfo.TITLE, TableData.TableInfo.DATE, TableData.TableInfo.CAPTION, TableData.TableInfo.IMAGE };

        // Points to first row of table
        return sqLiteDatabase.query(TableData.TableInfo.TABLE_NAME, columns, null, null, null, null, null);
    }
}
