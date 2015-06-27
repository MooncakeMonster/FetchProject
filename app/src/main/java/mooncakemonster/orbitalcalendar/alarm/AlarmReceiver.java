package mooncakemonster.orbitalcalendar.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String SOMEACTION = "mooncakemonster.orbitalcalendar.alarm.ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        Time now = new Time();
        now.setToNow();
        //String time = FileHandler.timeFormat(now);

        String action = intent.getAction();
        if (SOMEACTION.equals(action)) {
            // here you call a service etc.
        }
    }
}