package mooncakemonster.orbitalcalendar.cloudant;

import com.cloudant.sync.datastore.BasicDocumentRevision;

import java.util.HashMap;
import java.util.Map;

import mooncakemonster.orbitalcalendar.voteinvitation.VoteOptionItem;
import mooncakemonster.orbitalcalendar.voteinvitation.VoteSelectedItem;

/**
 * This class represents the details of users registered in the application.
 */
public class User {

    // User details
    private String email_address;
    private String username;
    private String password;

    // Voting options
    private String option_my_username;
    private VoteOptionItem voteOptionItem;

    // Voting selected options
    private String selected_my_username;
    private VoteSelectedItem voteSelectedItem;


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

    public String getOption_my_username() {
        return option_my_username;
    }

    public void setOption_my_username(String option_my_username) {
        this.option_my_username = option_my_username;
    }

    public VoteOptionItem getVoteOptionItem() {
        return voteOptionItem;
    }

    public void setVoteOptionItem(VoteOptionItem voteOptionItem) {
        this.voteOptionItem = voteOptionItem;
    }

    public String getSelected_my_username() {
        return selected_my_username;
    }

    public void setSelected_my_username(String selected_my_username) {
        this.selected_my_username = selected_my_username;
    }

    public VoteSelectedItem getVoteSelectedItem() {
        return voteSelectedItem;
    }

    public void setVoteSelectedItem(VoteSelectedItem voteSelectedItem) {
        this.voteSelectedItem = voteSelectedItem;
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

        // User details
        user.setEmail_address((String) ((Map) user_revised.get("user_details")).get("email_address"));
        user.setUsername((String) ((Map) user_revised.get("user_details")).get("username"));
        user.setPassword((String) ((Map) user_revised.get("user_details")).get("encrypted_password"));

        // Voting options
        user.setOption_my_username((String) ((Map) user_revised.get("voting_options")).get("option_my_username"));
        user.setVoteOptionItem((VoteOptionItem) ((Map) user_revised.get("voting_options")).get("option_item"));

        // Voting selected options
        user.setSelected_my_username((String) ((Map) user_revised.get("voting_selected")).get("selected_my_username"));
        user.setVoteSelectedItem((VoteSelectedItem) ((Map) user_revised.get("voting_selected")).get("selected_item"));

        return user;
    }

    // This method retrieves user details upon registration.
    public Map<String, Object> asMap() {
        HashMap<String, Object> user = new HashMap<>();

        // User details
        HashMap<String, Object> user_details = new HashMap<>();
        user_details.put("email_address", email_address);
        user_details.put("username", username);
        user_details.put("encrypted_password", password);

        // Voting options
        HashMap<String, Object> voting_options = new HashMap<>();
        voting_options.put("option_my_username", option_my_username);
        voting_options.put("option_item", voteOptionItem);

        // Voting selected options
        HashMap<String, Object> voting_selected = new HashMap<>();
        voting_selected.put("selected_my_username", selected_my_username);
        voting_selected.put("selected_item", voteSelectedItem);

        // User document
        user.put("user_details", user_details);
        user.put("voting_options", voting_options);
        user.put("voting_selected", voting_selected);

        return user;
    }
}
