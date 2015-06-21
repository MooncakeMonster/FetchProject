package mooncakemonster.orbitalcalendar.calendar;

import android.view.View;

import com.roomorama.caldroid.CaldroidListener;

import java.util.Date;

public abstract class CaldroidListenerModified extends CaldroidListener {

    public CaldroidListenerModified() {
        super();
    }

    public void onSelectDateTwice(Date date, View view) {

    }

}
