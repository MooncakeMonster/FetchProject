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
public class PictureDatabase extends SQLiteOpenHelper {

    public String query = "CREATE TABLE " + PictureData.TableInfo.TABLE_NAME + " (" +
                                            PictureData.TableInfo.SMILEY + " INTEGER NOT NULL, " +
                                            PictureData.TableInfo.TITLE + " TEXT, " +
                                            PictureData.TableInfo.DATE + " TEXT, " +
                                            PictureData.TableInfo.CAPTION + " TEXT, " +
                                            PictureData.TableInfo.IMAGE + " TEXT);";

    public PictureDatabase(Context context) {
        super(context, PictureData.TableInfo.DATABASE_NAME, null, PictureData.TableInfo.DATABASE_VERSION);
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
    public void putInformation(PictureDatabase data, int smiley, String title, String date, String caption, String image) {
        // Write data into database
        SQLiteDatabase sqLiteDatabase = data.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Add value from each column into contentvalue
        contentValues.put(PictureData.TableInfo.SMILEY, smiley);
        contentValues.put(PictureData.TableInfo.TITLE, title);
        contentValues.put(PictureData.TableInfo.DATE, date);
        contentValues.put(PictureData.TableInfo.CAPTION, caption);
        contentValues.put(PictureData.TableInfo.IMAGE, image);

        // Insert into sqlite database
        sqLiteDatabase.insert(PictureData.TableInfo.TABLE_NAME, null, contentValues);
        Log.d("Database operations", "One row inserted");
    }

    // Retrieve data from database
    public Cursor getInformation(PictureDatabase data) {
        // Read data from sqlite database
        SQLiteDatabase sqLiteDatabase = data.getReadableDatabase();
        String[] columns = { PictureData.TableInfo.SMILEY, PictureData.TableInfo.TITLE, PictureData.TableInfo.DATE, PictureData.TableInfo.CAPTION, PictureData.TableInfo.IMAGE };

        // Points to first row of table
        return sqLiteDatabase.query(PictureData.TableInfo.TABLE_NAME, columns, null, null, null, null, null);
    }
}
