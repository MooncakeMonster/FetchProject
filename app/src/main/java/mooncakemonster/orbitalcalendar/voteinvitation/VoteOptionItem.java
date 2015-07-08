package mooncakemonster.orbitalcalendar.voteinvitation;

/**
 * This class represents the voting options sent out to target participants to vote the dates they are available.
 */
public class VoteOptionItem {
    private int imageId;
    private String event_title;
    private String event_location;
    private String event_notes;
    private String event_start_date;
    private String event_end_date;
    private String event_start_time;
    private String event_end_time;

    public VoteOptionItem(int imageId, String event_title, String event_location, String event_notes,
                          String event_start_date, String event_end_date, String event_start_time, String event_end_time) {
        this.imageId = imageId;
        this.event_title = event_title;
        this.event_location = event_location;
        this.event_notes = event_notes;
        this.event_start_date = event_start_date;
        this.event_end_date = event_end_date;
        this.event_start_time = event_start_time;
        this.event_end_time = event_end_time;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
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

    public String getEvent_notes() {
        return event_notes;
    }

    public void setEvent_notes(String event_notes) {
        this.event_notes = event_notes;
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
}
