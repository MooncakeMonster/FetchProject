package mooncakemonster.orbitalcalendar.cloudant;

/**
 * This class searches for requested information (email address, username).
 */
public class CloudantQuery {
    /*
    Database database;

    public CloudantQuery() {
        database = new CloudantConnect().getDatabase("database_name");
        //TODO

        Log.d("Index created", "Index");
    }

    // This method searches for existing email found in Cloudant
    public boolean findExistingEmail(String email_address) {
        // Finds email from document
        User user = database.find(User.class, "{\"email\" :" + "\"" + email_address + "\"" + "}");

        // Return true if email already exist
        if(user != null) return true;
        return false;
    }

    // This method searches for existing username found in Cloudant
    public boolean findExistingUsername(String username) {
        User user = database.find(User.class, "{\"username\" :" + "\"" + username + "\"" + "}");

        // Return true if username already exist
        if(user != null) return true;
        return false;
    }

    // This method searches for existing user found in Cloudant
    public boolean authenticateUser(String username, String password) {
        // TODO

        // Return true if username and password provided by user is found in database
        //if(user != null) return true;
        return false;
    }
    */
}
