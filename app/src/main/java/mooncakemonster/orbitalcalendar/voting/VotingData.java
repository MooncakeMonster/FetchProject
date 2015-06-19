package mooncakemonster.orbitalcalendar.voting;

import android.provider.BaseColumns;

/**
 * This class stores the title of columns.
 */
public class VotingData {

    public VotingData() { }

    public static abstract class VotingInfo implements BaseColumns {
        public static final String EVENT_TITLE = "event_name";
        public static final String EVENT_LOCATION = "event_location";
        public static final String EVENT_PARTICIPANTS = "event_participants";

        public static final String START_DATE = "start_date";
        public static final String START_TIME = "start_time";

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "voting_storage";
        public static final String TABLE_NAME = "user_voting";
    }
}
