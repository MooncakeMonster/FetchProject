package mooncakemonster.orbitalcalendar.menudrawer;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
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

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.calendar.CalendarFragment;
import mooncakemonster.orbitalcalendar.database.Constant;
import mooncakemonster.orbitalcalendar.event.EventFragment;
import mooncakemonster.orbitalcalendar.friendlist.FriendlistFragment;
import mooncakemonster.orbitalcalendar.notifications.NotificationFragment;
import mooncakemonster.orbitalcalendar.notifications.NotificationReceiveService;
import mooncakemonster.orbitalcalendar.votereceive.VotingFragment;

public class MenuDrawer extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = MenuDrawer.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menubar);

        //TODO: Place this in appropriate place - tentatively here for testing
        //startService(new Intent(this, NotificationReceiveService.class));
        Intent intent = new Intent(getApplicationContext(), NotificationReceiveService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), NotificationReceiveService.JOB_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
        am.cancel(pendingIntent);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), Constant.MIN_IN_MILLISECOND, pendingIntent);

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
                title = "Calendar";
                break;
            case 1:
                fragment = new EventFragment();
                title = "Events";
                break;
            case 2:
                fragment = new FriendlistFragment();
                title = "Friend List";
                break;
            case 3:
                fragment = new NotificationFragment();
                title = "Notifications";
                break;
            case 4:
                fragment = new VotingFragment();
                title = "Voting Results";
                break;
            default:
                break;
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

