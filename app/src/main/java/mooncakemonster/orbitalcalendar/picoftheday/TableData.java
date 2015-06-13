package mooncakemonster.orbitalcalendar.picoftheday;

import android.provider.BaseColumns;

/**
 * This class stores the title of the columns.
 */
public class TableData {

    public TableData() { }

    public static abstract class TableInfo implements BaseColumns {
        public static final String SMILEY = "smiley";
        public static final String TITLE = "title";
        public static final String DATE = "date";
        public static final String CAPTION = "caption";
        public static final String IMAGE = "image";

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "image_storage";
        public static final String TABLE_NAME = "user_image";
    }
}
