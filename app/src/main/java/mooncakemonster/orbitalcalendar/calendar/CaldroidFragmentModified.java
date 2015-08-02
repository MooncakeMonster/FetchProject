package mooncakemonster.orbitalcalendar.calendar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.antonyt.infiniteviewpager.InfinitePagerAdapter;
import com.antonyt.infiniteviewpager.InfiniteViewPager;
import com.caldroid.R.id;
import com.caldroid.R.layout;
import com.caldroid.R.style;
import com.roomorama.caldroid.CalendarHelper;
import com.roomorama.caldroid.DateGridFragment;
import com.roomorama.caldroid.MonthPagerAdapter;
import com.roomorama.caldroid.WeekdayArrayAdapter;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;
import hirondelle.date4j.DateTime.DayOverflow;

@SuppressLint({"DefaultLocale"})
public class CaldroidFragmentModified extends DialogFragment {
    public String TAG = "CaldroidFragmentModified";
    public static int SUNDAY = 1;
    public static int MONDAY = 2;
    public static int TUESDAY = 3;
    public static int WEDNESDAY = 4;
    public static int THURSDAY = 5;
    public static int FRIDAY = 6;
    public static int SATURDAY = 7;
    private static final int MONTH_YEAR_FLAG = 52;
    private Time firstMonthTime = new Time();
    private final StringBuilder monthYearStringBuilder = new StringBuilder(50);
    private Formatter monthYearFormatter;
    public static final int NUMBER_OF_PAGES = 4;
    public static int disabledBackgroundDrawable = -1;
    public static int disabledTextColor = -7829368;
    private Button leftArrowButton;
    private Button rightArrowButton;
    private TextView monthTitleTextView;
    private GridView weekdayGridView;
    private InfiniteViewPager dateViewPager;
    private DatePageChangeListener pageChangeListener;
    private ArrayList<DateGridFragment> fragments;
    private int themeResource;
    public static final String DIALOG_TITLE = "dialogTitle";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String SHOW_NAVIGATION_ARROWS = "showNavigationArrows";
    public static final String DISABLE_DATES = "disableDates";
    public static final String SELECTED_DATES = "selectedDates";
    public static final String MIN_DATE = "minDate";
    public static final String MAX_DATE = "maxDate";
    public static final String ENABLE_SWIPE = "enableSwipe";
    public static final String START_DAY_OF_WEEK = "startDayOfWeek";
    public static final String SIX_WEEKS_IN_CALENDAR = "sixWeeksInCalendar";
    public static final String ENABLE_CLICK_ON_DISABLED_DATES = "enableClickOnDisabledDates";
    public static final String SQUARE_TEXT_VIEW_CELL = "squareTextViewCell";
    public static final String THEME_RESOURCE = "themeResource";
    public static final String _MIN_DATE_TIME = "_minDateTime";
    public static final String _MAX_DATE_TIME = "_maxDateTime";
    public static final String _BACKGROUND_FOR_DATETIME_MAP = "_backgroundForDateTimeMap";
    public static final String _TEXT_COLOR_FOR_DATETIME_MAP = "_textColorForDateTimeMap";
    protected String dialogTitle;
    protected int month;
    protected int year;
    protected ArrayList<DateTime> disableDates;
    protected ArrayList<DateTime> selectedDates;
    protected DateTime minDateTime;
    protected DateTime maxDateTime;
    protected ArrayList<DateTime> dateInMonthsList;
    protected HashMap<String, Object> caldroidData;
    protected HashMap<String, Object> extraData;
    protected HashMap<DateTime, Integer> backgroundForDateTimeMap;
    protected HashMap<DateTime, Integer> textColorForDateTimeMap;
    protected int startDayOfWeek;
    private boolean sixWeeksInCalendar;
    protected ArrayList<CaldroidCustomGridAdapter> datePagerAdapters;
    protected boolean enableSwipe;
    protected boolean showNavigationArrows;
    protected boolean enableClickOnDisabledDates;
    protected boolean squareTextViewCell;
    private OnItemClickListener dateItemClickListener;
    private OnItemLongClickListener dateItemLongClickListener;
    private CaldroidListenerModified caldroidListener;

    public CaldroidFragmentModified() {
        this.monthYearFormatter = new Formatter(this.monthYearStringBuilder, Locale.getDefault());
        this.themeResource = style.CaldroidDefault;
        this.month = -1;
        this.year = -1;
        this.disableDates = new ArrayList();
        this.selectedDates = new ArrayList();
        this.caldroidData = new HashMap();
        this.extraData = new HashMap();
        this.backgroundForDateTimeMap = new HashMap();
        this.textColorForDateTimeMap = new HashMap();
        this.startDayOfWeek = SUNDAY;
        this.sixWeeksInCalendar = true;
        this.datePagerAdapters = new ArrayList();
        this.enableSwipe = true;
        this.showNavigationArrows = true;
        this.enableClickOnDisabledDates = false;
    }

    public CaldroidListenerModified getCaldroidListener() {
        return this.caldroidListener;
    }

    public CaldroidCustomGridAdapter getNewDatesGridAdapter(int month, int year) {
        return new CaldroidCustomGridAdapter(this.getActivity(), month, year, this.getCaldroidData(), this.extraData);
    }

    public WeekdayArrayAdapter getNewWeekdayAdapter() {
        return new WeekdayArrayAdapter(this.getActivity(), 17367043, this.getDaysOfWeek());
    }

    public GridView getWeekdayGridView() {
        return this.weekdayGridView;
    }

    public ArrayList<DateGridFragment> getFragments() {
        return this.fragments;
    }

    public InfiniteViewPager getDateViewPager() {
        return this.dateViewPager;
    }

    public HashMap<DateTime, Integer> getBackgroundForDateTimeMap() {
        return this.backgroundForDateTimeMap;
    }

    public HashMap<DateTime, Integer> getTextColorForDateTimeMap() {
        return this.textColorForDateTimeMap;
    }

    public Button getLeftArrowButton() {
        return this.leftArrowButton;
    }

    public Button getRightArrowButton() {
        return this.rightArrowButton;
    }

    public TextView getMonthTitleTextView() {
        return this.monthTitleTextView;
    }

    public void setMonthTitleTextView(TextView monthTitleTextView) {
        this.monthTitleTextView = monthTitleTextView;
    }

    public ArrayList<CaldroidCustomGridAdapter> getDatePagerAdapters() {
        return this.datePagerAdapters;
    }

    public HashMap<String, Object> getCaldroidData() {
        this.caldroidData.clear();
        this.caldroidData.put("disableDates", this.disableDates);
        this.caldroidData.put("selectedDates", this.selectedDates);
        this.caldroidData.put("_minDateTime", this.minDateTime);
        this.caldroidData.put("_maxDateTime", this.maxDateTime);
        this.caldroidData.put("startDayOfWeek", Integer.valueOf(this.startDayOfWeek));
        this.caldroidData.put("sixWeeksInCalendar", Boolean.valueOf(this.sixWeeksInCalendar));
        this.caldroidData.put("squareTextViewCell", Boolean.valueOf(this.squareTextViewCell));
        this.caldroidData.put("themeResource", Integer.valueOf(this.themeResource));
        this.caldroidData.put("_backgroundForDateTimeMap", this.backgroundForDateTimeMap);
        this.caldroidData.put("_textColorForDateTimeMap", this.textColorForDateTimeMap);
        return this.caldroidData;
    }

    public HashMap<String, Object> getExtraData() {
        return this.extraData;
    }

    public void setExtraData(HashMap<String, Object> extraData) {
        this.extraData = extraData;
    }

    public void setBackgroundResourceForDates(HashMap<Date, Integer> backgroundForDateMap) {
        if(backgroundForDateMap != null && backgroundForDateMap.size() != 0) {
            this.backgroundForDateTimeMap.clear();
            Iterator i$ = backgroundForDateMap.keySet().iterator();

            while(i$.hasNext()) {
                Date date = (Date)i$.next();
                Integer resource = backgroundForDateMap.get(date);
                DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
                this.backgroundForDateTimeMap.put(dateTime, resource);
            }

        }
    }

    public void clearBackgroundResourceForDates(List<Date> dates) {
        if(dates != null && dates.size() != 0) {
            Iterator i$ = dates.iterator();

            while(i$.hasNext()) {
                Date date = (Date)i$.next();
                this.clearBackgroundResourceForDate(date);
            }

        }
    }

    public void setBackgroundResourceForDateTimes(HashMap<DateTime, Integer> backgroundForDateTimeMap) {
        this.backgroundForDateTimeMap.putAll(backgroundForDateTimeMap);
    }

    public void clearBackgroundResourceForDateTimes(List<DateTime> dateTimes) {
        if(dateTimes != null && dateTimes.size() != 0) {
            Iterator i$ = dateTimes.iterator();

            while(i$.hasNext()) {
                DateTime dateTime = (DateTime)i$.next();
                this.backgroundForDateTimeMap.remove(dateTime);
            }

        }
    }

    public void setBackgroundResourceForDate(int backgroundRes, Date date) {
        DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
        this.backgroundForDateTimeMap.put(dateTime, Integer.valueOf(backgroundRes));
    }

    public void clearBackgroundResourceForDate(Date date) {
        DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
        this.backgroundForDateTimeMap.remove(dateTime);
    }

    public void setBackgroundResourceForDateTime(int backgroundRes, DateTime dateTime) {
        this.backgroundForDateTimeMap.put(dateTime, Integer.valueOf(backgroundRes));
    }

    public void clearBackgroundResourceForDateTime(DateTime dateTime) {
        this.backgroundForDateTimeMap.remove(dateTime);
    }

    public void setTextColorForDates(HashMap<Date, Integer> textColorForDateMap) {
        if(textColorForDateMap != null && textColorForDateMap.size() != 0) {
            this.textColorForDateTimeMap.clear();
            Iterator i$ = textColorForDateMap.keySet().iterator();

            while(i$.hasNext()) {
                Date date = (Date)i$.next();
                Integer resource = textColorForDateMap.get(date);
                DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
                this.textColorForDateTimeMap.put(dateTime, resource);
            }

        }
    }

    public void clearTextColorForDates(List<Date> dates) {
        if(dates != null && dates.size() != 0) {
            Iterator i$ = dates.iterator();

            while(i$.hasNext()) {
                Date date = (Date)i$.next();
                this.clearTextColorForDate(date);
            }

        }
    }

    public void setTextColorForDateTimes(HashMap<DateTime, Integer> textColorForDateTimeMap) {
        this.textColorForDateTimeMap.putAll(textColorForDateTimeMap);
    }

    public void setTextColorForDate(int textColorRes, Date date) {
        DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
        this.textColorForDateTimeMap.put(dateTime, Integer.valueOf(textColorRes));
    }

    public void clearTextColorForDate(Date date) {
        DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
        this.textColorForDateTimeMap.remove(dateTime);
    }

    public void setTextColorForDateTime(int textColorRes, DateTime dateTime) {
        this.textColorForDateTimeMap.put(dateTime, Integer.valueOf(textColorRes));
    }

    public Bundle getSavedStates() {
        Bundle bundle = new Bundle();
        bundle.putInt("month", this.month);
        bundle.putInt("year", this.year);
        if(this.dialogTitle != null) {
            bundle.putString("dialogTitle", this.dialogTitle);
        }

        if(this.selectedDates != null && this.selectedDates.size() > 0) {
            bundle.putStringArrayList("selectedDates", CalendarHelper.convertToStringList(this.selectedDates));
        }

        if(this.disableDates != null && this.disableDates.size() > 0) {
            bundle.putStringArrayList("disableDates", CalendarHelper.convertToStringList(this.disableDates));
        }

        if(this.minDateTime != null) {
            bundle.putString("minDate", this.minDateTime.format("YYYY-MM-DD"));
        }

        if(this.maxDateTime != null) {
            bundle.putString("maxDate", this.maxDateTime.format("YYYY-MM-DD"));
        }

        bundle.putBoolean("showNavigationArrows", this.showNavigationArrows);
        bundle.putBoolean("enableSwipe", this.enableSwipe);
        bundle.putInt("startDayOfWeek", this.startDayOfWeek);
        bundle.putBoolean("sixWeeksInCalendar", this.sixWeeksInCalendar);
        bundle.putInt("themeResource", this.themeResource);
        return bundle;
    }

    public void saveStatesToKey(Bundle outState, String key) {
        outState.putBundle(key, this.getSavedStates());
    }

    public void restoreStatesFromKey(Bundle savedInstanceState, String key) {
        if(savedInstanceState != null && savedInstanceState.containsKey(key)) {
            Bundle caldroidSavedState = savedInstanceState.getBundle(key);
            this.setArguments(caldroidSavedState);
        }

    }

    public void restoreDialogStatesFromKey(FragmentManager manager, Bundle savedInstanceState, String key, String dialogTag) {
        this.restoreStatesFromKey(savedInstanceState, key);
        CaldroidFragmentModified existingDialog = (CaldroidFragmentModified)manager.findFragmentByTag(dialogTag);
        if(existingDialog != null) {
            existingDialog.dismiss();
            this.show(manager, dialogTag);
        }

    }

    public int getCurrentVirtualPosition() {
        int currentPage = this.dateViewPager.getCurrentItem();
        return this.pageChangeListener.getCurrent(currentPage);
    }

    public void moveToDate(Date date) {
        this.moveToDateTime(CalendarHelper.convertDateToDateTime(date));
    }

    public void moveToDateTime(DateTime dateTime) {
        DateTime firstOfMonth = new DateTime(Integer.valueOf(this.year), Integer.valueOf(this.month), Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
        DateTime lastOfMonth = firstOfMonth.getEndOfMonth();
        DateTime firstDayLastMonth;
        int currentItem;
        if(dateTime.lt(firstOfMonth)) {
            firstDayLastMonth = dateTime.plus(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), DayOverflow.LastDay);
            this.pageChangeListener.setCurrentDateTime(firstDayLastMonth);
            currentItem = this.dateViewPager.getCurrentItem();
            this.pageChangeListener.refreshAdapters(currentItem);
            this.dateViewPager.setCurrentItem(currentItem - 1);
        } else if(dateTime.gt(lastOfMonth)) {
            firstDayLastMonth = dateTime.minus(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), DayOverflow.LastDay);
            this.pageChangeListener.setCurrentDateTime(firstDayLastMonth);
            currentItem = this.dateViewPager.getCurrentItem();
            this.pageChangeListener.refreshAdapters(currentItem);
            this.dateViewPager.setCurrentItem(currentItem + 1);
        }

    }

    public void setCalendarDate(Date date) {
        this.setCalendarDateTime(CalendarHelper.convertDateToDateTime(date));
    }

    public void setCalendarDateTime(DateTime dateTime) {
        this.month = dateTime.getMonth().intValue();
        this.year = dateTime.getYear().intValue();
        if(this.caldroidListener != null) {
            this.caldroidListener.onChangeMonth(this.month, this.year);
        }

        this.refreshView();
    }

    public void prevMonth() {
        this.dateViewPager.setCurrentItem(this.pageChangeListener.getCurrentPage() - 1);
    }

    public void nextMonth() {
        this.dateViewPager.setCurrentItem(this.pageChangeListener.getCurrentPage() + 1);
    }

    public void clearDisableDates() {
        this.disableDates.clear();
    }

    public void setDisableDates(ArrayList<Date> disableDateList) {
        if(disableDateList != null && disableDateList.size() != 0) {
            this.disableDates.clear();
            Iterator i$ = disableDateList.iterator();

            while(i$.hasNext()) {
                Date date = (Date)i$.next();
                DateTime dateTime = CalendarHelper.convertDateToDateTime(date);
                this.disableDates.add(dateTime);
            }

        }
    }

    public void setDisableDatesFromString(ArrayList<String> disableDateStrings) {
        this.setDisableDatesFromString(disableDateStrings, null);
    }

    public void setDisableDatesFromString(ArrayList<String> disableDateStrings, String dateFormat) {
        if(disableDateStrings != null) {
            this.disableDates.clear();
            Iterator i$ = disableDateStrings.iterator();

            while(i$.hasNext()) {
                String dateString = (String)i$.next();
                DateTime dateTime = CalendarHelper.getDateTimeFromString(dateString, dateFormat);
                this.disableDates.add(dateTime);
            }

        }
    }

    public void clearSelectedDates() {
        this.selectedDates.clear();
    }

    public void setSelectedDates(Date fromDate, Date toDate) {
        if(fromDate != null && toDate != null && !fromDate.after(toDate)) {
            this.selectedDates.clear();
            DateTime fromDateTime = CalendarHelper.convertDateToDateTime(fromDate);
            DateTime toDateTime = CalendarHelper.convertDateToDateTime(toDate);

            for(DateTime dateTime = fromDateTime; dateTime.lt(toDateTime); dateTime = dateTime.plusDays(Integer.valueOf(1))) {
                this.selectedDates.add(dateTime);
            }

            this.selectedDates.add(toDateTime);
        }
    }

    public void setSelectedDateStrings(String fromDateString, String toDateString, String dateFormat) throws ParseException {
        Date fromDate = CalendarHelper.getDateFromString(fromDateString, dateFormat);
        Date toDate = CalendarHelper.getDateFromString(toDateString, dateFormat);
        this.setSelectedDates(fromDate, toDate);
    }

    public boolean isShowNavigationArrows() {
        return this.showNavigationArrows;
    }

    //NOTE: Integer replaced with View.VISIBLE and View.GONE
    public void setShowNavigationArrows(boolean showNavigationArrows) {
        this.showNavigationArrows = showNavigationArrows;
        if(showNavigationArrows) {
            this.leftArrowButton.setVisibility(View.VISIBLE);
            this.rightArrowButton.setVisibility(View.VISIBLE);
        } else {
            this.leftArrowButton.setVisibility(View.GONE);
            this.rightArrowButton.setVisibility(View.GONE);
        }

    }

    public boolean isEnableSwipe() {
        return this.enableSwipe;
    }

    public void setEnableSwipe(boolean enableSwipe) {
        this.enableSwipe = enableSwipe;
        this.dateViewPager.setEnabled(enableSwipe);
    }

    public void setMinDate(Date minDate) {
        if(minDate == null) {
            this.minDateTime = null;
        } else {
            this.minDateTime = CalendarHelper.convertDateToDateTime(minDate);
        }

    }

    public boolean isSixWeeksInCalendar() {
        return this.sixWeeksInCalendar;
    }

    public void setSixWeeksInCalendar(boolean sixWeeksInCalendar) {
        this.sixWeeksInCalendar = sixWeeksInCalendar;
        this.dateViewPager.setSixWeeksInCalendar(sixWeeksInCalendar);
    }

    public void setMinDateFromString(String minDateString, String dateFormat) {
        if(minDateString == null) {
            this.setMinDate(null);
        } else {
            this.minDateTime = CalendarHelper.getDateTimeFromString(minDateString, dateFormat);
        }

    }

    public void setMaxDate(Date maxDate) {
        if(maxDate == null) {
            this.maxDateTime = null;
        } else {
            this.maxDateTime = CalendarHelper.convertDateToDateTime(maxDate);
        }

    }

    public void setMaxDateFromString(String maxDateString, String dateFormat) {
        if(maxDateString == null) {
            this.setMaxDate(null);
        } else {
            this.maxDateTime = CalendarHelper.getDateTimeFromString(maxDateString, dateFormat);
        }

    }

    public void setCaldroidListener(CaldroidListenerModified caldroidListener) {
        this.caldroidListener = caldroidListener;
    }


    /****************************************************************************************************
     * Modified to include double click
     ****************************************************************************************************/
    private int numberOfClick = 0;

    public OnItemClickListener getDateItemClickListener() {
        if(this.dateItemClickListener == null)
        {
            this.dateItemClickListener = new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    numberOfClick++;
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            numberOfClick = 0;
                        }
                    };

                    //For the onSelectDate(date, view); method in CaldroidListener
                    if (numberOfClick == 1)
                    {
                        //Wait to see if second click will follow suit
                        handler.postDelayed(r, 250);

                        DateTime dateTime = CaldroidFragmentModified.this.dateInMonthsList.get(position);
                        if (CaldroidFragmentModified.this.caldroidListener != null) {
                            if (!CaldroidFragmentModified.this.enableClickOnDisabledDates && (CaldroidFragmentModified.this.minDateTime != null && dateTime.lt(CaldroidFragmentModified.this.minDateTime) || CaldroidFragmentModified.this.maxDateTime != null && dateTime.gt(CaldroidFragmentModified.this.maxDateTime) || CaldroidFragmentModified.this.disableDates != null && CaldroidFragmentModified.this.disableDates.indexOf(dateTime) != -1)) {
                                return;
                            }

                            Date date = CalendarHelper.convertDateTimeToDate(dateTime);
                            CaldroidFragmentModified.this.caldroidListener.onSelectDate(date, view);
                        }

                    }

                    //For the onSelectDateTwice(date, view); method in CaldroidListenerModified
                    else if(numberOfClick == 2)
                    {
                        numberOfClick = 0;

                        DateTime dateTime = CaldroidFragmentModified.this.dateInMonthsList.get(position);
                        if (CaldroidFragmentModified.this.caldroidListener != null) {
                            if (!CaldroidFragmentModified.this.enableClickOnDisabledDates && (CaldroidFragmentModified.this.minDateTime != null && dateTime.lt(CaldroidFragmentModified.this.minDateTime) || CaldroidFragmentModified.this.maxDateTime != null && dateTime.gt(CaldroidFragmentModified.this.maxDateTime) || CaldroidFragmentModified.this.disableDates != null && CaldroidFragmentModified.this.disableDates.indexOf(dateTime) != -1)) {
                                return;
                            }

                            Date date = CalendarHelper.convertDateTimeToDate(dateTime);
                            CaldroidFragmentModified.this.caldroidListener.onSelectDateTwice(date, view);
                        }
                    }
                }
            };
        }

        return this.dateItemClickListener;
    }

    public OnItemLongClickListener getDateItemLongClickListener() {
        if(this.dateItemLongClickListener == null) {
            this.dateItemLongClickListener = new OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    DateTime dateTime = CaldroidFragmentModified.this.dateInMonthsList.get(position);
                    if(CaldroidFragmentModified.this.caldroidListener != null) {
                        if(!CaldroidFragmentModified.this.enableClickOnDisabledDates && (CaldroidFragmentModified.this.minDateTime != null && dateTime.lt(CaldroidFragmentModified.this.minDateTime) || CaldroidFragmentModified.this.maxDateTime != null && dateTime.gt(CaldroidFragmentModified.this.maxDateTime) || CaldroidFragmentModified.this.disableDates != null && CaldroidFragmentModified.this.disableDates.indexOf(dateTime) != -1)) {
                            return false;
                        }

                        Date date = CalendarHelper.convertDateTimeToDate(dateTime);
                        CaldroidFragmentModified.this.caldroidListener.onLongClickDate(date, view);
                    }

                    return true;
                }
            };
        }

        return this.dateItemLongClickListener;
    }

    /****************************************************************************************************
     * Create Drag-And-Drop Functionality here
     ****************************************************************************************************/
    // TODO: WORK-IN-PROGRESS Define Touch Listener
    private final class OnTouchItemListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return motionEvent.getAction() == MotionEvent.ACTION_DOWN;
        }
    }


    protected void refreshMonthTitleTextView() {
        this.firstMonthTime.year = this.year;
        this.firstMonthTime.month = this.month - 1;
        this.firstMonthTime.monthDay = 1;
        long millis = this.firstMonthTime.toMillis(true);
        this.monthYearStringBuilder.setLength(0);
        String monthTitle = DateUtils.formatDateRange(this.getActivity(), this.monthYearFormatter, millis, millis, 52).toString();
        this.monthTitleTextView.setText(monthTitle.toUpperCase(Locale.getDefault()));
    }

    public void refreshView() {
        if(this.month != -1 && this.year != -1) {
            this.refreshMonthTitleTextView();
            Iterator i$ = this.datePagerAdapters.iterator();

            while(i$.hasNext()) {
                CaldroidCustomGridAdapter adapter = (CaldroidCustomGridAdapter)i$.next();
                adapter.setCaldroidData(this.getCaldroidData());
                adapter.setExtraData(this.extraData);
                adapter.updateToday();
                adapter.notifyDataSetChanged();
            }

        }
    }

    protected void retrieveInitialArgs() {
        Bundle args = this.getArguments();
        if(args != null) {
            this.month = args.getInt("month", -1);
            this.year = args.getInt("year", -1);
            this.dialogTitle = args.getString("dialogTitle");
            Dialog dateTime = this.getDialog();
            if(dateTime != null) {
                if(this.dialogTitle != null) {
                    dateTime.setTitle(this.dialogTitle);
                } else {
                    dateTime.requestWindowFeature(1);
                }
            }

            this.startDayOfWeek = args.getInt("startDayOfWeek", 1);
            if(this.startDayOfWeek > 7) {
                this.startDayOfWeek %= 7;
            }

            this.showNavigationArrows = args.getBoolean("showNavigationArrows", true);
            this.enableSwipe = args.getBoolean("enableSwipe", true);
            this.sixWeeksInCalendar = args.getBoolean("sixWeeksInCalendar", true);
            int orientation = this.getResources().getConfiguration().orientation;
            if(orientation == 1) {
                this.squareTextViewCell = args.getBoolean("squareTextViewCell", true);
            } else {
                this.squareTextViewCell = args.getBoolean("squareTextViewCell", false);
            }

            this.enableClickOnDisabledDates = args.getBoolean("enableClickOnDisabledDates", false);
            ArrayList disableDateStrings = args.getStringArrayList("disableDates");
            String minDateTimeString;
            if(disableDateStrings != null && disableDateStrings.size() > 0) {
                this.disableDates.clear();
                Iterator selectedDateStrings = disableDateStrings.iterator();

                while(selectedDateStrings.hasNext()) {
                    minDateTimeString = (String)selectedDateStrings.next();
                    DateTime maxDateTimeString = CalendarHelper.getDateTimeFromString(minDateTimeString, "yyyy-MM-dd");
                    this.disableDates.add(maxDateTimeString);
                }
            }

            ArrayList selectedDateStrings1 = args.getStringArrayList("selectedDates");
            String maxDateTimeString1;
            if(selectedDateStrings1 != null && selectedDateStrings1.size() > 0) {
                this.selectedDates.clear();
                Iterator minDateTimeString1 = selectedDateStrings1.iterator();

                while(minDateTimeString1.hasNext()) {
                    maxDateTimeString1 = (String)minDateTimeString1.next();
                    DateTime dt = CalendarHelper.getDateTimeFromString(maxDateTimeString1, "yyyy-MM-dd");
                    this.selectedDates.add(dt);
                }
            }

            minDateTimeString = args.getString("minDate");
            if(minDateTimeString != null) {
                this.minDateTime = CalendarHelper.getDateTimeFromString(minDateTimeString, null);
            }

            maxDateTimeString1 = args.getString("maxDate");
            if(maxDateTimeString1 != null) {
                this.maxDateTime = CalendarHelper.getDateTimeFromString(maxDateTimeString1, null);
            }

            this.themeResource = args.getInt("themeResource", style.CaldroidDefault);
        }

        if(this.month == -1 || this.year == -1) {
            DateTime dateTime1 = DateTime.today(TimeZone.getDefault());
            this.month = dateTime1.getMonth().intValue();
            this.year = dateTime1.getYear().intValue();
        }

    }

    public static CaldroidFragmentModified newInstance(String dialogTitle, int month, int year) {
        CaldroidFragmentModified f = new CaldroidFragmentModified();
        Bundle args = new Bundle();
        args.putString("dialogTitle", dialogTitle);
        args.putInt("month", month);
        args.putInt("year", year);
        f.setArguments(args);
        return f;
    }

    public void onDestroyView() {
        if(this.getDialog() != null && this.getRetainInstance()) {
            this.getDialog().setDismissMessage(null);
        }

        super.onDestroyView();
    }

    public void setThemeResource(int id) {
        this.themeResource = id;
    }

    public int getThemeResource() {
        return this.themeResource;
    }

    public static LayoutInflater getLayoutInflater(Context context, LayoutInflater origInflater, int themeResource) {
        ContextThemeWrapper wrapped = new ContextThemeWrapper(context, themeResource);
        return origInflater.cloneInContext(wrapped);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.retrieveInitialArgs();
        if(this.getDialog() != null) {
            try {
                this.setRetainInstance(true);
            } catch (IllegalStateException var7) {
                var7.printStackTrace();
            }
        }

        LayoutInflater localInflater = getLayoutInflater(this.getActivity(), inflater, this.themeResource);
        View view = localInflater.inflate(layout.calendar_view, container, false);
        this.monthTitleTextView = (TextView)view.findViewById(id.calendar_month_year_textview);
        this.leftArrowButton = (Button)view.findViewById(id.calendar_left_arrow);
        this.rightArrowButton = (Button)view.findViewById(id.calendar_right_arrow);
        this.leftArrowButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CaldroidFragmentModified.this.prevMonth();
            }
        });
        this.rightArrowButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CaldroidFragmentModified.this.nextMonth();
            }
        });
        this.setShowNavigationArrows(this.showNavigationArrows);
        this.weekdayGridView = (GridView)view.findViewById(id.weekday_gridview);
        WeekdayArrayAdapter weekdaysAdapter = this.getNewWeekdayAdapter();
        this.weekdayGridView.setAdapter(weekdaysAdapter);
        this.setupDateGridPages(view);
        this.refreshView();
        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(this.caldroidListener != null) {
            this.caldroidListener.onCaldroidViewCreated();
        }

    }

    protected int getGridViewRes() {
        return layout.date_grid_fragment;
    }

    private void setupDateGridPages(View view) {
        DateTime currentDateTime = new DateTime(Integer.valueOf(this.year), Integer.valueOf(this.month), Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
        this.pageChangeListener = new DatePageChangeListener();
        this.pageChangeListener.setCurrentDateTime(currentDateTime);
        CaldroidCustomGridAdapter adapter0 = this.getNewDatesGridAdapter(currentDateTime.getMonth().intValue(), currentDateTime.getYear().intValue());
        this.dateInMonthsList = adapter0.getDatetimeList();
        DateTime nextDateTime = currentDateTime.plus(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), DayOverflow.LastDay);
        CaldroidCustomGridAdapter adapter1 = this.getNewDatesGridAdapter(nextDateTime.getMonth().intValue(), nextDateTime.getYear().intValue());
        DateTime next2DateTime = nextDateTime.plus(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), DayOverflow.LastDay);
        CaldroidCustomGridAdapter adapter2 = this.getNewDatesGridAdapter(next2DateTime.getMonth().intValue(), next2DateTime.getYear().intValue());
        DateTime prevDateTime = currentDateTime.minus(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), DayOverflow.LastDay);
        CaldroidCustomGridAdapter adapter3 = this.getNewDatesGridAdapter(prevDateTime.getMonth().intValue(), prevDateTime.getYear().intValue());
        this.datePagerAdapters.add(adapter0);
        this.datePagerAdapters.add(adapter1);
        this.datePagerAdapters.add(adapter2);
        this.datePagerAdapters.add(adapter3);
        this.pageChangeListener.setCaldroidCustomGridAdapters(this.datePagerAdapters);
        this.dateViewPager = (InfiniteViewPager)view.findViewById(id.months_infinite_pager);
        this.dateViewPager.setEnabled(this.enableSwipe);
        this.dateViewPager.setSixWeeksInCalendar(this.sixWeeksInCalendar);
        this.dateViewPager.setDatesInMonth(this.dateInMonthsList);
        MonthPagerAdapter pagerAdapter = new MonthPagerAdapter(this.getChildFragmentManager());
        this.fragments = pagerAdapter.getFragments();

        for(int infinitePagerAdapter = 0; infinitePagerAdapter < 4; ++infinitePagerAdapter) {
            DateGridFragment dateGridFragment = this.fragments.get(infinitePagerAdapter);
            CaldroidCustomGridAdapter adapter = this.datePagerAdapters.get(infinitePagerAdapter);
            dateGridFragment.setGridViewRes(this.getGridViewRes());
            dateGridFragment.setGridAdapter(adapter);
            dateGridFragment.setOnItemClickListener(this.getDateItemClickListener());
            dateGridFragment.setOnItemLongClickListener(this.getDateItemLongClickListener());
        }

        InfinitePagerAdapter var14 = new InfinitePagerAdapter(pagerAdapter);
        this.dateViewPager.setAdapter(var14);
        this.dateViewPager.setOnPageChangeListener(this.pageChangeListener);
    }

    protected ArrayList<String> getDaysOfWeek() {
        ArrayList list = new ArrayList();
        SimpleDateFormat fmt = new SimpleDateFormat("EEE", Locale.getDefault());
        DateTime sunday = new DateTime(Integer.valueOf(2013), Integer.valueOf(2), Integer.valueOf(17), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
        DateTime nextDay = sunday.plusDays(Integer.valueOf(this.startDayOfWeek - SUNDAY));

        for(int i = 0; i < 7; ++i) {
            Date date = CalendarHelper.convertDateTimeToDate(nextDay);
            list.add(fmt.format(date).toUpperCase());
            nextDay = nextDay.plusDays(Integer.valueOf(1));
        }

        return list;
    }

    public void onDetach() {
        super.onDetach();

        try {
            Field e = Fragment.class.getDeclaredField("mChildFragmentManager");
            e.setAccessible(true);
            e.set(this, null);
        } catch (NoSuchFieldException var2) {
            throw new RuntimeException(var2);
        } catch (IllegalAccessException var3) {
            throw new RuntimeException(var3);
        }
    }

    public class DatePageChangeListener implements OnPageChangeListener {
        private int currentPage = 1000;
        private DateTime currentDateTime;
        private ArrayList<CaldroidCustomGridAdapter> caldroidCustomGridAdapters;

        public DatePageChangeListener() {
        }

        public int getCurrentPage() {
            return this.currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public DateTime getCurrentDateTime() {
            return this.currentDateTime;
        }

        public void setCurrentDateTime(DateTime dateTime) {
            this.currentDateTime = dateTime;
            CaldroidFragmentModified.this.setCalendarDateTime(this.currentDateTime);
        }

        public ArrayList<CaldroidCustomGridAdapter> getCaldroidCustomGridAdapters() {
            return this.caldroidCustomGridAdapters;
        }

        public void setCaldroidCustomGridAdapters(ArrayList<CaldroidCustomGridAdapter> caldroidCustomGridAdapters) {
            this.caldroidCustomGridAdapters = caldroidCustomGridAdapters;
        }

        private int getNext(int position) {
            return (position + 1) % 4;
        }

        private int getPrevious(int position) {
            return (position + 3) % 4;
        }

        public int getCurrent(int position) {
            return position % 4;
        }

        public void onPageScrollStateChanged(int position) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void refreshAdapters(int position) {
            CaldroidCustomGridAdapter currentAdapter = this.caldroidCustomGridAdapters.get(this.getCurrent(position));
            CaldroidCustomGridAdapter prevAdapter = this.caldroidCustomGridAdapters.get(this.getPrevious(position));
            CaldroidCustomGridAdapter nextAdapter = this.caldroidCustomGridAdapters.get(this.getNext(position));
            if(position == this.currentPage) {
                currentAdapter.setAdapterDateTime(this.currentDateTime);
                currentAdapter.notifyDataSetChanged();
                prevAdapter.setAdapterDateTime(this.currentDateTime.minus(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), DayOverflow.LastDay));
                prevAdapter.notifyDataSetChanged();
                nextAdapter.setAdapterDateTime(this.currentDateTime.plus(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), DayOverflow.LastDay));
                nextAdapter.notifyDataSetChanged();
            } else if(position > this.currentPage) {
                this.currentDateTime = this.currentDateTime.plus(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), DayOverflow.LastDay);
                nextAdapter.setAdapterDateTime(this.currentDateTime.plus(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), DayOverflow.LastDay));
                nextAdapter.notifyDataSetChanged();
            } else {
                this.currentDateTime = this.currentDateTime.minus(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), DayOverflow.LastDay);
                prevAdapter.setAdapterDateTime(this.currentDateTime.minus(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), DayOverflow.LastDay));
                prevAdapter.notifyDataSetChanged();
            }

            this.currentPage = position;
        }

        public void onPageSelected(int position) {
            this.refreshAdapters(position);
            CaldroidFragmentModified.this.setCalendarDateTime(this.currentDateTime);
            CaldroidCustomGridAdapter currentAdapter = this.caldroidCustomGridAdapters.get(position % 4);
            CaldroidFragmentModified.this.dateInMonthsList.clear();
            CaldroidFragmentModified.this.dateInMonthsList.addAll(currentAdapter.getDatetimeList());
        }
    }
}