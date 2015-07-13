package mooncakemonster.orbitalcalendar.notifications;

import android.provider.BaseColumns;

/**
 * Created by BAOJUN on 7/7/15.
 */
public class NotificationData {

    public NotificationData() { }

    public static abstract class NotificationInfo implements BaseColumns {
        public static final String NOTIFICATION_ID = "notification_id";
        public static final String EVENT_ID = "event_id";
        public static final String IMAGE_ID = "image_id";

        public static final String SENDER_USERNAME = "sender_username";
        public static final String MESSAGE = "message";
        public static final String SENDER_EVENT = "sender_event";
        public static final String ACTION = "action";
        public static final String SENDER_LOCATION = "sender_location";
        public static final String SENDER_NOTES = "sender_notes";

        public static final String START_DATE = "start_date";
        public static final String END_DATE = "end_date";
        public static final String START_TIME = "start_time";
        public static final String END_TIME = "end_time";

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "notification_storage";
        public static final String TABLE_NAME = "notification_intent_storage";
    }
}
