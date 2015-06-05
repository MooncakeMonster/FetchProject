package mooncakemonster.orbitalcalendar.registration;

import android.provider.BaseColumns;

/**
 * This program creates the items for the table database.
 */
public class TableData {

    public TableData() { }

    public static abstract class TableInfo implements BaseColumns {
        public static final String USER_EMAIL = "user_email";
        public static final String USER_NAME = "user_name";
        public static final String USER_PASS = "user_pass";
        public static final String DATABASE_NAME = "user_info";
        public static final String TABLE_NAME = "reg_info";
    }
}
