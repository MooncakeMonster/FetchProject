package mooncakemonster.orbitalcalendar.voteinvitation;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import mooncakemonster.orbitalcalendar.R;

/**
 * Created by BAOJUN on 8/7/15.
 */
public class VoteInvitation extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_invitation);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
