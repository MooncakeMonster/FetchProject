package mooncakemonster.orbitalcalendar.event;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.AppointmentController;

/*
 * Purpose: EventFragment.java shows the user all the appointment created in the form of a list
 * Access via: Menu tab > Events
 */

public class EventFragment extends ListFragment{

    //Set database to allow user to retrieve data to populate EventFragment.java
    private AppointmentController appointmentDatabase;
    //List to get all the appointments
    private List<Appointment> allAppointment;
    EventAdapter adapter;
    Appointment selected_appointment;

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Get all the appointment
        appointmentDatabase = new AppointmentController(getActivity());
        appointmentDatabase.open();
        allAppointment = appointmentDatabase.getAllAppointment();
        //Initialise ArrayAdapter adapter for view
        adapter = new EventAdapter(getActivity(), R.layout.row_event, allAppointment);
        setListAdapter(adapter);

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id)
            {
                //Get Appointment from ArrayAdapter
                final Appointment appointmentToDelete = adapter.getItem(position);

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Delete appointment");
                alert.setMessage("Are you sure you want to delete \"" + appointmentToDelete.toString() + "\"?");

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //Delete from SQLite database
                        appointmentDatabase.deleteAppointment(appointmentToDelete);
                        //Delete from ArrayAdapter & allAppointment
                        adapter.remove(appointmentToDelete);
                        allAppointment.remove(appointmentToDelete);
                        adapter.notifyDataSetChanged();
                        //Remove dialog after execution of the above
                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                    }
                });
                alert.show();
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listfragment, container, false);
    }

    //TODO: on selection of event
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        Appointment selectedAppt = adapter.getItem(position);
        selected_appointment = adapter.getItem(position);
        //Instantiate EventView.java for viewing of appointment (and editing)
        DialogFragment dialogfragment = EventView.newInstance(selectedAppt);
        dialogfragment.show(getFragmentManager(), null);
        //Update ArrayAdapter
        allAppointment = appointmentDatabase.getAllAppointment();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        if(appointmentDatabase != null)
        {
            appointmentDatabase.close();
            appointmentDatabase = null;
        }
    }

    // This method retrieves selected appointment for voting
    public Appointment getEventPostition() {
        return selected_appointment;
    }

}
