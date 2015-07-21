package mooncakemonster.orbitalcalendar.friendlist;

/**
 * Created by BAOJUN on 6/7/15.
 */
public class FriendItem {
    private byte[] image;
    private String username;

    public FriendItem(byte[] image, String username) {
        this.image = image;
        this.username = username;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
