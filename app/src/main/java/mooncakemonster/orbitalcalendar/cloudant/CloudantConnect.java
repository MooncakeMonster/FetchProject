package mooncakemonster.orbitalcalendar.cloudant;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.cloudant.sync.datastore.ConflictException;
import com.cloudant.sync.datastore.Datastore;
import com.cloudant.sync.datastore.DatastoreManager;
import com.cloudant.sync.datastore.DatastoreNotCreatedException;
import com.cloudant.sync.datastore.DocumentBodyFactory;
import com.cloudant.sync.datastore.DocumentException;
import com.cloudant.sync.datastore.MutableDocumentRevision;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import mooncakemonster.orbitalcalendar.authentication.User;

/**
 * This class establish connection with Cloudant database and allows replication.
 */
public class CloudantConnect {

    private static final String TAG = CloudantConnect.class.getSimpleName();
    private static final String DATASTORE_DIRECTORY = "data";

    // Register user database
    static final String CLOUDANT_USER = "pref_key_username";
    static final String CLOUDANT_DB = "pref_key_dbname";
    static final String CLOUDANT_API_KEY = "pref_key_api_key";
    static final String CLOUDANT_API_SECRET = "pref_key_api_password";

    private Datastore datastore;

    private Context context;

    public CloudantConnect(Context context, String datastore_name) {
        this.context = context;

        // Set up information within its own folder in the application
        File path = this.context.getApplicationContext().getDir(DATASTORE_DIRECTORY, Context.MODE_PRIVATE);
        DatastoreManager manager = new DatastoreManager(path.getAbsolutePath());

        try {
            this.datastore = manager.openDatastore(datastore_name);
        } catch (DatastoreNotCreatedException e) {
            Log.e(TAG, "Unable to open Datastore", e);
        }

        // Reach here if datastore successfully created
        Log.d(TAG, "Successfully set up database at" + path.getAbsolutePath());
    }

    /****************************************************************************************************
     * (1) USER DOCUMENT
     ****************************************************************************************************/

    /**
     * Creates new document for user details database storage
     * @param user to store user details into database
     * @return document of user details stored
     */
    public User createNewUserDocument(User user) {
        MutableDocumentRevision revision = new MutableDocumentRevision();
        revision.body = DocumentBodyFactory.create(user.asMap());

        try {
            BasicDocumentRevision created = this.datastore.createDocumentFromRevision(revision);
            return User.fromRevision(created);
        } catch (DocumentException e) {
            return null;
        }
    }


    /**
     * Updates document when user update their details
     * @param user to retrieve the user details to be updated
     * @return document of new user details updated
     */
    public User updateUserDetailsDocument(User user) throws ConflictException {
        // Retrieve the original version from Cloudant document
        MutableDocumentRevision revision = user.getDocumentRevision().mutableCopy();
        revision.body = DocumentBodyFactory.create(user.asMap());

        try {
            BasicDocumentRevision updated = this.datastore.createDocumentFromRevision(revision);
            return User.fromRevision(updated);
        } catch (DocumentException e) {
            return null;
        }
    }

    /**
     * Checks through database for any exisiting username
     * @param username to check duplicate when user registers
     * @return true if there is existing username, else false
     */
    public boolean authenticateUser(String username, String password) {
        int size_doc = this.datastore.getDocumentCount();

        List<BasicDocumentRevision> all_doc = this.datastore.getAllDocuments(0, size_doc, true);

        // Check through all username in user datastore
        for(BasicDocumentRevision revision : all_doc) {
            User user = User.fromRevision(revision);
            if(user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        // Reach here if no existing username found
        return false;
    }

    /**
     * Checks through database for any exisiting email address
     * @param email_address to check duplicate when user registers
     * @return true if there is existing email, else false
     */
    public boolean checkExistingEmail(String email_address) {
        int size_doc = this.datastore.getDocumentCount();

        List<BasicDocumentRevision> all_doc = this.datastore.getAllDocuments(0, size_doc, true);

        // Check through all email address in user datastore
        for(BasicDocumentRevision revision : all_doc) {
            User user = User.fromRevision(revision);
            if(user.getEmail_address().equals(email_address)) {
                return true;
            }
        }
        // Reach here if no existing emails found
        return false;
    }

    /**
     * Checks through database for any exisiting username
     * @param username to check duplicate when user registers
     * @return true if there is existing username, else false
     */
    public boolean checkExistingUsername(String username) {
        int size_doc = this.datastore.getDocumentCount();

        List<BasicDocumentRevision> all_doc = this.datastore.getAllDocuments(0, size_doc, true);

        // Check through all username in user datastore
        for(BasicDocumentRevision revision : all_doc) {
            User user = User.fromRevision(revision);
            if(user.getUsername().equals(username)) {
                return true;
            }
        }
        // Reach here if no existing username found
        return false;
    }


    /****************************************************************************************************
     * (2) VOTING DOCUMENT
     ****************************************************************************************************/

    /**
     * Retrieves URI for Cloudant database
     * @return URI
     */
    public URI createServerURI() throws URISyntaxException {
        // TODO: Find ways to retrieve cloudant info securely.

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        String cloudant_username = sharedPreferences.getString(CLOUDANT_USER, "");
        String cloudant_dbName = sharedPreferences.getString(CLOUDANT_DB, "");
        String cloudant_api_key = sharedPreferences.getString(CLOUDANT_API_KEY, "");
        String cloudant_api_password = sharedPreferences.getString(CLOUDANT_API_SECRET, "");
        String host = cloudant_username + ".cloudant.com";

        return new URI("https", cloudant_api_key + ":" + cloudant_api_password, host, 443, "/" + cloudant_dbName, null, null);
    }
}
