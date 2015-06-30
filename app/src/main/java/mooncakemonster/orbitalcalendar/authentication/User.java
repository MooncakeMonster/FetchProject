package mooncakemonster.orbitalcalendar.authentication;

/**
 * Created by BAOJUN on 30/6/15.
 */
public class User {
    private String email_address;
    private String username;
    private String password;

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
