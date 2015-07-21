package mooncakemonster.orbitalcalendar.cloudant;

import com.cloudant.sync.datastore.BasicDocumentRevision;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the details of users registered in the application.
 */
public class User {

    // User details
    private String image;
    private String email_address;
    private String username;
    private String password;

    // Voting options
    private String option_my_username;
    private int option_event_id;
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
    private int selected_event_id;
    private int selected_event_colour;
    private String selected_event_title;
    private String selected_event_location;
    private String selected_event_notes;
    private String selected_start_date;
    private String selected_end_date;
    private String selected_start_time;
    private String selected_end_time;
    private String not_selected_start_date;
    private String not_selected_end_date;
    private String not_selected_start_time;
    private String not_selected_end_time;

    // Reject event
    private String reject_reason;

    // Voting Confirm data and time
    private String confirm_my_username;
    private int confirm_event_id;
    private int confirm_event_colour;
    private String confirm_event_title;
    private String confirm_start_date;
    private String confirm_end_date;
    private String confirm_start_time;
    private String confirm_end_time;

    // Voting Reminder
    private String reminder_my_username;
    private int reminder_event_id;
    private int reminder_event_colour;
    private String reminder_event_title;

    // Voting attendance
    private String attendance_my_username;
    private int attendance_event_id;
    private int attendance_event_colour;
    private String attendance_event_title;

    // Document in database representing this user to be revised
    private BasicDocumentRevision revision;

    public User() {

    }

    public User(String image, String email_address, String username, String password) {
        this.setImage(image);
        this.setEmail_address(email_address);
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public void setOption_event_id(int option_event_id) {
        this.option_event_id = option_event_id;
    }

    public int getOption_event_id() {
        return option_event_id;
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

    public void setSelected_event_id(int selected_event_id) {
        this.selected_event_id = selected_event_id;
    }

    public int getSelected_event_id() {
        return selected_event_id;
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

    public String getNot_selected_start_date() {
        return not_selected_start_date;
    }

    public void setNot_selected_start_date(String not_selected_start_date) {
        this.not_selected_start_date = not_selected_start_date;
    }

    public String getNot_selected_end_date() {
        return not_selected_end_date;
    }

    public void setNot_selected_end_date(String not_selected_end_date) {
        this.not_selected_end_date = not_selected_end_date;
    }

    public String getNot_selected_start_time() {
        return not_selected_start_time;
    }

    public void setNot_selected_start_time(String not_selected_start_time) {
        this.not_selected_start_time = not_selected_start_time;
    }

    public String getNot_selected_end_time() {
        return not_selected_end_time;
    }

    public void setNot_selected_end_time(String not_selected_end_time) {
        this.not_selected_end_time = not_selected_end_time;
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

    public String getConfirm_my_username() {
        return confirm_my_username;
    }

    public void setConfirm_my_username(String confirm_my_username) {
        this.confirm_my_username = confirm_my_username;
    }

    public int getConfirm_event_id() {
        return confirm_event_id;
    }

    public void setConfirm_event_id(int confirm_event_id) {
        this.confirm_event_id = confirm_event_id;
    }

    public int getConfirm_event_colour() {
        return confirm_event_colour;
    }

    public void setConfirm_event_colour(int confirm_event_colour) {
        this.confirm_event_colour = confirm_event_colour;
    }

    public String getConfirm_event_title() {
        return confirm_event_title;
    }

    public void setConfirm_event_title(String confirm_event_title) {
        this.confirm_event_title = confirm_event_title;
    }

    public String getConfirm_start_date() {
        return confirm_start_date;
    }

    public void setConfirm_start_date(String confirm_start_date) {
        this.confirm_start_date = confirm_start_date;
    }

    public String getConfirm_end_date() {
        return confirm_end_date;
    }

    public void setConfirm_end_date(String confirm_end_date) {
        this.confirm_end_date = confirm_end_date;
    }

    public String getConfirm_start_time() {
        return confirm_start_time;
    }

    public void setConfirm_start_time(String confirm_start_time) {
        this.confirm_start_time = confirm_start_time;
    }

    public String getConfirm_end_time() {
        return confirm_end_time;
    }

    public void setConfirm_end_time(String confirm_end_time) {
        this.confirm_end_time = confirm_end_time;
    }

    public String getReminder_my_username() {
        return reminder_my_username;
    }

    public void setReminder_my_username(String reminder_my_username) {
        this.reminder_my_username = reminder_my_username;
    }

    public int getReminder_event_id() {
        return reminder_event_id;
    }

    public void setReminder_event_id(int reminder_event_id) {
        this.reminder_event_id = reminder_event_id;
    }

    public int getReminder_event_colour() {
        return reminder_event_colour;
    }

    public void setReminder_event_colour(int reminder_event_colour) {
        this.reminder_event_colour = reminder_event_colour;
    }

    public String getReminder_event_title() {
        return reminder_event_title;
    }

    public void setReminder_event_title(String reminder_event_title) {
        this.reminder_event_title = reminder_event_title;
    }

    public String getAttendance_my_username() {
        return attendance_my_username;
    }

    public void setAttendance_my_username(String attendance_my_username) {
        this.attendance_my_username = attendance_my_username;
    }

    public int getAttendance_event_id() {
        return attendance_event_id;
    }

    public void setAttendance_event_id(int attendance_event_id) {
        this.attendance_event_id = attendance_event_id;
    }

    public int getAttendance_event_colour() {
        return attendance_event_colour;
    }

    public void setAttendance_event_colour(int attendance_event_colour) {
        this.attendance_event_colour = attendance_event_colour;
    }

    public String getAttendance_event_title() {
        return attendance_event_title;
    }

    public void setAttendance_event_title(String attendance_event_title) {
        this.attendance_event_title = attendance_event_title;
    }

    // This method returns user details from revision
    public static User fromRevision(BasicDocumentRevision revision) {
        User user = new User();
        user.revision = revision;

        // Retrieve the user document from Cloudant
        Map<String, Object> user_revised = revision.asMap();

        // User details
        user.setImage((String) ((Map) user_revised.get("user_details")).get("image"));
        user.setEmail_address((String) ((Map) user_revised.get("user_details")).get("email_address"));
        user.setUsername((String) ((Map) user_revised.get("user_details")).get("username"));
        user.setPassword((String) ((Map) user_revised.get("user_details")).get("encrypted_password"));

        // Voting options
        user.setOption_my_username((String) ((Map) user_revised.get("voting_options")).get("option_my_username"));
        user.setOption_event_id((int) ((Map) user_revised.get("voting_options")).get("option_event_id"));
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
        user.setSelected_event_id((int) ((Map) user_revised.get("voting_selected")).get("selected_event_id"));
        user.setSelected_event_colour((int) ((Map) user_revised.get("voting_selected")).get("selected_event_colour"));
        user.setSelected_event_title((String) ((Map) user_revised.get("voting_selected")).get("selected_event_title"));
        user.setSelected_event_location((String) ((Map) user_revised.get("voting_selected")).get("selected_event_location"));
        user.setSelected_event_notes((String) ((Map) user_revised.get("voting_selected")).get("selected_event_notes"));
        user.setSelected_start_date((String) ((Map) user_revised.get("voting_selected")).get("selected_start_date"));
        user.setSelected_end_date((String) ((Map) user_revised.get("voting_selected")).get("selected_end_date"));
        user.setSelected_start_time((String) ((Map) user_revised.get("voting_selected")).get("selected_start_time"));
        user.setSelected_end_time((String) ((Map) user_revised.get("voting_selected")).get("selected_end_time"));
        user.setNot_selected_start_date((String) ((Map) user_revised.get("voting_selected")).get("not_selected_start_date"));
        user.setNot_selected_end_date((String) ((Map) user_revised.get("voting_selected")).get("not_selected_end_date"));
        user.setNot_selected_start_time((String) ((Map) user_revised.get("voting_selected")).get("not_selected_start_time"));
        user.setNot_selected_end_time((String) ((Map) user_revised.get("voting_selected")).get("not_selected_end_time"));
        user.setReject_reason((String) ((Map) user_revised.get("voting_selected")).get("reject_reason"));

        // Voting confirm
        user.setConfirm_my_username((String) ((Map) user_revised.get("voting_confirmed")).get("confirm_my_username"));
        user.setConfirm_event_id((int) ((Map) user_revised.get("voting_confirmed")).get("confirm_event_id"));
        user.setConfirm_event_colour((int) ((Map) user_revised.get("voting_confirmed")).get("confirm_event_colour"));
        user.setConfirm_event_title((String) ((Map) user_revised.get("voting_confirmed")).get("confirm_event_title"));
        user.setConfirm_start_date((String) ((Map) user_revised.get("voting_confirmed")).get("confirm_start_date"));
        user.setConfirm_end_date((String) ((Map) user_revised.get("voting_confirmed")).get("confirm_end_date"));
        user.setConfirm_start_time((String) ((Map) user_revised.get("voting_confirmed")).get("confirm_start_time"));
        user.setConfirm_end_time((String) ((Map) user_revised.get("voting_confirmed")).get("confirm_end_time"));

        // Voting reminder
        user.setReminder_my_username((String) ((Map) user_revised.get("voting_reminder")).get("reminder_my_username"));
        user.setReminder_event_id((int) ((Map) user_revised.get("voting_reminder")).get("reminder_event_id"));
        user.setReminder_event_colour((int) ((Map) user_revised.get("voting_reminder")).get("reminder_event_colour"));
        user.setReminder_event_title((String) ((Map) user_revised.get("voting_reminder")).get("reminder_event_title"));

        // Voting attendance
        user.setAttendance_my_username((String) ((Map) user_revised.get("voting_attendance")).get("attendance_my_username"));
        user.setAttendance_event_id((int) ((Map) user_revised.get("voting_attendance")).get("attendance_event_id"));
        user.setAttendance_event_colour((int) ((Map) user_revised.get("voting_attendance")).get("attendance_event_colour"));
        user.setAttendance_event_title((String) ((Map) user_revised.get("voting_attendance")).get("attendance_event_title"));

        return user;
    }

    // This method retrieves user details upon registration.
    public Map<String, Object> asMap() {
        HashMap<String, Object> user = new HashMap<>();

        // User details
        HashMap<String, Object> user_details = new HashMap<>();
        user_details.put("image", image);
        user_details.put("email_address", email_address);
        user_details.put("username", username);
        user_details.put("encrypted_password", password);

        // Voting options
        HashMap<String, Object> voting_options = new HashMap<>();
        voting_options.put("option_my_username", option_my_username);
        voting_options.put("option_event_id", option_event_id);
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
        voting_selected.put("selected_event_id", selected_event_id);
        voting_selected.put("selected_event_colour", selected_event_colour);
        voting_selected.put("selected_event_title", selected_event_title);
        voting_selected.put("selected_event_location", selected_event_location);
        voting_selected.put("selected_event_notes", selected_event_notes);
        voting_selected.put("selected_start_date", selected_start_date);
        voting_selected.put("selected_end_date", selected_end_date);
        voting_selected.put("selected_start_time", selected_start_time);
        voting_selected.put("selected_end_time", selected_end_time);
        voting_selected.put("not_selected_start_date", not_selected_start_date);
        voting_selected.put("not_selected_end_date", not_selected_end_date);
        voting_selected.put("not_selected_start_time", not_selected_start_time);
        voting_selected.put("not_selected_end_time", not_selected_end_time);
        voting_selected.put("reject_reason", reject_reason);

        // Confirm date for event
        HashMap<String, Object> voting_confirmed = new HashMap<>();
        voting_confirmed.put("confirm_my_username", confirm_my_username);
        voting_confirmed.put("confirm_event_id", confirm_event_id);
        voting_confirmed.put("confirm_event_colour", confirm_event_colour);
        voting_confirmed.put("confirm_event_title", confirm_event_title);
        voting_confirmed.put("confirm_start_date", confirm_start_date);
        voting_confirmed.put("confirm_end_date", confirm_end_date);
        voting_confirmed.put("confirm_start_time", confirm_start_time);
        voting_confirmed.put("confirm_end_time", confirm_end_time);

        // Reminder for event
        HashMap<String, Object> voting_remind = new HashMap<>();
        voting_remind.put("reminder_my_username", reminder_my_username);
        voting_remind.put("reminder_event_id", reminder_event_id);
        voting_remind.put("reminder_event_colour", reminder_event_colour);
        voting_remind.put("reminder_event_title", reminder_event_title);

        // Confirm attendance
        HashMap<String, Object> voting_attendance = new HashMap<>();
        voting_attendance.put("attendance_my_username", attendance_my_username);
        voting_attendance.put("attendance_event_id", attendance_event_id);
        voting_attendance.put("attendance_event_colour", attendance_event_colour);
        voting_attendance.put("attendance_event_title", attendance_event_title);

        // User
        user.put("user_details", user_details);
        user.put("voting_options", voting_options);
        user.put("voting_selected", voting_selected);
        user.put("voting_confirmed", voting_confirmed);
        user.put("voting_reminder", voting_remind);
        user.put("voting_attendance", voting_attendance);

        return user;
    }
}