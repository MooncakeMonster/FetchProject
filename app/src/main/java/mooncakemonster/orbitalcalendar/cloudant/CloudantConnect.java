package mooncakemonster.orbitalcalendar.cloudant;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.cloudant.sync.datastore.ConflictException;
import com.cloudant.sync.datastore.Datastore;
import com.cloudant.sync.datastore.DatastoreManager;
import com.cloudant.sync.datastore.DatastoreNotCreatedException;
import com.cloudant.sync.datastore.DocumentBodyFactory;
import com.cloudant.sync.datastore.DocumentException;
import com.cloudant.sync.datastore.DocumentRevision;
import com.cloudant.sync.datastore.MutableDocumentRevision;
import com.cloudant.sync.notifications.ReplicationCompleted;
import com.cloudant.sync.notifications.ReplicationErrored;
import com.cloudant.sync.query.IndexManager;
import com.cloudant.sync.query.QueryResult;
import com.cloudant.sync.replication.PullReplication;
import com.cloudant.sync.replication.PushReplication;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorFactory;
import com.google.common.eventbus.Subscribe;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mooncakemonster.orbitalcalendar.authentication.RegisterActivity;
import mooncakemonster.orbitalcalendar.voteinvitation.VoteOptionItem;

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
    private IndexManager indexManager;

    private Replicator push_replicator;
    private Replicator pull_replicator;

    private Context context;
    private final Handler handler;
    private RegisterActivity register_listener;

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

        // Set up replicator objects
        try {
            this.reloadReplicationSettings();
        } catch (URISyntaxException e) {
            Log.e(TAG, "Unable to construct remote URI from configuration", e);
        }

        this.handler = new Handler(Looper.getMainLooper());

        Log.d(TAG, "CloudantConnect set up " + path.getAbsolutePath());
    }

    /****************************************************************************************************
     * (1) USER DOCUMENT
     ****************************************************************************************************/

    /**
     * Creates new document for user details database storage
     *
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
     * Save user details in user's phone
     *
     * @param username, password
     * @return user found in Cloudant
     */
    public User saveUserDetails(String username, String password) {
        int size_doc = this.datastore.getDocumentCount();

        List<BasicDocumentRevision> all_doc = this.datastore.getAllDocuments(0, size_doc, true);

        // Check through all email address in user datastore
        for (BasicDocumentRevision revision : all_doc) {
            User user = User.fromRevision(revision);
            Log.d(TAG, user.getUsername());
            Log.d(TAG, user.getPassword());

            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        // Reach here if no existing emails found
        return null;
    }

    /**
     * Updates document when user update their details
     *
     * @param user to retrieve the user details to be updated
     * @return document of new user details updated
     */
    public User updateUserDetailsDocument(User user) throws ConflictException {
        // Retrieve the original version from Cloudant document
        MutableDocumentRevision revision = user.getDocumentRevision().mutableCopy();
        revision.body = DocumentBodyFactory.create(user.asMap());

        try {
            BasicDocumentRevision updated = this.datastore.updateDocumentFromRevision(revision);
            return User.fromRevision(updated);
        } catch (DocumentException e) {
            return null;
        }
    }

    /**
     * Creates index for targeted document
     */
    private void createIndex() {
        indexManager = new IndexManager(datastore);
        if (indexManager.isTextSearchEnabled()) {
            String user_details = indexManager.ensureIndexed(Arrays.<Object>asList(
                            "user_details.email_address", "user_details.username", "user_details.password",
                            "voting_options.my_username", "voting_options.option_item",
                            "voting_selected.my_username", "voting_selected.selected_item"),
                            "user", "json");

            if (user_details == null) Log.e(TAG, "Unable to create user index");
            else Log.d(TAG, "Successfully created index" + user_details);
        }
    }

    /**
     * Checks through database for any exisiting username
     *
     * @param username to check duplicate when user registers
     * @return true if there is existing username, else false
     */
    public boolean authenticateUser(String username, String password) {
        Map<String, Object> query = new HashMap<>();
        Map<String, Object> search_username = new HashMap<>();
        Map<String, Object> search_password = new HashMap<>();

        search_username.put("user_details.username", username);
        search_password.put("user_details.encrypted_password", password);

        query.put("$and", Arrays.<Object>asList(search_username, search_password));

        createIndex();
        QueryResult result = indexManager.find(query);

        try {
            for (DocumentRevision revision : result) return true;
        } catch (Exception e) {
            Log.e(TAG, "No matching queries found");
        }

        // Reach here if no existing username found
        return false;
    }

    /**
     * Checks through database for any existing email address or username
     *
     * @param type to indicate if it's checking for email address or username
     * @return true if there is existing email, else false
     */
    public boolean checkExistingItems(String type, String line) {
        Map<String, Object> query = new HashMap<>();
        query.put("user_details." + type, line);

        createIndex();
        QueryResult result = indexManager.find(query);

        try {
            for (DocumentRevision revision : result) return true;
        } catch (Exception e) {
            Log.e(TAG, "No matching queries found");
        }

        // Reach here if no existing username found
        return false;
    }

    /****************************************************************************************************
     * (2) VOTING DOCUMENT
     ****************************************************************************************************/

    /**
     * This method sends the voting options to the target participants.
     */
    public void sendOptionsToTargetParticipants(String my_username, String participant_username, VoteOptionItem voteOptionItem) {
        // Get target participant's username
        User user = getTargetUser(participant_username);
        // Set options into target user's document
        user.setOption_my_username(my_username);
        user.setVoteOptionItem(voteOptionItem);

        // Retrieve user's documents
        try {
            // Update the latest targeted user's items back into Cloudant document
            updateUserDetailsDocument(user);
            Log.d(TAG, "Successfully updated target user's information");
        } catch (ConflictException e) {
            Log.e(TAG, "Unable to update target user's information");
        }
    }

    /**
     * Retrieve the target user's document from Cloudant
     *
     * @param username
     * @return user found in Cloudant
     */
    public User getTargetUser(String username) {
        Log.d(TAG, username);
        int size_doc = this.datastore.getDocumentCount();

        List<BasicDocumentRevision> all_doc = this.datastore.getAllDocuments(0, size_doc, true);

        // Check through all email address in user datastore
        for (BasicDocumentRevision revision : all_doc) {
            User user = User.fromRevision(revision);

            if (user != null && user.getUsername().equals(username)) {
                Log.d(TAG, "Successfully found user to vote");
                return user;
            }
        }
        // Reach here if no existing emails found
        Log.e(TAG, "Unable to find user to vote");
        return null;
    }

    /**
     * Check if there is any voting request
     *
     * @param username
     * @return true if there is voting request, else false
     */
    public boolean checkVotingRequest(String username) {
        User user = getTargetUser(username);
        if (user.getVoteOptionItem() != null) return true;
        return false;
    }

    /**
     * Check if there is any voting response
     *
     * @param username
     * @return true if there is voting request, else false
     */
    public boolean checkVotingResponse(String username) {
        User user = getTargetUser(username);
        if (user.getVoteSelectedItem() != null) return true;
        return false;
    }

    /**
     * Reset voting options once saved in user's phone
     *
     * @param username to reset the document of target username
     */
    public void resetVotingOptions(String username) {
        User user = getTargetUser(username);

        user.setOption_my_username("");
        user.getVoteOptionItem().setImageId(-1);
        user.getVoteOptionItem().setEvent_title("");
        user.getVoteOptionItem().setEvent_location("");
        user.getVoteOptionItem().setEvent_notes("");
        user.getVoteOptionItem().setEvent_start_date("");
        user.getVoteOptionItem().setEvent_end_date("");
        user.getVoteOptionItem().setEvent_start_time("");
        user.getVoteOptionItem().setEvent_end_time("");

        // Retrieve user's documents
        try {
            // Update the latest targeted user's items back into Cloudant document
            updateUserDetailsDocument(user);
            Log.d(TAG, "Successfully updated target user's information");
        } catch (ConflictException e) {
            Log.e(TAG, "Unable to update target user's information");
        }
    }

    /**
     * Reset voting response once saved in user's phone
     *
     * @param username to reset the document of target username
     */
    public void resetVotingResponse(String username) {
        User user = getTargetUser(username);

        user.setSelected_my_username("");
        user.getVoteSelectedItem().setImageId(-1);
        user.getVoteSelectedItem().setEvent_title("");
        user.getVoteSelectedItem().setEvent_start_date("");
        user.getVoteSelectedItem().setEvent_end_date("");
        user.getVoteSelectedItem().setEvent_start_time("");
        user.getVoteSelectedItem().setEvent_end_time("");

        // Retrieve user's documents
        try {
            // Update the latest targeted user's items back into Cloudant document
            updateUserDetailsDocument(user);
            Log.d(TAG, "Successfully updated target user's information");
        } catch (ConflictException e) {
            Log.e(TAG, "Unable to update target user's information");
        }
    }

    /****************************************************************************************************
     * (3) REPLICATION
     ****************************************************************************************************/

    /**
     * Sets replication listener
     */
    public void setReplicationListener(RegisterActivity listener) {
        this.register_listener = listener;
    }

    /**
     * Start push replication
     */
    public void startPushReplication() {
        if (this.push_replicator != null) {
            this.push_replicator.start();
        } else {
            throw new RuntimeException("Push replication not set up correctly");
        }
    }

    /**
     * Start pull replication
     */
    public void startPullReplication() {
        if (this.pull_replicator != null) {
            this.pull_replicator.start();
        } else {
            throw new RuntimeException("Pull replication not set up correctly");
        }
    }

    /**
     * Stop running replication
     */
    public void stopAllReplication() {
        if (this.push_replicator != null) {
            this.push_replicator.stop();
        }

        if (this.pull_replicator != null) {
            this.pull_replicator.stop();
        }
    }

    /**
     * Stop running replication and reloads replication settings
     * from the app's preferences.
     */
    public void reloadReplicationSettings() throws URISyntaxException {
        this.stopAllReplication();

        // Set up new replicator objects
        URI uri = this.createServerURI();

        // Push replication
        PushReplication push = new PushReplication();
        push.source = datastore;
        push.target = uri;
        push_replicator = ReplicatorFactory.oneway(push);
        push_replicator.getEventBus().register(this);

        // Pull replication
        PullReplication pull = new PullReplication();
        pull.source = uri;
        pull.target = datastore;
        pull_replicator = ReplicatorFactory.oneway(pull);
        pull_replicator.getEventBus().register(this);

        Log.d(TAG, "Set up replicators for URI:" + uri.toString());
    }

    /**
     * Calls when replication is completed
     */
    @Subscribe
    public void complete(ReplicationCompleted rc) {
        Log.d(TAG, "Replication completed");
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (register_listener != null) {
                    register_listener.replicationComplete();
                }
            }
        });
    }

    /**
     * Calls when replication has error
     */
    @Subscribe
    public void error(ReplicationErrored re) {
        Log.e(TAG, "Replication error:", re.errorInfo.getException());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (register_listener != null) {
                    register_listener.replicationError();
                }
            }
        });
    }

    /**
     * Retrieves URI for Cloudant database
     *
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
