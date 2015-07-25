package mooncakemonster.orbitalcalendar.profilepicture;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.authentication.UserDatabase;
import mooncakemonster.orbitalcalendar.cloudant.CloudantConnect;
import mooncakemonster.orbitalcalendar.database.Constant;

/**
 * Created by BAOJUN on 21/7/15.
 */
public class CropImage extends ActionBarActivity {

    private static final int ROTATE_LEFT_NINETY_DEGREES = -90;
    private static final int ROTATE_RIGHT_NINETY_DEGREES = 90;
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 20;

    private CropImageView cropImageView;
    private Uri imageUri;
    private Button load_image, rotate_left, rotate_right;
    private CloudantConnect cloudantConnect;
    private UserDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        getSupportActionBar().setElevation(0);

        db = new UserDatabase(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        String my_username = user.get("username");

        if (cloudantConnect == null)
            this.cloudantConnect = new CloudantConnect(this, "user");

        cropImageView = (CropImageView) findViewById(R.id.CropImageView);

        try {
            cropImageView.setImageBitmap(cloudantConnect.retrieveUserImage(my_username));
        } catch (Exception e) {
            cropImageView.setImageResource(R.drawable.profile);
        }

        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);
        // Ensure cropped image is a square
        cropImageView.setFixedAspectRatio(true);

        load_image = (Button) findViewById(R.id.button_load);
        rotate_left = (Button) findViewById(R.id.button_rotate_left);
        rotate_right = (Button) findViewById(R.id.button_rotate_right);

        load_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });

        rotate_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(ROTATE_LEFT_NINETY_DEGREES);
            }
        });

        rotate_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(ROTATE_RIGHT_NINETY_DEGREES);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            imageUri = getPickImageResultUri(data);
            cropImageView.setImageUri(imageUri);
        }
    }

    // Create a chooser intent to select the  source to get image from.
    public Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // Collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the  list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select Image");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    /**
     * Get URI to image received from capture  by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    /**
     * Get the URI of the selected image
     * @return the correct URI for camera  and gallery image.
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
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

            if (cloudantConnect == null)
                this.cloudantConnect = new CloudantConnect(this, "user");

            db = new UserDatabase(getApplicationContext());
            HashMap<String, String> user = db.getUserDetails();
            final String my_username = user.get("username");

            Bitmap bitmap = cropImageView.getCroppedImage(500, 500);

            Uri uri = Constant.getImageUri(getApplicationContext(), bitmap);
            String filename = Constant.getFileName(this, uri);
            cloudantConnect.setImageFilename(my_username, filename);
            cloudantConnect.updateUserImage(new File(Constant.getRealPathFromURI(getApplicationContext(), uri)), cloudantConnect.getTargetUser(my_username));
            cloudantConnect.startPushReplication();

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
