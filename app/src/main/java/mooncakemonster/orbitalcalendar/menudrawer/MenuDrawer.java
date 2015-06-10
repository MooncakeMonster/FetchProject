package mooncakemonster.orbitalcalendar.menudrawer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.roomorama.caldroid.CaldroidFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.calendar.CalendarFragment;
import mooncakemonster.orbitalcalendar.event.EventFragment;
import mooncakemonster.orbitalcalendar.notifications.NotificationFragment;
import mooncakemonster.orbitalcalendar.timetable.TimetableFragment;
import mooncakemonster.orbitalcalendar.voting.VotingFragment;

public class MenuDrawer extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = MenuDrawer.class.getSimpleName();
    private Calendar cal = Calendar.getInstance();
    private CaldroidFragment caldroidFragment = new CaldroidFragment();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM yyyy");

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menubar);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch
        displayView(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (position) {
            case 0:
                fragment = new CalendarFragment();
                // get current month and year
                title = dateFormatter.format(cal.getTime());
                break;
            case 1:
                fragment = new NotificationFragment();
                title = "Notifications";
                break;
            case 2:
                fragment = new EventFragment();
                title = "Events";
                break;
            case 3:
                fragment = new TimetableFragment();
                title = "Timetable";
                break;
            case 4:
                fragment = new VotingFragment();
                title = "Voting Results";
                break;
            default: break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }
}

