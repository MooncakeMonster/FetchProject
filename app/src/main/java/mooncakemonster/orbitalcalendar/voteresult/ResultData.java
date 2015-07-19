package mooncakemonster.orbitalcalendar.voteresult;

import android.provider.BaseColumns;

/**
 * Created by BAOJUN on 10/7/15.
 */
public class ResultData {

    public ResultData() { }

    public static abstract class ResultInfo implements BaseColumns {
        public static final String EVENT_ID = "event_id";

        public static final String START_DATE = "start_date";
        public static final String END_DATE = "end_date";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";

        public static final String EVENT_PARTICIPANTS = "event_participants";
        public static final String SELECT_PARTICIPANTS = "select_participants";
        public static final String NOT_SELECTED_PARTICIPANTS = "not_selected_participants";
        public static final String REJECT_PARTICIPANTS = "reject_participants";

        public static final String TOTAL = "total";

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "result_storage";
        public static final String TABLE_NAME = "result_voting";
    }
}
