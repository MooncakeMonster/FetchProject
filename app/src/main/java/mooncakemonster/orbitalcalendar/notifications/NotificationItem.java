package mooncakemonster.orbitalcalendar.notifications;

import java.io.Serializable;

/**
 * Created by BAOJUN on 7/7/15.
 */
public class NotificationItem implements Serializable {
    private String action_done;
    private String row_id;
    private int notificationId;
    private long timestamp;
    private int eventId;
    private int imageId;
    private String sender_username;
    private String message;
    private String sender_event;
    private String action;
    private String sender_location;
    private String sender_notes;
    private String start_date;
    private String end_date;
    private String start_time;
    private String end_time;
    private String reject_reason;

    public NotificationItem(String action_done, String row_id, int notificationId, int timestamp, int eventId, int imageId, String sender_username, String message, String sender_event, String action,
                            String sender_location, String sender_notes, String start_date, String end_date, String start_time, String end_time, String reject_reason) {
        this.action_done = action_done;
        this.row_id = row_id;
        this.notificationId = notificationId;
        this.timestamp = timestamp;
        this.eventId = eventId;
        this.imageId = imageId;
        this.sender_username = sender_username;
        this.message = message;
        this.sender_event = sender_event;
        this.action = action;
        this.sender_location = sender_location;
        this.sender_notes = sender_notes;
        this.start_date = start_date;
        this.end_date = end_date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.reject_reason = reject_reason;
    }

    public String getAction_done() {
        return action_done;
    }

    public void setAction_done(String action_done) {
        this.action_done = action_done;
    }

    public String getRow_id() {
        return row_id;
    }

    public void setRow_id(String row_id) {
        this.row_id = row_id;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
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

    public String getReject_reason() {
        return reject_reason;
    }

    public void setReject_reason(String reject_reason) {
        this.reject_reason = reject_reason;
    }
}
