package mooncakemonster.orbitalcalendar.authentication;

import com.cloudant.sync.datastore.BasicDocumentRevision;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the details of users registered in the application.
 */
public class User {
    private String email_address;
    private String username;
    private String password;

    // Document in database representing this user to be revised
    private BasicDocumentRevision revision;

    public User() {

    }

    public User(String email_address, String username, String password) {
        this.setEmail_address(email_address);
        this.setUsername(username);
        this.setPassword(password);
    }

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

    public BasicDocumentRevision getDocumentRevision() {
        return revision;
    }

    // This method returns user details from revision
    public static User fromRevision(BasicDocumentRevision revision) {
        User user = new User();
        user.revision = revision;

        // Retrieve the user document from Cloudant
        Map<String, Object> user_revised = revision.asMap();

        // Only allow update of user details if both user document
        // and this class refer to the same user.
        //if(user_revised.containsKey("email_address") && user_revised.get("email_address").equals(user.email_address)) {
        user.setEmail_address((String) user_revised.get("email_address"));
        user.setUsername((String) user_revised.get("username"));
        user.setPassword((String) user_revised.get("encrypted_password"));
        return user;
    }

    // This method retrieves user details upon registration.
    public Map<String, Object> asMap() {
        HashMap<String, Object> user = new HashMap<>();
        user.put("email_address", email_address);
        user.put("username", username);
        user.put("encrypted_password", password);

        return user;
    }
}
