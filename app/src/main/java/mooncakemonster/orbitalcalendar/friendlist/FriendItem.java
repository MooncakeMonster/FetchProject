package mooncakemonster.orbitalcalendar.friendlist;

/**
 * Created by BAOJUN on 6/7/15.
 */
public class FriendItem {
    int image;
    String username;

    public FriendItem(int image, String username) {
        this.image = image;
        this.username = username;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
