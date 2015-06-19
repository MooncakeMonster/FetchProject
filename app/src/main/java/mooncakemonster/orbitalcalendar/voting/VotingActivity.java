package mooncakemonster.orbitalcalendar.voting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mooncakemonster.orbitalcalendar.R;

/**
 * This class allows users to send a list of date
 * and time options to participants for voting an event.
 */
public class VotingActivity extends ActionBarActivity {

    TextView vote_title, vote_location;
    EditText vote_participants;
    Button numberOfDays, numberOfHours, addOption;

    // Number picker
    final Context context = this;
    NumberPicker numberPicker;
    AlertDialog.Builder alertBw1, alertBw2;
    AlertDialog alertDw1, alertDw2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        getSupportActionBar().setElevation(0);

        vote_title = (TextView) findViewById(R.id.vote_title);
        vote_location= (TextView) findViewById(R.id.vote_location);

        vote_participants = (EditText) findViewById(R.id.vote_participants);
        numberOfDays = (Button) findViewById(R.id.vote_day);
        numberOfHours = (Button) findViewById(R.id.vote_duration);
        addOption = (Button) findViewById(R.id.add_option);

        // Get intent that started this activity
        Intent intent = getIntent();
        // Get bundle that stores data of this activity
        Bundle bundle = intent.getExtras();
        // Get data from bundle and set to relevant texts
        vote_title.setText(bundle.getString("event_title"));
        vote_location.setText(" @ " + bundle.getString("event_location"));

        numberOfDays.setText("1 day event");
        numberOfHours.setText("1 hour event");


        //TODO: This should be in adapter instead
        //numberOfDays.setText(bundle.getString("event_start_date"));
        //numberOfHours.setText(bundle.getString("event_start_time"));

        numberOfDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlert1();
                alertDw1.show();
            }
        });

        numberOfHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlert2();
                alertDw2.show();
            }
        });
    }


    // This method initialize number picker.
    private RelativeLayout setNumberPicker() {
        numberPicker = new NumberPicker(context);
        numberPicker.setClickable(false);
        numberPicker.setEnabled(true);
        numberPicker.setWrapSelectorWheel(true);

        final RelativeLayout linearLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams numPickerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        numPickerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        linearLayout.setLayoutParams(params);
        linearLayout.addView(numberPicker, numPickerParams);
        linearLayout.isClickable();

        return linearLayout;
    }


    // This method opens dialog for everyNum button.
    private void setAlert1() {
        RelativeLayout relativeLayout = setNumberPicker();
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(100);

        alertBw1 = new AlertDialog.Builder(context);
        alertBw1.setTitle("Select number");

        alertBw1.setView(relativeLayout).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Remove 's' from days, weeks, months and years when 1 is selected.
                //Conversely, append 's' to day, week, month, and year when value greater than 1 selected.
                if (numberPicker.getValue() == 1) {
                    numberOfDays.setText(Integer.toString(numberPicker.getValue()) + " day event");
                } else {
                    numberOfDays.setText(Integer.toString(numberPicker.getValue()) + " days event");
                }

                dialog.dismiss();
            }
        });

        alertDw1 = alertBw1.create();
    }

    // This method opens dialog for remindNum button.
    private void setAlert2() {
        RelativeLayout relativeLayout = setNumberPicker();
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(24);

        alertBw2 = new AlertDialog.Builder(context);
        alertBw2.setTitle("Select number");

        alertBw2.setView(relativeLayout).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Remove 's' from days, weeks, months and years when 1 is selected.
                //Conversely, append 's' to day, week, month, and year when value greater than 1 selected.
                if (numberPicker.getValue() == 1) {
                    numberOfHours.setText(Integer.toString(numberPicker.getValue()) + " hours event");
                } else {
                    numberOfHours.setText(Integer.toString(numberPicker.getValue()) + " hours event");
                }

                dialog.dismiss();
            }
        });

        alertDw2 = alertBw2.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cross, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
