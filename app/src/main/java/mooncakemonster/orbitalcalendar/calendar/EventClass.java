package mooncakemonster.orbitalcalendar.calendar;

public class EventClass {

    private int event_resource;
    private String event_title;
    private String event_time;

    public EventClass(int event_resource, String event_title, String event_time) {
        super();
        this.setEvent_resource(event_resource);
        this.setEvent_title(event_title);
        this.setEvent_time(event_time);
    }

    public int getEvent_Resource() {
        return event_resource;
    }

    public void setEvent_resource(int event_resource) {
        this.event_resource = event_resource;
    }

    public String getEvent_title() {
        return event_title;
    }

    public void setEvent_title(String event_name) {
        this.event_title = event_name;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

}
