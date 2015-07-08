package mooncakemonster.orbitalcalendar.notifications;

/**
 * Created by BAOJUN on 7/7/15.
 */
public class NotificationItem {
    private String sender_username;
    private String message;
    private String sender_event;
    private String action;
    private String intent;

    public NotificationItem(String sender_username, String message, String sender_event, String action, String intent) {
        this.sender_username = sender_username;
        this.message = message;
        this.sender_event = sender_event;
        this.action = action;
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

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }
}
