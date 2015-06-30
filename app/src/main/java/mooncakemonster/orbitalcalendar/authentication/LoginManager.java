package mooncakemonster.orbitalcalendar.authentication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * This class checks if a user is already logged in.
 */
public class LoginManager {
    private static String TAG = LoginManager.class.getSimpleName();

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    private static final String PREF_NAME = "FetchUser";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    public LoginManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Set user is logged in
    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    // Check if user is logged in
    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(KEY_IS_LOGGEDIN, false);
    }
}
