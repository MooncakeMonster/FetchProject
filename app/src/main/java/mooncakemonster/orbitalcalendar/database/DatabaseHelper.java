package mooncakemonster.orbitalcalendar.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper
{
    //Allow only one instance of DatabaseHelper at any one time
    private static DatabaseHelper sInstance;
    //Name of database: appointment
    public static final String DATABASE_NAME = "appointment";
    //Serial number of the rows
    public static final String COLUMN_ID = "_id";
    //Serial number of sub events
    public static final String SUB_ID = "_sub_id";
    //Save the actual start date of few days event
    public static final String ACTUAL_START_DATE = "actual_start_date";
    //What is this appointment about
    public static final String EVENT = "event";
    //Start date for easy comparison: In format YYYY-MM-DD
    public static final String STARTPROPERDATE = "startproperdate";
    //Date tentatively stored as millisecond
    public static final String STARTDATE = "startdate";
    public static final String ENDDATE = "enddate";
    //Where the event is held
    public static final String LOCATION = "location";
    //Miscellaneous notes
    public static final String NOTES = "notes";
    //Send reminder on N many minutes before the event; if zero, no notification
    public static final String REMIND = "remind";
    //Get ID of the colour bear chosen by user
    public static final String COLOUR = "colour";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = "CREATE TABLE " + DATABASE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY, "
                                                                                         + SUB_ID + " INTEGER NOT NULL, "
                                                                                         + ACTUAL_START_DATE+ " TEXT NOT NULL, "
                                                                                         + EVENT + " TEXT NOT NULL, "
                                                                                         + STARTPROPERDATE + " TEXT NOT NULL, "
                                                                                         + STARTDATE + " INTEGER NOT NULL, "
                                                                                         + ENDDATE + " INTEGER, "
                                                                                         + LOCATION + " TEXT, "
                                                                                         + NOTES + " TEXT, "
                                                                                         + REMIND + " INTEGER NOT NULL, "
                                                                                         + COLOUR + " INTEGER NOT NULL "
                                                                                  + ");";

    public static synchronized DatabaseHelper getInstance(Context context) {

        // Use the application context, ensuring no accidental leakage an Activity's context.
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelper(Context context)
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
