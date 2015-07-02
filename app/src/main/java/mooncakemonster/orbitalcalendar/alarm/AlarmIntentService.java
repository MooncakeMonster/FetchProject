package mooncakemonster.orbitalcalendar.alarm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

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

    private static String LOCK_NAME_STATIC = "mooncakemonster.orbitalcalendar.alarm.AlarmIntentService.STATIC";
    private static String LOCK_NAME_LOCAL = "mooncakemonster.orbitalcalendar.alarm.AlarmIntentService.LOCAL";

    private static PowerManager.WakeLock lockStatic=null;
    private PowerManager.WakeLock lockLocal=null;

    public AlarmIntentService()
    {
        super("AlarmIntentService");
    }

    /**
     * Acquire a partial static WakeLock, you need too call this within the class
     * that calls startService()
     * @param context
     */
    public static void acquireStaticLock(Context context) {
        getLock(context).acquire();
    }

    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (lockStatic==null) {
            PowerManager mgr=(PowerManager)context.getSystemService(Context.POWER_SERVICE);
            lockStatic=mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
            lockStatic.setReferenceCounted(true);
        }
        return(lockStatic);
    }

    @Override
    protected void onHandleIntent (Intent intent)
    {
        //Initialise and open database
        appointmentDatabase = new AppointmentController(this);
        appointmentDatabase.open();
        allAppointment = appointmentDatabase.getAllAppointment();
        appointmentDatabase.close();

        //Set all the alarms
        for(Appointment appt: allAppointment)
        {
            //Check if alarm is set
            if(appt.getRemind() != 0) {
                String appointmentLabel = appt.getEvent();
                String locationLabel = appt.getLocation();
                long millisecondReminder = appt.getRemind();

                AlarmSetter.setAlarm(this, appointmentLabel,locationLabel, millisecondReminder);
            }
        }

        //Release wakeful lock
        lockLocal.release();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager mgr=(PowerManager)getSystemService(Context.POWER_SERVICE);
        lockLocal=mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_LOCAL);
        lockLocal.setReferenceCounted(true);
    }

    @Override
    public void onStart(Intent intent, final int startId) {
        lockLocal.acquire();
        super.onStart(intent, startId);
        getLock(this).release();
    }


}
