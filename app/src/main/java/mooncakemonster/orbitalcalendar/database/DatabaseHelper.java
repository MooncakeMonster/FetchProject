package mooncakemonster.orbitalcalendar.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper
{

    public static final String DATABASE_NAME = "appointment.db";
    //Serial number of the rows
    public static final String COLUMN_ID = "_id";
    //What is this appointment about
    public static final String EVENT = "event";
    //Date tentatively stored as millisecond
    public static final String STARTDATE = "startdate";
    public static final String ENDDATE = "enddate";
    //Where the event is held
    public static final String LOCATION = "location";
    //Miscellaneous notes
    public static final String NOTES = "notes";
    //Send reminder on N many minutes before the event; if zero, no notification
    public static final String REMIND = "remind";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = "CREATE TABLE " + DATABASE_NAME + "( " + COLUMN_ID + " INTEGER PRIMARY KEY, "
                                                                                         + EVENT + " TEXT NOT NULL, "
                                                                                         + STARTDATE + " INTEGER NOT NULL, "
                                                                                         + ENDDATE + "INTEGER, "
                                                                                         + LOCATION + " TEXT, "
                                                                                         + NOTES + " TEXT, "
                                                                                         + REMIND + " INTEGER NOT NULL"
                                                                                  + " );";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DATABASE_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(DatabaseHelper.class.getName(),"Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data.");
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }
}
