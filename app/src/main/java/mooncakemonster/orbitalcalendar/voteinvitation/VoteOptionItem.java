package mooncakemonster.orbitalcalendar.voteinvitation;

import java.io.Serializable;

/**
 * This class represents the voting options sent out to target participants to vote the dates they are available.
 */
public class VoteOptionItem implements Serializable {
    private String eventId;
    private String imageId;
    private String event_title;
    private String event_location;
    private String event_participants;
    private String event_voted_participants;
    private String event_start_date;
    private String event_end_date;
    private String event_start_time;
    private String event_end_time;
    private String event_confirm_start_date;
    private String event_confirm_end_date;
    private String event_confirm_start_time;
    private String event_confirm_end_time;

    public VoteOptionItem() { }

    public VoteOptionItem(String eventId, String imageId, String event_title, String event_location, String event_participants, String event_voted_participants,
                          String event_start_date, String event_end_date, String event_start_time, String event_end_time,
                          String event_confirm_start_date, String event_confirm_end_date, String event_confirm_start_time, String event_confirm_end_time) {
        this.eventId = eventId;
        this.imageId = imageId;
        this.event_title = event_title;
        this.event_location = event_location;
        this.event_participants = event_participants;
        this.event_voted_participants = event_voted_participants;
        this.event_start_date = event_start_date;
        this.event_end_date = event_end_date;
        this.event_start_time = event_start_time;
        this.event_end_time = event_end_time;
        this.event_confirm_start_date = event_confirm_start_date;
        this.event_confirm_end_date = event_confirm_end_date;
        this.event_confirm_start_time = event_confirm_start_time;
        this.event_confirm_end_time = event_confirm_end_time;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getEvent_title() {
        return event_title;
    }

    public void setEvent_title(String event_title) {
        this.event_title = event_title;
    }

    public String getEvent_location() {
        return event_location;
    }

    public void setEvent_location(String event_location) {
        this.event_location = event_location;
    }

    public String getEvent_participants() {
        return event_participants;
    }

    public void setEvent_participants(String event_participants) {
        this.event_participants = event_participants;
    }

    public String getEvent_voted_participants() {
        return event_voted_participants;
    }

    public void setEvent_voted_participants(String event_voted_participants) {
        this.event_voted_participants = event_voted_participants;
    }

    public String getEvent_start_date() {
        return event_start_date;
    }

    public void setEvent_start_date(String event_start_date) {
        this.event_start_date = event_start_date;
    }

    public String getEvent_end_date() {
        return event_end_date;
    }

    public void setEvent_end_date(String event_end_date) {
        this.event_end_date = event_end_date;
    }

    public String getEvent_start_time() {
        return event_start_time;
    }

    public void setEvent_start_time(String event_start_time) {
        this.event_start_time = event_start_time;
    }

    public String getEvent_end_time() {
        return event_end_time;
    }

    public void setEvent_end_time(String event_end_time) {
        this.event_end_time = event_end_time;
    }

    public String getEvent_confirm_start_date() {
        return event_confirm_start_date;
    }

    public void setEvent_confirm_start_date(String event_confirm_start_date) {
        this.event_confirm_start_date = event_confirm_start_date;
    }

    public String getEvent_confirm_end_date() {
        return event_confirm_end_date;
    }

    public void setEvent_confirm_end_date(String event_confirm_end_date) {
        this.event_confirm_end_date = event_confirm_end_date;
    }

    public String getEvent_confirm_start_time() {
        return event_confirm_start_time;
    }

    public void setEvent_confirm_start_time(String event_confirm_start_time) {
        this.event_confirm_start_time = event_confirm_start_time;
    }

    public String getEvent_confirm_end_time() {
        return event_confirm_end_time;
    }

    public void setEvent_confirm_end_time(String event_confirm_end_time) {
        this.event_confirm_end_time = event_confirm_end_time;
    }
}
