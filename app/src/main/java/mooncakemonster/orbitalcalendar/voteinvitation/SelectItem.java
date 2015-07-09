package mooncakemonster.orbitalcalendar.voteinvitation;

/**
 * Created by BAOJUN on 9/7/15.
 */
public class SelectItem {
    private boolean selected_date;
    private String event_start_date;
    private String event_end_date;
    private String event_start_time;
    private String event_end_time;

    public SelectItem(boolean selected_date, String event_start_date, String event_end_date, String event_start_time, String event_end_time) {
        this.selected_date = selected_date;
        this.event_start_date = event_start_date;
        this.event_end_date = event_end_date;
        this.event_start_time = event_start_time;
        this.event_end_time = event_end_time;
    }

    public boolean getSelected_date() { return selected_date; }

    public void setSelected_date(boolean selected_date) {
        this.selected_date = selected_date;
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
