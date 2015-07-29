package mooncakemonster.orbitalcalendar.votesend;

import android.provider.BaseColumns;

/**
 * This class stores the title of columns.
 */
public class VotingData {

    public VotingData() { }

    public static abstract class VotingInfo implements BaseColumns {
        public static final String EVENT_ID = "event_id";

        public static final String EVENT_COLOUR = "event_colour";
        public static final String EVENT_TITLE = "event_name";
        public static final String EVENT_LOCATION = "event_location";
        public static final String EVENT_PARTICIPANTS = "event_participants";
        public static final String EVENT_VOTED_PARTICIPANTS = "event_voted_participants";
        public static final String EVENT_ATTENDANCE = "event_attendance";

        public static final String START_DATE = "start_date";
        public static final String END_DATE = "end_date";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";

        public static final String CONFIRMED = "confirmed";
        public static final String CONFIRM_START_DATE = "confirm_start_date";
        public static final String CONFIRM_END_DATE = "confirm_end_date";
        public static final String CONFIRM_START_TIME = "confirm_start_time";
        public static final String CONFIRM_END_TIME = "confirm_end_time";

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "voting_storage";
        public static final String TABLE_NAME = "voting_table";
    }
}
