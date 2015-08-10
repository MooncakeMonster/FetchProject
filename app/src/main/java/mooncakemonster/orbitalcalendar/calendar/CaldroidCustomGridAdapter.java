package mooncakemonster.orbitalcalendar.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.HashMap;
import java.util.List;

import hirondelle.date4j.DateTime;
import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.AppointmentController;
import mooncakemonster.orbitalcalendar.database.Constant;

/**
 * Created by BAOJUN on 2/8/15.
 */
public class CaldroidCustomGridAdapter extends CaldroidGridAdapter {

    AppointmentController appointmentController;
    List<Appointment> allAppointments;

    public CaldroidCustomGridAdapter(Context context, int month, int year,
                                     HashMap<String, Object> caldroidData,
                                     HashMap<String, Object> extraData) {
        super(context, month, year, caldroidData, extraData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cellView = convertView;
        Fresco.initialize(context);

        // For reuse
        if (convertView == null) {
            cellView = inflater.inflate(R.layout.custom_cell, null);
        }

        int topPadding = cellView.getPaddingTop();
        int leftPadding = cellView.getPaddingLeft();
        int bottomPadding = cellView.getPaddingBottom();
        int rightPadding = cellView.getPaddingRight();

        TextView tv1 = (TextView) cellView.findViewById(R.id.tv1);
        SimpleDraweeView ballred = (SimpleDraweeView) cellView.findViewById(R.id.ball1);
        SimpleDraweeView ballyellow = (SimpleDraweeView) cellView.findViewById(R.id.ball2);
        SimpleDraweeView ballgreen = (SimpleDraweeView) cellView.findViewById(R.id.ball3);
        SimpleDraweeView ballblue = (SimpleDraweeView) cellView.findViewById(R.id.ball4);
        SimpleDraweeView ballpurple = (SimpleDraweeView) cellView.findViewById(R.id.ball5);

        tv1.setTextColor(Color.BLACK);
        // Get dateTime of this cell
        DateTime dateTime = this.datetimeList.get(position);
        Resources resources = context.getResources();

        // Set color of the dates in previous / next month
        if (dateTime.getMonth() != month) {
            tv1.setTextColor(resources.getColor(com.caldroid.R.color.caldroid_darker_gray));
        }

        boolean shouldResetDiabledView = false;
        boolean shouldResetSelectedView = false;

        // Customize for disabled dates and date outside min/max dates
        if ((minDateTime != null && dateTime.lt(minDateTime))
                || (maxDateTime != null && dateTime.gt(maxDateTime))
                || (disableDates != null && disableDates.indexOf(dateTime) != -1)) {

            tv1.setTextColor(CaldroidFragment.disabledTextColor);
            if (CaldroidFragment.disabledBackgroundDrawable == -1) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.disable_cell);
            } else {
                cellView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
            }

            if (dateTime.equals(getToday())) {
                cellView.setBackgroundResource(R.drawable.border_calendar);
            }

        } else {
            shouldResetDiabledView = true;
        }

        // (2) Customize for selected dates
        // Check if the current date is in database
        appointmentController = new AppointmentController(context);
        appointmentController.open();
        String date = dateTime.format("YYYY-MM-DD");
        allAppointments = appointmentController.getTargetEvent(date);
        int[] colour_array = new int[5];

        // Only retrieve the first 5 colours
        for(int i = 0; i < 5; i++) {
            try {
                Appointment appointment = allAppointments.get(i);
                if (appointment != null) {
                    colour_array[i] = allAppointments.get(i).getColour();
                    Log.d("HEY COLOUR", "" + allAppointments.get(i).getColour());
                }
            } catch (IndexOutOfBoundsException e) {
                Log.e("CustomAdapter", "No event dates found");
                break;
            }
        }

        // Set colour into imageview
        if(allAppointments.size() > 0) {
            if (allAppointments.size() == 1) {
                ballgreen.setBackgroundResource(Constant.getCircleColour(colour_array[0]));
            } else if (allAppointments.size() == 2) {
                ballyellow.setBackgroundResource(Constant.getCircleColour(colour_array[0]));
                ballgreen.setBackgroundResource(Constant.getCircleColour(colour_array[1]));
            } else if (allAppointments.size() == 3) {
                ballyellow.setBackgroundResource(Constant.getCircleColour(colour_array[0]));
                ballgreen.setBackgroundResource(Constant.getCircleColour(colour_array[1]));
                ballblue.setBackgroundResource(Constant.getCircleColour(colour_array[2]));
            } else if (allAppointments.size() == 4) {
                ballred.setBackgroundResource(Constant.getCircleColour(colour_array[0]));
                ballyellow.setBackgroundResource(Constant.getCircleColour(colour_array[1]));
                ballgreen.setBackgroundResource(Constant.getCircleColour(colour_array[2]));
                ballblue.setBackgroundResource(Constant.getCircleColour(colour_array[3]));
            } else if (allAppointments.size() >= 5) {
                ballred.setBackgroundResource(Constant.getCircleColour(colour_array[0]));
                ballyellow.setBackgroundResource(Constant.getCircleColour(colour_array[1]));
                ballgreen.setBackgroundResource(Constant.getCircleColour(colour_array[2]));
                ballblue.setBackgroundResource(Constant.getCircleColour(colour_array[3]));
                ballpurple.setBackgroundResource(Constant.getCircleColour(colour_array[4]));
            }
        }

        if (selectedDates != null && selectedDates.indexOf(dateTime) == -1) {
            shouldResetSelectedView = true;
        }

        // (3) Customize for today
        if (shouldResetDiabledView && shouldResetSelectedView) {
            if (dateTime.equals(getToday())) {
                cellView.setBackgroundResource(R.drawable.border_calendar);
            } else {
                cellView.setBackgroundResource(R.color.caldroid_transparent);
            }
        }

        tv1.setText("" + dateTime.getDay());

        // Recover padding to prevent collapse after setting background resource
        cellView.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);

        // Set custom color if required
        setCustomResources(dateTime, cellView, tv1);

        return cellView;
    }

}
