package mooncakemonster.orbitalcalendar.friendlist;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by BAOJUN on 6/7/15.
 */
public class FriendItem implements Comparable<FriendItem>, Serializable {
    private String friend_added;
    private long timestamp;
    private String username;

    public FriendItem(String friend_added, long timestamp, String username) {
        this.friend_added = friend_added;
        this.timestamp = timestamp;
        this.username = username;
    }

    public String getFriend_added() {
        return friend_added;
    }

    public void setFriend_added(String friend_added) {
        this.friend_added = friend_added;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int compareTo(@NonNull FriendItem another) {
        if(this.username.charAt(0) < another.getUsername().charAt(0)) return -1;
        return 1;
    }
}
