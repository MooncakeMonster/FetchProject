package mooncakemonster.orbitalcalendar.voting;


public class VoteItem {

    private String event_start_date;
    private String event_start_time;

    public VoteItem(String event_start_date, String event_start_time) {
        this.event_start_date = event_start_date;
        this.event_start_time = event_start_time;
    }

    public String getEvent_start_date() {
        return event_start_date;
    }

    public void setEvent_start_date(String event_start_date) {
        this.event_start_date = event_start_date;
    }

    public String getEvent_start_time() {
        return event_start_time;
    }

    public void setEvent_start_time(String event_start_time) {
        this.event_start_time = event_start_time;
    }
}
