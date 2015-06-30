package mooncakemonster.orbitalcalendar.cloudant;

import android.util.Log;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;

import mooncakemonster.orbitalcalendar.authentication.User;

/**
 * This class stores user details in Cloudant database.
 */
public class CloudantStorage {
    Database database;

    public CloudantStorage() {
        database = new CloudantConnect().getDatabase("database_name");
    }

    // This method stores user details in Cloudant; if successful, return true, else false.
    // TODO: Ensure user details are successfully saved
    public boolean storeUserDetails(String email_address, String username, String password) {
        User user = new User();
        user.setEmail_address(email_address);
        user.setUsername(username);
        user.setPassword(password);

        Response response = database.post(user);
        Log.d("User created", "id: " + response.getId() + ", rev: " + response.getRev());

        if(response != null) return true;
        return false;
    }
}
