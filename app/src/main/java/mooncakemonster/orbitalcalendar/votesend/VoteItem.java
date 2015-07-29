package mooncakemonster.orbitalcalendar.votesend;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Comparator;

import mooncakemonster.orbitalcalendar.database.Constant;

/**
 * This class represents the voting options sent out to target participants to vote the dates they are available.
 */
public class VoteItem implements Serializable {
    private String eventId;
    private String imageId;
    private String event_title;
    private String event_location;
    private String event_participants;
    private String event_voted_participants;
    private String event_attendance;
    private String event_start_date;
    private String event_end_date;
    private String event_start_time;
    private String event_end_time;
    private String event_confirmed;
    private String event_confirm_start_date;
    private String event_confirm_end_date;
    private String event_confirm_start_time;
    private String event_confirm_end_time;

    public VoteItem() { }

    public VoteItem(String eventId, String imageId, String event_title, String event_location, String event_participants, String event_voted_participants,
                    String event_attendance, String event_start_date, String event_end_date, String event_start_time, String event_end_time,
                    String event_confirmed, String event_confirm_start_date, String event_confirm_end_date, String event_confirm_start_time, String event_confirm_end_time) {
        this.eventId = eventId;
        this.imageId = imageId;
        this.event_title = event_title;
        this.event_location = event_location;
        this.event_participants = event_participants;
        this.event_voted_participants = event_voted_participants;
        this.event_attendance = event_attendance;
        this.event_start_date = event_start_date;
        this.event_end_date = event_end_date;
        this.event_start_time = event_start_time;
        this.event_end_time = event_end_time;
        this.event_confirmed = event_confirmed;
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

    public String getEvent_attendance() {
        return event_attendance;
    }

    public void setEvent_attendance(String event_attendance) {
        this.event_attendance = event_attendance;
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

    public String getEvent_confirmed() {
        return event_confirmed;
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

    // This method sort items according to event name.
    public static Comparator<VoteItem> eventNameComparator = new Comparator<VoteItem>() {
        @Override
        public int compare(VoteItem lhs, VoteItem rhs) {
            String this_title = lhs.getEvent_title();
            String that_title = rhs.getEvent_title();

            int this_size = this_title.length();
            int that_size = that_title.length();
            int size;

            if(this_size < that_size) size = this_size;
            else size = that_size;

            for(int i = 0; i < size; i++) {
                if (lhs.getEvent_title().charAt(i) < rhs.getEvent_title().charAt(i)) return -1;
                else if (lhs.getEvent_title().charAt(i) > rhs.getEvent_title().charAt(i)) return 1;
            }
            return 0;
        }
    };


    // This method sort items according to number of voted participants.
    public static Comparator<VoteItem> totalComparator = new Comparator<VoteItem>() {
        @Override
        public int compare(VoteItem lhs, VoteItem rhs) {
            if(lhs.getEvent_voted_participants() != null && rhs.getEvent_voted_participants() != null) {
                int this_total = lhs.getEvent_voted_participants().length();
                int that_total = rhs.getEvent_voted_participants().length();
                if (this_total <= that_total) return 1;
            } else if(lhs.getEvent_voted_participants() != null && rhs.getEvent_voted_participants() == null) {
                return -1;
            } else if(lhs.getEvent_voted_participants() == null && rhs.getEvent_voted_participants() != null) {
                return 1;
            }
            return 0;
        }
    };

    // This method sort items according to date.
    public static Comparator<VoteItem> dateComparator = new Comparator<VoteItem>() {
        @Override
        public int compare(VoteItem lhs, VoteItem rhs) {
            if(lhs.getEvent_confirmed().equals("true") && rhs.getEvent_confirmed().equals("true")) {
                long this_start_date = Constant.stringToMillisecond(lhs.getEvent_confirm_start_date(), lhs.getEvent_confirm_start_time(), new SimpleDateFormat("dd/MM/yyyy"), Constant.TIMEFORMATTER);
                long that_start_date = Constant.stringToMillisecond(rhs.getEvent_confirm_start_date(), rhs.getEvent_confirm_start_time(), new SimpleDateFormat("dd/MM/yyyy"), Constant.TIMEFORMATTER);
                if(this_start_date <= that_start_date) return 1;
            } else if(lhs.getEvent_confirmed().equals("true") && rhs.getEvent_confirmed().equals("false")) {
               return -1;
            } else if(lhs.getEvent_confirmed().equals("false") && rhs.getEvent_confirmed().equals("true")) {
                return 1;
            }
            return 0;
        }
    };


}
