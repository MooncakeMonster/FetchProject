package mooncakemonster.orbitalcalendar.voteconfirmdate;

/**
 * Created by BAOJUN on 11/7/15.
 */
public class ConfirmParticipants {
    private boolean send;
    private String username;

    public ConfirmParticipants(boolean send, String username) {
        this.send = send;
        this.username = username;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public boolean getSend() {
        return send;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
