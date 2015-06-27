package mooncakemonster.orbitalcalendar.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
 * Receiver for resetting alarms whenever phone has been shut down.
 * Adopted from http://stackoverflow.com/questions/7845660/how-to-run-a-service-every-day-at-noon-and-on-every-boot
 */
public class AlarmSetter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Start Service
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, AlarmIntentService.class);
            context.startService(serviceIntent);
        }
    }

    /****************************************************************************************************
     * SET ALARM - After boot up of Android Phone
     ****************************************************************************************************/
    public static void setAlarm(Context context, long millisecond)
    {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.SOMEACTION);

        int uniqueID = (int)((millisecond >> 32) ^ millisecond);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, uniqueID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP, millisecond, pendingIntent);
    }

    public static void cancelAlarm(Context context, long millisecond)
    {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.SOMEACTION);

        int uniqueID = (int)((millisecond >> 32) ^ millisecond);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, uniqueID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
    }

    /*
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, MySystemService.class);
            context.startService(serviceIntent);
        }
    }
     */


}
