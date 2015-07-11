package mooncakemonster.orbitalcalendar.votereceive;

/**
 * Created by BAOJUN on 10/7/15.
 */
public class ResultItem {
    private String event_id;
    private String start_date;
    private String end_date;
    private String start_time;
    private String end_time;
    private String all_username;
    private String selected_username;
    private String not_selected_username;
    private String username_rejected;
    private String total;

    public ResultItem(String event_id, String start_date, String end_date, String start_time, String end_time, String all_username,
                      String selected_username, String not_selected_username, String username_rejected, String total) {
        this.event_id = event_id;
        this.start_date = start_date;
        this.end_date = end_date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.all_username = all_username;
        this.selected_username = selected_username;
        this.not_selected_username = not_selected_username;
        this.username_rejected = username_rejected;
        this.total = total;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
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

    public String getAll_username() {
        return all_username;
    }

    public void setAll_username(String all_username) {
        this.all_username = all_username;
    }

    public void setSelected_username(String selected_username) {
        this.selected_username = selected_username;
    }

    public String getSelected_username() {
        return selected_username;
    }

    public void setNot_selected_username(String not_selected_username) {
        this.not_selected_username = not_selected_username;
    }

    public String getNot_selected_username() {
        return not_selected_username;
    }

    public String getUsername_rejected() {
        return username_rejected;
    }

    public void setUsername_rejected(String username_rejected) {
        this.username_rejected = username_rejected;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
