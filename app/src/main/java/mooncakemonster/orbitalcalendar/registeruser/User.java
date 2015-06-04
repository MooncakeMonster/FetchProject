package mooncakemonster.orbitalcalendar.registeruser;

/**
 * Created by BAOJUN on 4/6/15.
 */
public class User {
    String name, username, password;

    public User(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public User(String username, String password) {
        this.name = "";
        this.username = username;
        this.password = password;
    }
}
