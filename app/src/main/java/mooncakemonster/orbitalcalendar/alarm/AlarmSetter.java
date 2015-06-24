package mooncakemonster.orbitalcalendar.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Map;

/*
 * Receiver for resetting alarms whenever phone has been shut down.
 * Adopted from http://stackoverflow.com/questions/7845660/how-to-run-a-service-every-day-at-noon-and-on-every-boot
 */
public class AlarmSetter extends BroadcastReceiver {

    private static final String PREF_FILENAME = "appointmentAlarm";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get preference
        SharedPreferences alarmSchedule = context.getSharedPreferences(PREF_FILENAME, 0);
        Map<String, ?> scheduleData = alarmSchedule.getAll();

        // Set the schedule time
        if(scheduleData.containsKey("fromHour") && scheduleData.containsKey("toHour")) {
            int fromHour = (Integer) scheduleData.get("fromHour");
            int fromMinute = (Integer) scheduleData.get("fromMinute");

            int toHour = (Integer) scheduleData.get("toHour");
            int toMinute = (Integer) scheduleData.get("toMinute");

            //Do some action
        }
    }

}
