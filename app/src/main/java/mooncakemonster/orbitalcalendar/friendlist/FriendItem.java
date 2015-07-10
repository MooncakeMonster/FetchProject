package mooncakemonster.orbitalcalendar.friendlist;

/**
 * Created by BAOJUN on 6/7/15.
 */
public class FriendItem {
    private String image;
    private String username;

    public FriendItem(String image, String username) {
        this.image = image;
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
