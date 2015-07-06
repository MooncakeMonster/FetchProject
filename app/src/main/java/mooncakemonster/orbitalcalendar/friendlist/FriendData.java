package mooncakemonster.orbitalcalendar.friendlist;

import android.provider.BaseColumns;

/**
 * Created by BAOJUN on 6/7/15.
 */
public class FriendData {

    public FriendData() { }

    public static abstract class FriendInfo implements BaseColumns {
        public static final String FRIEND_IMAGE = "friend_image";
        public static final String FRIEND_USERNAME = "friend_username";

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "friend_storage";
        public static final String TABLE_NAME = "friend_username_storage";
    }
}
