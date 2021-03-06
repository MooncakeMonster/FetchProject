package mooncakemonster.orbitalcalendar.database;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import mooncakemonster.orbitalcalendar.R;
import mooncakemonster.orbitalcalendar.voteresult.AttendanceAdapter;
import mooncakemonster.orbitalcalendar.voteresult.ResultOption;

/*************************************************************************************************
 * Purpose: Abstract Constant.java class to store all helper method that will most likely be used across the app
 * as well as all the constants.
 *
 * Constant.java contains the following:
 * (1) Constants
 * (2) Conversion Methods
 * (3) Validity Check
 * (4) Widget Interactivity
 * (5) Bitmap (Image) Management
 * (6) Developer's Tool (Tentatively, at the moment, for Facebook)
 * (7) Event colour
 *
 * N.B. Do not instantiate, or extend this class
 *
 * Access Via: Not Applicable
 **************************************************************************************************/

public final class Constant
{
    private Constant() {
        //Prevent Constant.java from being instantiated
    }

    /****************************************************************************************************
     * (1) CONSTANTS
     ****************************************************************************************************/
    public static final long SECOND_IN_MILLISECOND = 1000L;
    public static final long MIN_IN_MILLISECOND = 60000L;
    public static final long HOUR_IN_MILLISECOND = 3600000L;
    public static final long DAY_IN_MILLISECOND = 86400000L;
    public static final long YESTERDAY_IN_MILLISECOND = 172800000L;
    public static final long WEEK_IN_MILLISECOND = 604800000L;
    public static final long CENTURY_IN_MILLISECOND = 3155692600000L;

    public static final int EVENT_TITLE_MAX_LENGTH = 50;
    public static final int LOCATION_MAX_LENGTH = 50;
    public static final int NOTES_MAX_LENGTH = 200;

    public static SimpleDateFormat NOTIFICATION_DATEFORMATTER = new SimpleDateFormat("dd/MM/yyyy, hh:mma");
    public static SimpleDateFormat NOTIFICATION_WEEK_DATEFORMATTER = new SimpleDateFormat("EEEE, hh:mma");

    public static SimpleDateFormat DATEFORMATTER = new SimpleDateFormat("dd/MM/yyyy, EEE");
    public static SimpleDateFormat TIMEFORMATTER = new SimpleDateFormat("hh:mma");
    public static SimpleDateFormat YYYYMMDD_FORMATTER = new SimpleDateFormat("yyyy MM dd");

    private static Calendar calendar = Calendar.getInstance();

    /****************************************************************************************************
     * (2) CONVERSION METHODS
     ****************************************************************************************************/

    /****************************************************************************************************
     * Change strings (date and time) to corresponding millisecond
     ****************************************************************************************************/
    public static long stringToMillisecond(String date, String time, SimpleDateFormat formatterForDate, SimpleDateFormat formatterForTime) {
        try {
            Date tempTime = formatterForTime.parse(time);
            Date tempDate = formatterForDate.parse(date);

            long timeMillisecond = (tempTime.getHours() * HOUR_IN_MILLISECOND) + (tempTime.getMinutes() * MIN_IN_MILLISECOND);
            long dateMillisecond = tempDate.getTime();

            return (dateMillisecond + timeMillisecond);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Should not reach here
        return -1;
    }

    public static long stringToMillisecond(String dateTime, SimpleDateFormat formatterForDateTime) {
        try {
            Date tempDateTime = formatterForDateTime.parse(dateTime);
            long dateTimeMillisecond = tempDateTime.getTime();
            return (dateTimeMillisecond);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Should not reach here
        return -1;
    }

    /****************************************************************************************************
     * Convert millisecond back to strings
     ****************************************************************************************************/
    public static String getDate(long milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        calendar.clear();
        calendar.setTimeInMillis(milliSeconds);

        return formatter.format(calendar.getTime());
    }

    public static String getDate(long milliSeconds, SimpleDateFormat formatter) {
        calendar.clear();
        calendar.setTimeInMillis(milliSeconds);

        return formatter.format(calendar.getTime());
    }

    /****************************************************************************************************
     * Convert any string date to standardised string date format: YYYY-MM-DD
     ****************************************************************************************************/
    public static String standardYearMonthDate(String date, SimpleDateFormat formatter, SimpleDateFormat standardFormat) {
        try {
            Date tempDate = formatter.parse(date);
            return standardFormat.format(tempDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Should not reach here
        return "";
    }

    /****************************************************************************************************
     * Convert string date to date object
     ****************************************************************************************************/
    public static Date stringToDate(String date, SimpleDateFormat standardFormat) {
        try {
            return standardFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Should not reach here
        return new Date();
    }

    public static long retrieveCurrentTime() {
        Calendar current = Calendar.getInstance();
        long offset = current.get(Calendar.ZONE_OFFSET) +
                current.get(Calendar.DST_OFFSET);

        Log.d("Constant", "" + (current.getTimeInMillis() + offset) % (24 * 60 * 60 * 1000));
        return (current.getTimeInMillis() + offset) % (24 * 60 * 60 * 1000);
    }

    /****************************************************************************************************
     * (3) VALIDITY CHECK
     ****************************************************************************************************/

    /****************************************************************************************************
     * Ensure string extracted from an EditText widget is less than a certain length.
     * Otherwise, warn user through a custom error message, or a default error message.
     ****************************************************************************************************/
    public static boolean maxStringLength(String input, int length, EditText edittext, String errorMessage) {
        if (length <= 0) {
            return false;
        }
        else if (input.length() > length) {
            if (errorMessage == null && input.length() > length) {
                edittext.setError("" + length + " characters or less.");
            } else {
                edittext.setError(errorMessage);
            }
            return false;
        }

        return true;
    }

    /****************************************************************************************************
     * Ensure string extracted from an EditText widget is more than a certain length.
     * Otherwise, warn user through a custom error message, or a default error message.
     ****************************************************************************************************/
    public static boolean minStringLength(String input, int length, EditText edittext, String errorMessage) {
        if (length <= 0) {
            return false;
        } else if ((input.length() == 0 || input == null)) {
            edittext.setError("Please do not leave this empty.");
            return false;
        } else if (input.length() < length) {
            if (errorMessage == null) {
                edittext.setError("Please key in at least " + length + " characters.");
            } else {
                edittext.setError(errorMessage);
            }
            return false;
        }

        return true;
    }

    /****************************************************************************************************
     * (4) WIDGET INTERACTIVITY
     ****************************************************************************************************/

    /****************************************************************************************************
     * Set Button as DatePicker
     ****************************************************************************************************/
    public static String setButtonDatePicker(final Context context, final Button button, final long timeInMillisecond, final String additionalString) {
        if (timeInMillisecond > 0) {
            calendar.setTimeInMillis(timeInMillisecond);
        } else {
            calendar.setTimeInMillis(System.currentTimeMillis());
        }

        final String dateFormat = DATEFORMATTER.format(calendar.getTime());
        button.setText(additionalString + dateFormat);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog date = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        button.setText(additionalString + DATEFORMATTER.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                date.show();
            }
        });

        return button.getText().toString();
    }

    /****************************************************************************************************
     * Set Button as TimePicker
     ****************************************************************************************************/
    public static String setButtonTimePicker(final Context context, final Button button, final long timeInMillisecond, final String additionalString) {
        if (timeInMillisecond > 0) {
            calendar.setTimeInMillis(timeInMillisecond);
        } else {
            calendar.setTimeInMillis(System.currentTimeMillis());
        }

        final String timeFormat = TIMEFORMATTER.format(calendar.getTime());
        button.setText(additionalString + timeFormat);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog time = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        button.setText(additionalString + TIMEFORMATTER.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                time.show();
            }
        });

        return button.getText().toString();
    }

    /****************************************************************************************************
     * Append/Remove 's' to type (e.g. min, hour, day, etc) when appropriate quantity is selected
     ****************************************************************************************************/
    public static void setNumberDialogWheel(final Context context, final Button quantityCount, final Button typeWheel, final CharSequence[] wheel, final String additionalTag) {
        quantityCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NumberPicker numberPicker = new NumberPicker(context);
                RelativeLayout relativeLayout = setNumberPicker(context, numberPicker);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Select number");

                builder.setView(relativeLayout).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        quantityCount.setText(Integer.toString(numberPicker.getValue()));
                        String reminderOption = typeWheel.getText().toString();
                        //Remove 's' from when 1 is selected. Conversely, append 's' when value greater than 1 selected.
                        if (quantityCount.getText().toString().equals("1")) {
                            for (CharSequence remind : wheel) {
                                if (reminderOption.equals(remind + "s" + additionalTag)) {
                                    typeWheel.setText(remind + additionalTag);
                                    break;
                                }
                            }
                        } else if (!quantityCount.getText().toString().equals("1")) {
                            for (CharSequence remind : wheel) {
                                if (reminderOption.equals(remind + additionalTag)) {
                                    typeWheel.setText(remind + "s" + additionalTag);
                                    break;
                                }
                            }
                        }

                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    // Helper Method: Initialize number picker.
    private static RelativeLayout setNumberPicker(Context context, NumberPicker numberPicker) {
        numberPicker.setClickable(false);
        numberPicker.setEnabled(true);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(100);

        final RelativeLayout linearLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams numPickerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        numPickerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        linearLayout.setLayoutParams(params);
        linearLayout.addView(numberPicker, numPickerParams);
        linearLayout.isClickable();

        return linearLayout;
    }

    /****************************************************************************************************
     * Launch dialog with appropriate type (e.g. min, versus mins; hour, versus hours... etc.)
     ****************************************************************************************************/
    public static void setTypeDialog(final Context context, final Button quantityCount, final Button typeWheel, final CharSequence[] wheel, final String additionalTag) {
        typeWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder build = new AlertDialog.Builder(context);
                build.setTitle("Select type");
                build.setSingleChoiceItems(wheel, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!quantityCount.getText().toString().equals("1"))
                            typeWheel.setText(wheel[which] + "s" + additionalTag);
                        else typeWheel.setText(wheel[which] + additionalTag);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = build.create();
                alertDialog.show();
            }
        });
    }

    /****************************************************************************************************
     * Launch alert dialog
     ****************************************************************************************************/

    // This method calls alert dialog to inform users a message.
    public static void alertUser(Context context, String title, String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("Ok", null);
        //dialogBuilder.show();

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    // This method calls alert dialog to display the list of names that had confirmed their attendance.
    public static void openAttendanceDialog(Context context, String[] split_participants) {
        final View dialogview = LayoutInflater.from(context).inflate(R.layout.dialog_result, null);
        final View header = LayoutInflater.from(context).inflate(R.layout.header_result, null);
        final TextView input_username = (TextView) header.findViewById(R.id.result_notice);
        final ListView listView = (ListView) dialogview.findViewById(R.id.result_list);
        listView.addHeaderView(header);
        final List<ResultOption> list = new ArrayList<>();

        input_username.setText("The following participants will be coming to your event:");

        final int size = split_participants.length;
        for(int i = 0; i < size; i++) {
            list.add(i, new ResultOption(true, split_participants[i]));
        }

        final AttendanceAdapter adapter = new AttendanceAdapter(context, R.id.result_list, list);
        listView.setAdapter(adapter);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle("Attendance");
        alertBuilder.setView(dialogview);
        alertBuilder.setCancelable(true).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog dialog = alertBuilder.create();
        dialog.show();
    }

    /****************************************************************************************************
     * (5) BITMAP (IMAGE) MANAGEMENT
     ****************************************************************************************************/

    /****************************************************************************************************
     * Calculate factor N (2^N) to decrease original image by
     ****************************************************************************************************/
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /****************************************************************************************************
     * (6) DEVELOPER'S TOOL
     ****************************************************************************************************/

    /****************************************************************************************************
     * Get Development Key Hashes for Facebook
     ****************************************************************************************************/
    public static String printKeyHash(Activity context) {
        String key = null;

        try {
            //Getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();
            //Retriving package info
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        Log.i("Key Value = ", key);
        return key;
    }

    /****************************************************************************************************
     * (7) COLOUR ICON
     ****************************************************************************************************/

    public static int getBearColour(int chosenColour) {
        switch (chosenColour) {
            case R.color.redbear: return R.drawable.beared;
            case R.color.yellowbear: return R.drawable.bearyellow;
            case R.color.greenbear: return R.drawable.beargreen;
            case R.color.bluebear: return R.drawable.bearblue;
            case R.color.purplebear: return R.drawable.bearpurple;
        }

        // Should not reach here
        return -1;
    }

    public static int getCircleColour(int chosenColour) {
        switch (chosenColour) {
            case R.color.redbear: return R.drawable.border_red;
            case R.color.yellowbear: return R.drawable.border_yellow;
            case R.color.greenbear: return R.drawable.border_green;
            case R.color.bluebear: return R.drawable.border_blue;
            case R.color.purplebear: return R.drawable.border_purple;
        }

        return R.drawable.ballpurple;
    }

    public static int getPartyBearColour(int chosenColour) {
        switch (chosenColour) {
            case R.color.redbear: return R.drawable.partyred;
            case R.color.yellowbear: return R.drawable.partyyellow;
            case R.color.greenbear: return R.drawable.partygreen;
            case R.color.bluebear: return R.drawable.partyblue;
            case R.color.purplebear: return R.drawable.partypurple;
        }

        return R.drawable.ballpurple;
    }

    public static int getRandomColour() {
        Random random = new Random();
        int chosenColour = random.nextInt(4);

        switch (chosenColour) {
            case 0: return R.color.redbear;
            case 1: return R.color.yellowbear;
            case 2: return R.color.greenbear;
            case 3: return R.color.bluebear;
            case 4: return R.color.purplebear;
        }

        // Should not reach here
        return R.color.yellowbear;
    }
}
