package mooncakemonster.orbitalcalendar.importexternals;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.AppointmentController;

/**
 * Created by MunKeat_2 on 2/8/2015.
 */
public class ImportFacebook extends ListActivity {

    //List to store all imported appointments
    private ArrayList<ImportedAppointment> listOfInput = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_import_external_list);
        Bundle extras = getIntent().getExtras();

        Button import_external_events = (Button) findViewById(R.id.add_imported);

        if (extras != null) {
            //Assuming user got here from ImportExternalFragment.java
            //String filePath = extras.getString("filePath");
            listOfInput = (ArrayList<ImportedAppointment>) extras.getSerializable("FacebookImported");
        }

        if(listOfInput == null || listOfInput.size() == 0) {
            import_external_events.setVisibility(View.INVISIBLE);
        }

        ArrayAdapter adapter = new ImportExternalAdapter(this, R.layout.row_import_external, listOfInput);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ImportedAppointment appt = (ImportedAppointment) l.getItemAtPosition(position);
        appt.toggle();
        ((BaseAdapter)l.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {

        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v){
        switch(v.getId())
        {
            case R.id.add_imported:
                //Iterate through the list and add in database, any flagged as add_imported
                //Open database
                AppointmentController appointmentDatabase = new AppointmentController(this);
                appointmentDatabase.open();
                //Insert in database
                for(ImportedAppointment appt: listOfInput) {
                    if(appt.isToImport()) {
                        appointmentDatabase.createAppointment(appt);
                    }
                }
                //Close database
                finish();
                break;
        }
    }
}
