package mooncakemonster.orbitalcalendar.friendlist;

/**
 * Created by BAOJUN on 6/7/15.
 */
public class FriendItem {
    private String timestamp;
    private String username;

    public FriendItem(String timestamp, String username) {
        this.timestamp = timestamp;
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
