package mooncakemonster.orbitalcalendar.importexternals;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.database.Appointment;
import mooncakemonster.orbitalcalendar.database.AppointmentController;
import mooncakemonster.orbitalcalendar.database.Constant;

/**
 * Interface for login to Facebook
 */
public class ImportFacebookLogin extends DialogFragment {

    private LoginButton loginButton;
    private TextView input_fb_text;
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
        View view = inflater.inflate(R.layout.dialog_facebook, container, false);

        getDialog().setTitle("Import Facebook Events");

        input_fb_text = (TextView) view.findViewById(R.id.input_fb_text);
        input_fb_text.setText("Please login to your Facebook account to import your Facebook events into Fetch.");

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
                ArrayList<ImportedAppointment> appointmentList = new ArrayList<ImportedAppointment>();

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

                        ImportedAppointment tempAppt = new ImportedAppointment();
                        tempAppt.setColour(Constant.getRandomColour());
                        tempAppt.setEvent(event);
                        tempAppt.setStartDate(startTimeMillisec);
                        tempAppt.setStartProperDate(startProperDate);
                        tempAppt.setEndDate(endTimeMillisec);
                        tempAppt.setToImport();

                        appointmentList.add(tempAppt);

                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }

                Collections.sort(appointmentList);

                Bundle bundle = new Bundle();
                bundle.putSerializable("FacebookImported", appointmentList);
                Intent intent = new Intent(getActivity(), ImportFacebook.class);
                intent.putExtras(bundle);

                startActivity(intent);

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
