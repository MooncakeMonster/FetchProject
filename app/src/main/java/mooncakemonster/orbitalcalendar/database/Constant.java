package mooncakemonster.orbitalcalendar.database;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*************************************************************************************************
 * Purpose: Abstract Constant.java class to store all helper method that will most likely be used across the app
 * as well as all the constants.
 *
 * Constant.java contains the following:
 * (1) Constants
 * (2) Conversion Methods
 * (3) Validity Check
 * (4) Widget Interactivity
 * (5) Developer's Tool (Tentatively, at the moment, for Facebook)
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
    public static final long MIN_IN_MILLISECOND = 60000L;
    public static final long HOUR_IN_MILLISECOND = 3600000L;
    public static final long DAY_IN_MILLISECOND = 86400000L;
    public static final long WEEK_IN_MILLISECOND = 604800000L;

    public static final int EVENT_TITLE_MAX_LENGTH = 50;
    public static final int LOCATION_MAX_LENGTH = 50;
    public static final int NOTES_MAX_LENGTH = 200;

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
    public static void setButtonDatePicker(final Context context, final Button button, final long timeInMillisecond, final String additionalString) {
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
    }

    /****************************************************************************************************
     * Set Button as TimePicker
     ****************************************************************************************************/
    public static void setButtonTimePicker(final Context context, final Button button, final long timeInMillisecond, final String additionalString) {
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
                }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
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
     * (5) DEVELOPER'S TOOL
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
}
