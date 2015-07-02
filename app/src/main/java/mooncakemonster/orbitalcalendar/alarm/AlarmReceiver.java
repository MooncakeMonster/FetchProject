package mooncakemonster.orbitalcalendar.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import mooncakemonster.orbitalcalendar.R;

public class AlarmReceiver extends BroadcastReceiver {

    final static String TAG = "AlarmReceiver";
    public static final String SOMEACTION = "mooncakemonster.orbitalcalendar.alarm.ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "Intent received!");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Get Event name from intent
        String appointmentLabel = intent.getStringExtra("appointmentName");
        String locationLabel = intent.getStringExtra("locationName");
        if(locationLabel == null)
        {
            locationLabel = " - ";
        }
        //Set notification design: Set the icon, scrolling text and timestamp
        Notification notification  = new Notification.Builder(context)
                .setContentTitle("Fetch: " + appointmentLabel)
                .setContentText("Location: " + locationLabel)
                .setSmallIcon(R.drawable.bearicon)
                .setVibrate(new long[]{1000, 1000})
                .setAutoCancel(true).getNotification();

        notificationManager.notify(0, notification);
    }
}