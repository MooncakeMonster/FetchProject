package mooncakemonster.orbitalcalendar.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "appointment";
    //What is this appointment about
    private static final String EVENT = "event";
    //TODO: DATE IMPLEMENTATION
    private static final String DATE = "date";
    //Where the event is held
    private static final String LOCATION = "location";
    //Miscellaneous notes
    private static final String NOTES = "notes";
    //Send reminder on N many minutes before the event; if zero, no notification
    private static final String REMIND = "remind";

    //VERIFY the VERSION thing, appears to relate to updating the table;
    private static final int DATABASE_VERSION = 2;

    //TODO: FILL THE COMMAND FOR CREATING TABLE
    private static final String DATABASE_CREATE = "CREATE TABLE " + DATABASE_NAME + "(" + EVENT + "string";

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
