package mooncakemonster.orbitalcalendar.calendar;

/**
 * Created by BAOJUN on 31/5/15.
 */
public class EventClass {

    private int event_resource;
    private String event_name;
    private String event_time;

    public EventClass(int event_resource, String event_name, String event_time) {
        super();
        this.setEvent_resource(event_resource);
        this.setEvent_name(event_name);
        this.setEvent_time(event_time);
    }

    public int getEvent_Resource() {
        return event_resource;
    }

    public void setEvent_resource(int event_resource) {
        this.event_resource = event_resource;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

}