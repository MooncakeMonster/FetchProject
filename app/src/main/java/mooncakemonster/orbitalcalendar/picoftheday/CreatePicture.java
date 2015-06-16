package mooncakemonster.orbitalcalendar.picoftheday;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import mooncakemonster.orbitalcalendar.R;

/**
 * This method allows users to save image in the application.
 */
public class CreatePicture extends ActionBarActivity implements View.OnClickListener{

    private Calendar cal = Calendar.getInstance();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy, EEE @ hh:mm a");

    EditText pic_title, pic_caption;
    ImageView picture;
    Button smiley1, smiley2, smiley3, smiley4, smiley5, selected_smiley;

    // To store in database
    int smiley_id = R.drawable.smile1; // Set default smiley as first smiley if not chosen
    String title, date, caption;
    String uriPicture;    // Save uri in string format to store image as text format in database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        // Removes shadow under action bar
        getSupportActionBar().setElevation(0);

        pic_title = (EditText) findViewById(R.id.picture_title);
        pic_caption = (EditText) findViewById(R.id.picture_caption);

        picture = (ImageView) findViewById(R.id.imagebutton);
        smiley1 = (Button) findViewById(R.id.button1);
        smiley2 = (Button) findViewById(R.id.button2);
        smiley3 = (Button) findViewById(R.id.button3);
        smiley4 = (Button) findViewById(R.id.button4);
        smiley5 = (Button) findViewById(R.id.button5);
        selected_smiley = (Button) findViewById(R.id.select_smiley);

        picture.setOnClickListener(this);
        smiley1.setOnClickListener(this);
        smiley2.setOnClickListener(this);
        smiley3.setOnClickListener(this);
        smiley4.setOnClickListener(this);
        smiley5.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            title = pic_title.getText().toString();
            date = dateFormatter.format(cal.getTime());
            caption = pic_caption.getText().toString();

            // Do not save data
            if(title.isEmpty()) {
                alertUser("Upload failed!", "Please enter title.");
            }

            else if(caption.isEmpty()) {
                alertUser("Upload failed!", "Please enter caption.");
            }

            else if(uriPicture.isEmpty()) {
                alertUser("Upload failed!", "Please upload an image.");
            }

            // Save data when title, caption and image are not empty
            else {
                // Add information into database
                TableDatabase tableDatabase = new TableDatabase(this);
                tableDatabase.putInformation(tableDatabase, smiley_id, title, date, caption, uriPicture);

                Toast.makeText(getBaseContext(), "Details successfully saved", Toast.LENGTH_LONG).show();
                finish();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            // Show the image picked by user
            case R.id.imagebutton:
                picture.setImageDrawable(null);
                Crop.pickImage(this);
                break;

            // Saves the user's smiley choice
            case R.id.button1:
                selected_smiley.setBackgroundResource(R.drawable.smile1);
                selected_smiley.setText("");
                setSmileyID(R.drawable.smile1);
                break;
            case R.id.button2:
                selected_smiley.setBackgroundResource(R.drawable.smile2);
                selected_smiley.setText("");
                setSmileyID(R.drawable.smile2);
                break;
            case R.id.button3:
                selected_smiley.setBackgroundResource(R.drawable.smile3);
                selected_smiley.setText("");
                setSmileyID(R.drawable.smile3);
                break;
            case R.id.button4:
                selected_smiley.setBackgroundResource(R.drawable.smile4);
                selected_smiley.setText("");
                setSmileyID(R.drawable.smile4);
                break;
            case R.id.button5:
                selected_smiley.setBackgroundResource(R.drawable.smile5);
                selected_smiley.setText("");
                setSmileyID(R.drawable.smile5);
                break;

            default:
                break;
        }
    }

    // This method sets the smiley ID according to what the user picks.
    private void setSmileyID(int smileyID) {
        this.smiley_id = smileyID;
    }

    // This method calls alert dialog to inform users a message.
    private void alertUser(String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CreatePicture.this);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if(requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    // This method allows users to crop image in square.
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    // This method ensures there are no errors in cropping.
    private void handleCrop(int resultCode, Intent result) {
        if(resultCode == RESULT_OK) {
            picture.setImageURI(Crop.getOutput(result));
            uriPicture = Crop.getOutput(result).toString();
        } else if(resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
