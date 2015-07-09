package mooncakemonster.orbitalcalendar.cloudant;

import com.cloudant.sync.datastore.BasicDocumentRevision;

import java.util.HashMap;
import java.util.Map;

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
    private int option_event_colour;
    private String option_event_title;
    private String option_event_location;
    private String option_event_notes;
    private String option_start_date;
    private String option_end_date;
    private String option_start_time;
    private String option_end_time;

    // Voting selected options
    private String selected_my_username;
    private int selected_event_colour;
    private String selected_event_title;
    private String selected_event_location;
    private String selected_event_notes;
    private String selected_start_date;
    private String selected_end_date;
    private String selected_start_time;
    private String selected_end_time;

    // Reject event
    private String reject_reason;


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

    public int getOption_event_colour() {
        return option_event_colour;
    }

    public void setOption_event_colour(int option_event_colour) {
        this.option_event_colour = option_event_colour;
    }

    public String getOption_event_title() {
        return option_event_title;
    }

    public void setOption_event_title(String option_event_title) {
        this.option_event_title = option_event_title;
    }

    public String getOption_event_location() {
        return option_event_location;
    }

    public void setOption_event_location(String option_event_location) {
        this.option_event_location = option_event_location;
    }

    public String getOption_event_notes() {
        return option_event_notes;
    }

    public void setOption_event_notes(String option_event_notes) {
        this.option_event_notes = option_event_notes;
    }

    public String getSelected_my_username() {
        return selected_my_username;
    }

    public void setSelected_my_username(String selected_my_username) {
        this.selected_my_username = selected_my_username;
    }

    public int getSelected_event_colour() {
        return selected_event_colour;
    }

    public void setSelected_event_colour(int selected_event_colour) {
        this.selected_event_colour = selected_event_colour;
    }

    public String getSelected_event_title() {
        return selected_event_title;
    }

    public void setSelected_event_title(String selected_event_title) {
        this.selected_event_title = selected_event_title;
    }

    public String getSelected_event_location() {
        return selected_event_location;
    }

    public void setSelected_event_location(String selected_event_location) {
        this.selected_event_location = selected_event_location;
    }

    public String getSelected_event_notes() {
        return selected_event_notes;
    }

    public void setSelected_event_notes(String selected_event_notes) {
        this.selected_event_notes = selected_event_notes;
    }

    public String getOption_start_date() {
        return option_start_date;
    }

    public void setOption_start_date(String option_start_date) {
        this.option_start_date = option_start_date;
    }

    public String getOption_end_date() {
        return option_end_date;
    }

    public void setOption_end_date(String option_end_date) {
        this.option_end_date = option_end_date;
    }

    public String getOption_start_time() {
        return option_start_time;
    }

    public void setOption_start_time(String option_start_time) {
        this.option_start_time = option_start_time;
    }

    public String getOption_end_time() {
        return option_end_time;
    }

    public void setOption_end_time(String option_end_time) {
        this.option_end_time = option_end_time;
    }

    public String getSelected_start_date() {
        return selected_start_date;
    }

    public void setSelected_start_date(String selected_start_date) {
        this.selected_start_date = selected_start_date;
    }

    public String getSelected_end_date() {
        return selected_end_date;
    }

    public void setSelected_end_date(String selected_end_date) {
        this.selected_end_date = selected_end_date;
    }

    public String getSelected_start_time() {
        return selected_start_time;
    }

    public void setSelected_start_time(String selected_start_time) {
        this.selected_start_time = selected_start_time;
    }

    public String getSelected_end_time() {
        return selected_end_time;
    }

    public void setSelected_end_time(String selected_end_time) {
        this.selected_end_time = selected_end_time;
    }

    public String getReject_reason() {
        return reject_reason;
    }

    public void setReject_reason(String reject_reason) {
        this.reject_reason = reject_reason;
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
        user.setOption_event_colour((int) ((Map) user_revised.get("voting_options")).get("option_event_colour"));
        user.setOption_event_title((String) ((Map) user_revised.get("voting_options")).get("option_event_title"));
        user.setOption_event_location((String) ((Map) user_revised.get("voting_options")).get("option_event_location"));
        user.setOption_event_notes((String) ((Map) user_revised.get("voting_options")).get("option_event_notes"));
        user.setOption_start_date((String) ((Map) user_revised.get("voting_options")).get("option_start_date"));
        user.setOption_end_date((String) ((Map) user_revised.get("voting_options")).get("option_end_date"));
        user.setOption_start_time((String) ((Map) user_revised.get("voting_options")).get("option_start_time"));
        user.setOption_end_time((String) ((Map) user_revised.get("voting_options")).get("option_end_time"));

        // Voting selected options
        user.setSelected_my_username((String) ((Map) user_revised.get("voting_selected")).get("selected_my_username"));
        user.setSelected_event_colour((int) ((Map) user_revised.get("voting_selected")).get("selected_event_colour"));
        user.setSelected_event_title((String) ((Map) user_revised.get("voting_selected")).get("selected_event_title"));
        user.setSelected_event_location((String) ((Map) user_revised.get("voting_selected")).get("selected_event_location"));
        user.setSelected_event_notes((String) ((Map) user_revised.get("voting_selected")).get("selected_event_notes"));
        user.setSelected_start_date((String) ((Map) user_revised.get("voting_selected")).get("selected_start_date"));
        user.setSelected_end_date((String) ((Map) user_revised.get("voting_selected")).get("selected_end_date"));
        user.setSelected_start_time((String) ((Map) user_revised.get("voting_selected")).get("selected_start_time"));
        user.setSelected_end_time((String) ((Map) user_revised.get("voting_selected")).get("selected_end_time"));

        // Reject event
        user.setSelected_end_time((String) ((Map) user_revised.get("voting_selected")).get("reject_reason"));

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
        voting_options.put("option_event_colour", option_event_colour);
        voting_options.put("option_event_title", option_event_title);
        voting_options.put("option_event_location", option_event_location);
        voting_options.put("option_event_notes", option_event_notes);
        voting_options.put("option_start_date", option_start_date);
        voting_options.put("option_end_date", option_end_date);
        voting_options.put("option_start_time", option_start_time);
        voting_options.put("option_end_time", option_end_time);

        // Voting selected options
        HashMap<String, Object> voting_selected = new HashMap<>();
        voting_selected.put("selected_my_username", selected_my_username);
        voting_selected.put("selected_event_colour", selected_event_colour);
        voting_selected.put("selected_event_title", selected_event_title);
        voting_selected.put("selected_event_location", selected_event_location);
        voting_selected.put("selected_event_notes", selected_event_notes);
        voting_selected.put("selected_start_date", selected_start_date);
        voting_selected.put("selected_end_date", selected_end_date);
        voting_selected.put("selected_start_time", selected_start_time);
        voting_selected.put("selected_end_time", selected_end_time);

        // Reject event
        voting_selected.put("reject_reason", reject_reason);

        // User
        user.put("user_details", user_details);
        user.put("voting_options", voting_options);
        user.put("voting_selected", voting_selected);

        return user;
    }
}