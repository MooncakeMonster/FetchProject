package mooncakemonster.orbitalcalendar.notifications;

import java.io.Serializable;

/**
 * Created by BAOJUN on 7/7/15.
 */
public class NotificationItem implements Serializable {
    private String sender_username;
    private String message;
    private String sender_event;
    private String action;
    private String sender_location;
    private String sender_notes;
    private String selected_date;
    private String start_date;
    private String end_date;
    private String start_time;
    private String end_time;
    private String intent;

    public NotificationItem(String sender_username, String message, String sender_event, String action,
                            String sender_location, String sender_notes, String selected_date,
                            String start_date, String end_date, String start_time,
                            String end_time, String intent) {
        this.sender_username = sender_username;
        this.message = message;
        this.sender_event = sender_event;
        this.action = action;
        this.sender_location = sender_location;
        this.sender_notes = sender_notes;
        this.selected_date = selected_date;
        this.start_date = start_date;
        this.end_date = end_date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.intent = intent;
    }

    public String getSender_username() {
        return sender_username;
    }

    public void setSender_username(String sender_username) {
        this.sender_username = sender_username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender_event() {
        return sender_event;
    }

    public void setSender_event(String sender_event) {
        this.sender_event = sender_event;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSender_location() {
        return sender_location;
    }

    public void setSender_location(String sender_location) {
        this.sender_location = sender_location;
    }

    public String getSender_notes() {
        return sender_notes;
    }

    public void setSender_notes(String sender_notes) {
        this.sender_notes = sender_notes;
    }

    public String getSelected_date() {
        return selected_date;
    }

    public void setSelected_date(String selected_date) {
        this.selected_date = selected_date;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }
}
