package mooncakemonster.orbitalcalendar.ImportExternal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.AppointmentController;
import mooncakemonster.orbitalcalendar.database.Constant;

/**
 * Interface for login to Facebook
 */
public class ImportFacebookLogin extends DialogFragment {

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private static String dateWithTime = "yyyy-MM-dd'T'HH:mm:ss";
    private static String dateWithoutTime = "yyyy-MM-dd";

    private static SimpleDateFormat dateWithTimeFormatter = new SimpleDateFormat(dateWithTime);
    private static SimpleDateFormat dateWithoutTimeFormatter = new SimpleDateFormat(dateWithoutTime);

    //AppointmentController variable to control the SQLite database
    private AppointmentController appointmentDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getActivity());

        callbackManager = CallbackManager.Factory.create();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_importfacebooklogin, container, false);

        loginButton = (LoginButton) view.findViewById(R.id.facebook_login_button);
        //Set permission at users_events
        loginButton.setReadPermissions("user_events");
        // If using in a fragment
        loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                // Get Facebook Events
                importFacebookEvents();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        return view;
    }

    //Private helper class
    private void importFacebookEvents() {
        //Bundle parameters = new Bundle();
        //parameters.putString("user_events", "id,description,is_date_only,start_time,end_time,updated_time");

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/events", null, HttpMethod.GET, new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                // handle the result
                JSONObject jObject = response.getJSONObject();
                JSONArray eventsList = null;
                List<Appointment> appointmentList = new ArrayList<Appointment>();

                try {
                    eventsList = jObject.getJSONArray("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (eventsList == null) {
                    return;
                }

                //Convert JSONArray to list of appointmentList
                for (int limit = eventsList.length(), count = 0; count < limit ; count++) {
                    try {
                        JSONObject JSONtempAppt = eventsList.getJSONObject(count);

                        Appointment appt = new Appointment();


                        //Process each JSONObject
                        String event = JSONtempAppt.getString("name");
                        String startTime = JSONtempAppt.getString("start_time");
                        long startTimeMillisec;
                        String startProperDate;

                        if(startTime.contains("+")) {
                            startTime = startTime.replaceFirst("[+]([0-9]{4})", "");
                            startTimeMillisec = Constant.stringToMillisecond(startTime, dateWithTimeFormatter);
                            startProperDate = Constant.standardYearMonthDate(startTime, dateWithTimeFormatter, Constant.YYYYMMDD_FORMATTER);
                        } else {
                            startTimeMillisec = Constant.stringToMillisecond(startTime, dateWithoutTimeFormatter);
                            startProperDate = Constant.standardYearMonthDate(startTime, dateWithoutTimeFormatter, Constant.YYYYMMDD_FORMATTER);
                        }

                        String endTime = null;
                        long endTimeMillisec = startTimeMillisec;

                        if(JSONtempAppt.has("end_time")) {
                            endTime = JSONtempAppt.getString("end_time");

                            if(endTime.contains("T")) {
                                endTime = endTime.replaceFirst("[+]([0-9]{4})", "");
                                endTimeMillisec = Constant.stringToMillisecond(endTime, dateWithTimeFormatter);
                            } else {
                                endTimeMillisec = Constant.stringToMillisecond(endTime, dateWithoutTimeFormatter);
                            }
                        }

                        Appointment tempAppt = new Appointment();
                        tempAppt.setEvent(event);
                        tempAppt.setStartDate(startTimeMillisec);
                        tempAppt.setStartProperDate(startProperDate);
                        tempAppt.setEndDate(endTimeMillisec);

                        appointmentList.add(tempAppt);

                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }

                Collections.sort(appointmentList);

                //TODO: Create a dialog box, verifying events that user wants to import
                //TODO: Ensure "update" does not replicate the same event again

                //Insert into Appointment
                appointmentDatabase = new AppointmentController(getActivity());
                appointmentDatabase.open();

                for(Appointment appointment : appointmentList)
                {
                    appointmentDatabase.createAppointment(appointment);
                }

                appointmentDatabase.close();
                appointmentDatabase = null;

            }
        }
        ).executeAsync();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
