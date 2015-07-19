package mooncakemonster.orbitalcalendar.voteresult;

/**
 * Created by BAOJUN on 19/7/15.
 */
public class ResultOption {
    private boolean checked;
    private String username;

    public ResultOption(boolean checked, String username) {
        this.checked = checked;
        this.username = username;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
