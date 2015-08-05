package mooncakemonster.orbitalcalendar.menudrawer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.LoginActivity;
import mooncakemonster.orbitalcalendar.authentication.LoginManager;
import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.friendlist.FriendDatabase;
import mooncakemonster.orbitalcalendar.importexternals.ImportFacebookLogin;
import mooncakemonster.orbitalcalendar.importexternals.ImportICSParser;
import mooncakemonster.orbitalcalendar.profilepicture.CropImage;
import mooncakemonster.orbitalcalendar.profilepicture.RoundImage;

public class FragmentDrawer extends Fragment {

    private static String TAG = FragmentDrawer.class.getSimpleName();
    private static final int SELECTED_PICTURE = 1;

    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private View containerView;

    private RoundImage roundImage;
    private SimpleDraweeView userIcon;
    private TextView settings;
    private TextView nusTimetable;
    private TextView facebookImport;
    private TextView logoutButton;
    private TextView displayUsername;

    private static String[] titles = null;
    private static TypedArray icon = null;

    private FragmentDrawerListener drawerListener;

    private CloudantConnect cloudantConnect;
    private UserDatabase db;
    private FriendDatabase friendDatabase;
    private HashMap<String, String> user;
    private LoginManager session;

    public static final int ICS_IMPORT_REQUEST = 1;

    public FragmentDrawer() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public static List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();

        // preparing navigation drawer items
        for (int i = 0; i < titles.length; i++) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(titles[i]);
            navItem.setIcon(icon.getResourceId(i, -1));
            data.add(navItem);
        }
        return data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        titles = getActivity().getResources().getStringArray(R.array.nav_drawer_items);
        icon = getActivity().getResources().obtainTypedArray(R.array.nav_drawer_icons);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating view layout
        Fresco.initialize(getActivity());
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        db = new UserDatabase(getActivity().getApplicationContext());
        user = db.getUserDetails();

        if (cloudantConnect == null)
            this.cloudantConnect = new CloudantConnect(getActivity(), "user");
        final String my_username = user.get("username");
        cloudantConnect.startPullReplication();

        userIcon = (SimpleDraweeView) layout.findViewById(R.id.usericon);
        settings = (TextView) layout.findViewById(R.id.nav_settings);
        facebookImport = (TextView) layout.findViewById(R.id.importFacebook);
        nusTimetable = (TextView) layout.findViewById(R.id.importNUS);
        logoutButton = (TextView) layout.findViewById(R.id.nav_logout);

        try {
            roundImage = new RoundImage(cloudantConnect.retrieveUserImage(my_username));
            userIcon.setImageDrawable(roundImage);
        } catch (Exception e) {
            Log.e(TAG, "No profile image set yet");
            userIcon.setImageResource(R.drawable.profile);
        }

        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CropImage.class));
            }
        });

        // (1) Account settings
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(user.get("email"), my_username);
            }
        });

        // (2) Facebook importing of friends' birthday
        facebookImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                ImportFacebookLogin fragment = new ImportFacebookLogin();
                fragment.show(fragmentManager, "facebook_login_fragment");
            }
        });

        // (3) Importing NUS timetable
        nusTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get file path from file picker
                Intent intentFilePath = new Intent(Intent.ACTION_GET_CONTENT);
                intentFilePath.setType("*/*");
                startActivityForResult(intentFilePath, ICS_IMPORT_REQUEST);
            }
        });

        // (4) Logout button
        session = new LoginManager(getActivity());
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                dialogBuilder.setTitle("Logout");
                dialogBuilder.setMessage("Are you sure you want to logout from Fetch?");
                dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        session.setLogin(false);

                        // Remove user from sqlite in phone
                        db.deleteUsers();

                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        // Finish all previous activity and show login activity
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }).setNegativeButton("Cancel", null);

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });

        displayUsername = (TextView) layout.findViewById(R.id.displayusername);
        displayUsername.setText("Hello " + my_username + "!");

        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        adapter = new NavigationDrawerAdapter(getActivity(), getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position);
                mDrawerLayout.closeDrawer(containerView);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return layout;
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface FragmentDrawerListener {
        void onDrawerItemSelected(View view, int position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        // Terminate if result code indicate so
        if(resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        //If manages to get chosen file
        switch(requestCode){
            case ICS_IMPORT_REQUEST:
                Uri uri = data.getData();

                if (uri != null) {
                    Intent intent = new Intent(getActivity(), ImportICSParser.class);
                    intent.putExtra("fileChosen", uri);
                    startActivity(intent);
                    break;
                }
            default:
                break;
        }
    }

    // This method calls alert dialog to display the list of names.
    private void openDialog(final String email, final String username) {
        final View dialogview = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_account_settings, null);
        final TextView input_text = (TextView) dialogview.findViewById(R.id.input_text);
        final EditText input_email = (EditText) dialogview.findViewById(R.id.input_email);
        final EditText input_username = (EditText) dialogview.findViewById(R.id.input_username);

        input_text.setText("If you change your username, your friends will be notified.");
        input_email.setText(email);
        input_username.setText(username);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setView(dialogview);
        alertBuilder.setTitle("Account Settings");
        alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String new_email = input_email.getText().toString();
                String new_username = input_username.getText().toString();

                db = new UserDatabase(getActivity().getApplicationContext());
                user = db.getUserDetails();

                if (cloudantConnect == null) cloudantConnect = new CloudantConnect(getActivity(), "user");
                String my_username = user.get("username");

                friendDatabase = new FriendDatabase(getActivity());
                cloudantConnect.sendFriendUpdate(my_username, new_username, friendDatabase.getAllFriendUsernameString(friendDatabase));
                cloudantConnect.startPushReplication();

                // Update to phone
                db.updateUsers(new_email, my_username, new_username);

                dialog.dismiss();
            }
        }).setNegativeButton("Cancel", null);

        Dialog dialog = alertBuilder.create();
        dialog.show();
    }
}
