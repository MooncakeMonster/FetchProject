package mooncakemonster.orbitalcalendar.cloudant;

import android.util.Log;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;

/**
 * This class establish connection with cloudant.
 */
public class CloudantConnect {

    public CloudantClient connect() {
        String password = System.getProperty("cloudant_password");
        return new CloudantClient("account", "username", password);
    }

    // This method retrieves database from Cloudant
    public Database getDatabase(String databaseName) {
        Database database = connect().database(databaseName, true);
        Log.d("Database exist", "" + database.getDBUri());
        return database;
    }
}
