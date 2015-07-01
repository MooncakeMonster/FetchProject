package mooncakemonster.orbitalcalendar.cloudant;

import android.content.Context;

import com.cloudant.sync.datastore.DatastoreManager;

import java.io.File;

/**
 * Interface for Cloudant
 */
public class CloudantController {

    //Make the manager a singleton within an application
    private static DatastoreManager manager;

    public CloudantController(Context context){
        if(manager == null)
        {
            File path = context.getApplicationContext().getDir("datastores", Context.MODE_PRIVATE);
            manager = new DatastoreManager(path.getAbsolutePath());
        }
    }

}
