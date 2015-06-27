package mooncakemonster.orbitalcalendar.alarm;

import android.app.IntentService;
import android.content.Intent;

import java.util.List;

import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.AppointmentController;

/**
 * AlarmIntentService
 */
public class AlarmIntentService extends IntentService
{
    //AppointmentController variable to control the SQLite database
    private AppointmentController appointmentDatabase;
    //To get list of appointment
    private List<Appointment> allAppointment;

    public AlarmIntentService()
    {
        super("AlarmIntentService");
    }

    @Override
    protected void onHandleIntent (Intent intent)
    {
        //Initialise and open database
        appointmentDatabase = new AppointmentController(this);
        appointmentDatabase.open();
        allAppointment = appointmentDatabase.getAllAppointment();
        appointmentDatabase.close();
    }


}
