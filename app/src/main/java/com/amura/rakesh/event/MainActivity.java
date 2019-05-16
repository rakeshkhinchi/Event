package com.amura.rakesh.event;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements WeekView.EventClickListener, WeekView.EventLongPressListener,
        WeekView.EmptyViewLongPressListener, WeekView.EmptyViewClickListener, MonthLoader.MonthChangeListener {

    private WeekView mWeekView;
    private Calendar startTime;
    private WeekViewEvent event;
    private Calendar endTime;
    private ArrayList<WeekViewEvent> mNewEvents;
    private RecyclerView recycleEventList;
    private AdapterEventList mAdapter;
    private FloatingActionButton fabAddEvent;
    private LinearLayout llEvents;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private Spinner spinnerEvents;
    private ArrayList<String> spinnerEventList;
    private LinearLayout llEventRange;
    private ArrayList<WeekViewEvent> filterEventslist;
    private int eventId = 0;
    private ArrayList<Integer> colorArray;
    private int spinnerPos;
    private TextView tvNoEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mWeekView = (WeekView) findViewById(R.id.weekView);
        recycleEventList = (RecyclerView) findViewById(R.id.recycler_event_list);
        llEvents = (LinearLayout) findViewById(R.id.ll_events);
        tvStartDate = (TextView) findViewById(R.id.tv_start_date);
        tvEndDate = (TextView) findViewById(R.id.tv_end_date);
        spinnerEvents = (Spinner) findViewById(R.id.spinner_events);
        llEventRange = (LinearLayout) findViewById(R.id.ll_events_range);
        fabAddEvent = (FloatingActionButton) findViewById(R.id.fab_add_event);
        tvNoEvents = (TextView) findViewById(R.id.tv_no_events);


        mWeekView.setVisibility(View.GONE);
        llEvents.setVisibility(View.VISIBLE);

        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);

        colorArray = new ArrayList<Integer>();
        colorArray.add(R.color.event_color_01);
        colorArray.add(R.color.event_color_02);
        colorArray.add(R.color.event_color_03);
        colorArray.add(R.color.event_color_04);

        tvStartDate.setText("" + Utility.currentDate());
        tvEndDate.setText("" + Utility.currentDate());

        mNewEvents = new ArrayList<WeekViewEvent>();
        mAdapter = new AdapterEventList(this, mNewEvents);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycleEventList.setLayoutManager(mLayoutManager);
        recycleEventList.setItemAnimator(new DefaultItemAnimator());
        recycleEventList.setAdapter(mAdapter);

        checkEmptyEvents();

        spinnerEventList = new ArrayList<String>();
        spinnerEventList.add("All events");
        spinnerEventList.add("Filter events");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerEventList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEvents.setAdapter(dataAdapter);

        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(), AddEventActivity.class);
                intent.putExtra("ACTION", "ADD");

                startActivityForResult(intent, 1);// Activity is started with requestCode 1
            }
        });

        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog(0);
            }
        });
        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog(1);
            }
        });

        spinnerEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spinnerPos = position;

                applySpinnerClick(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void checkEmptyEvents() {

        if (mAdapter.getItemCount() == 0) {
            tvNoEvents.setVisibility(View.VISIBLE);
        } else {
            tvNoEvents.setVisibility(View.GONE);
        }
    }

    // to get the date from date picker dialog
    private void datePickerDialog(int i) {

        final int flag = i;
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        if (flag == 0) {

                            tvStartDate.setText("" + Utility.convertDate(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year));
                            filterEvents(tvStartDate.getText().toString(), tvEndDate.getText().toString());

                        } else {

                            tvEndDate.setText("" + Utility.convertDate(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year));
                            filterEvents(tvStartDate.getText().toString(), tvEndDate.getText().toString());

                        }

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }


    // To filter events between specific date range
    private void filterEvents(String startDate, String endDate) {

        Date date1 = null, date2 = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            date1 = sdf.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            date2 = sdf.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date1.compareTo(date2) > 0) {

            Toast.makeText(getBaseContext(), "Start date must be before or equal to end date", Toast.LENGTH_LONG).show();

            tvStartDate.setText("" + Utility.currentDate());
            tvEndDate.setText("" + Utility.currentDate());

            filterEvents(tvStartDate.getText().toString(), tvEndDate.getText().toString());

        } else {

            filterEventslist = (ArrayList<WeekViewEvent>) filterDates(startDate, endDate);

            mAdapter = new AdapterEventList(this, filterEventslist);

            recycleEventList.setAdapter(mAdapter);

            checkEmptyEvents();

        }

    }


    // To get to filtered dates between specific dates
    private List<WeekViewEvent> filterDates(String startDate, String endDate) {

        ArrayList<WeekViewEvent> datesInRange = new ArrayList<>();

        for (int i = 0; i < mNewEvents.size(); i++) {

            Date dateStart = null;
            try {
                dateStart = new SimpleDateFormat("dd-MM-yyyy").parse(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Date dateEnd = null;
            try {
                dateEnd = new SimpleDateFormat("dd-MM-yyyy").parse(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calStart = mNewEvents.get(i).getStartTime();
            Date date = calStart.getTime();

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String strDate = dateFormat.format(date);

            try {
                date = new SimpleDateFormat("dd-MM-yyyy").parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (dateStart.compareTo(date) * date.compareTo(dateEnd) >= 0) {

                long Id = mNewEvents.get(i).getId();
                String agenda = mNewEvents.get(i).getName();

                Log.e("id : position : agenda", "" + Id + " : " + i + " : " + agenda);

                datesInRange.add(mNewEvents.get(i));

            }

        }

        return datesInRange;
    }

    //To get all events list
    private void allEvents() {

        mAdapter = new AdapterEventList(this, mNewEvents);

        recycleEventList.setAdapter(mAdapter);

        checkEmptyEvents();
    }

    // Call Back method  to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (data == null) {

            mWeekView.notifyDatasetChanged();

            mAdapter.notifyDataSetChanged();

            checkEmptyEvents();

        } else if (requestCode == 1) {

            // requestCode 1 is for add event

            // To get the event detail from intent to add the event into calendar

            String agenda = data.getStringExtra("AGENDA");
            String email = data.getStringExtra("EMAIL");
            String agendaStartDate = data.getStringExtra("AGENDA_START_DATE");
            String agendaStartTime = data.getStringExtra("AGENDA_START_TIME");
            String agendaEndDate = data.getStringExtra("AGENDA_END_DATE");
            String agendaEndTime = data.getStringExtra("AGENDA_END_TIME");

            startTime = Calendar.getInstance();
            startTime.set(Calendar.DAY_OF_MONTH, getDayFromDate(agendaStartDate));
            startTime.set(Calendar.HOUR_OF_DAY, getHourFromTime(agendaStartTime));
            startTime.set(Calendar.MINUTE, getMinFromTime(agendaStartTime));
            startTime.set(Calendar.MONTH, getMonthFromDate(agendaStartDate) - 1);
            startTime.set(Calendar.YEAR, getYearFromDate(agendaStartDate));

            endTime = (Calendar) startTime.clone();
            endTime.set(Calendar.DAY_OF_MONTH, getDayFromDate(agendaEndDate));
            endTime.set(Calendar.HOUR_OF_DAY, getHourFromTime(agendaEndTime));
            endTime.set(Calendar.MINUTE, getMinFromTime(agendaEndTime));
            endTime.set(Calendar.MONTH, getMonthFromDate(agendaEndDate) - 1);
            endTime.set(Calendar.YEAR, getYearFromDate(agendaEndDate));

            Log.e("eventId,agenda", "" + eventId + " : " + agenda);

            event = new WeekViewEvent(eventId, agenda, email, startTime, endTime);


            // To find random color code
            Random Dice = new Random();
            int n = Dice.nextInt(colorArray.size());

            event.setColor(getResources().getColor(colorArray.get(n)));

            mNewEvents.add(event);

            eventId++;

            mWeekView.notifyDatasetChanged();

            mAdapter.notifyDataSetChanged();

            checkEmptyEvents();

            applySpinnerClick(spinnerPos);


        } else if (requestCode == 2) {

            // requestCode 2 is for edit or delete event

            String action = data.getStringExtra("ACTION");


            // for deleting event
            if (action.equalsIgnoreCase("delete")) {

                Long eventId = data.getLongExtra("EVENT_ID", 0);

                Log.e("eventId delete", "" + eventId);

                int position = 0;

                for (int i = 0; i < mNewEvents.size(); i++) {
                    if (eventId == mNewEvents.get(i).getId()) {
                        position = i;
                        Log.e("eventId", "" + eventId);
                    }
                }

                mNewEvents.remove(position);

                mWeekView.notifyDatasetChanged();

                mAdapter.notifyDataSetChanged();

                checkEmptyEvents();

                applySpinnerClick(spinnerPos);


            }

            // for editing event
            else {

                Long eventId = data.getLongExtra("EVENT_ID", 0);
                String agenda = data.getStringExtra("AGENDA");
                String email = data.getStringExtra("EMAIL");
                String agendaStartDate = data.getStringExtra("AGENDA_START_DATE");
                String agendaStartTime = data.getStringExtra("AGENDA_START_TIME");
                String agendaEndDate = data.getStringExtra("AGENDA_END_DATE");
                String agendaEndTime = data.getStringExtra("AGENDA_END_TIME");
                int eventColor = data.getIntExtra("EVENT_COLOR", 0);


                startTime = Calendar.getInstance();
                startTime.set(Calendar.DAY_OF_MONTH, getDayFromDate(agendaStartDate));
                startTime.set(Calendar.HOUR_OF_DAY, getHourFromTime(agendaStartTime));
                startTime.set(Calendar.MINUTE, getMinFromTime(agendaStartTime));
                startTime.set(Calendar.MONTH, getMonthFromDate(agendaStartDate) - 1);
                startTime.set(Calendar.YEAR, getYearFromDate(agendaStartDate));

                endTime = (Calendar) startTime.clone();
                endTime.set(Calendar.DAY_OF_MONTH, getDayFromDate(agendaEndDate));
                endTime.set(Calendar.HOUR_OF_DAY, getHourFromTime(agendaEndTime));
                endTime.set(Calendar.MINUTE, getMinFromTime(agendaEndTime));
                endTime.set(Calendar.MONTH, getMonthFromDate(agendaEndDate) - 1);
                endTime.set(Calendar.YEAR, getYearFromDate(agendaEndDate));

                Log.e("eventId,agenda", "" + eventId + " : " + agenda);

                event = new WeekViewEvent(eventId, agenda, email, startTime, endTime);

                event.setColor(eventColor);

                int position = 0;

                for (int i = 0; i < mNewEvents.size(); i++) {

                    if (eventId == mNewEvents.get(i).getId()) {
                        position = i;

                        Log.e("eventId", "" + eventId);

                    }

                }


                mNewEvents.set(position, event);

                mWeekView.notifyDatasetChanged();

                mAdapter.notifyDataSetChanged();

                checkEmptyEvents();

                applySpinnerClick(spinnerPos);


            }

        }


    }

    // apply events spinner events
    private void applySpinnerClick(int spinnerPos) {

        if (spinnerPos == 0) {

            llEventRange.setVisibility(View.GONE);

            allEvents();

        } else {

            llEventRange.setVisibility(View.VISIBLE);

            filterEvents(tvStartDate.getText().toString(), tvEndDate.getText().toString());

        }

    }


    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     *
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        setupDateTimeInterpreter(id == R.id.action_week_view);

        switch (id) {

            case R.id.action_event_list:
                mWeekView.setVisibility(View.GONE);
                llEvents.setVisibility(View.VISIBLE);
                item.setChecked(!item.isChecked());

                return true;

            case R.id.action_day_view:
                mWeekView.setVisibility(View.VISIBLE);
                llEvents.setVisibility(View.GONE);

                item.setChecked(!item.isChecked());
                mWeekView.setNumberOfVisibleDays(1);

                // Lets change some dimensions to best fit the view.
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                //              }
                return true;
            case R.id.action_three_day_view:
                mWeekView.setVisibility(View.VISIBLE);
                llEvents.setVisibility(View.GONE);

                item.setChecked(!item.isChecked());
                mWeekView.setNumberOfVisibleDays(3);

                // Lets change some dimensions to best fit the view.
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                return true;
            case R.id.action_week_view:
                mWeekView.setVisibility(View.VISIBLE);
                llEvents.setVisibility(View.GONE);

                item.setChecked(!item.isChecked());
                mWeekView.setNumberOfVisibleDays(7);

                // Lets change some dimensions to best fit the view.
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                return true;


        }

        return super.onOptionsItemSelected(item);


    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        // Populate the week view with the events that was added by tapping on empty view.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        ArrayList<WeekViewEvent> newEvents = getNewEvents(newYear, newMonth);
        events.addAll(newEvents);
        return events;
    }


    /**
     * Get events that were added by tapping on empty view.
     *
     * @param year  The year currently visible on the week view.
     * @param month The month currently visible on the week view.
     * @return The events of the given year and month.
     */
    private ArrayList<WeekViewEvent> getNewEvents(int year, int month) {

        // Get the starting point and ending point of the given month. We need this to find the
        // events of the given month.
        Calendar startOfMonth = Calendar.getInstance();
        startOfMonth.set(Calendar.YEAR, year);
        startOfMonth.set(Calendar.MONTH, month - 1);
        startOfMonth.set(Calendar.DAY_OF_MONTH, 1);
        startOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        startOfMonth.set(Calendar.MINUTE, 0);
        startOfMonth.set(Calendar.SECOND, 0);
        startOfMonth.set(Calendar.MILLISECOND, 0);
        Calendar endOfMonth = (Calendar) startOfMonth.clone();
        endOfMonth.set(Calendar.DAY_OF_MONTH, endOfMonth.getMaximum(Calendar.DAY_OF_MONTH));
        endOfMonth.set(Calendar.HOUR_OF_DAY, 23);
        endOfMonth.set(Calendar.MINUTE, 59);
        endOfMonth.set(Calendar.SECOND, 59);

        // Find the events that were added by tapping on empty view and that occurs in the given
        // time frame.
        ArrayList<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        for (WeekViewEvent event : mNewEvents) {
            if (event.getEndTime().getTimeInMillis() > startOfMonth.getTimeInMillis() &&
                    event.getStartTime().getTimeInMillis() < endOfMonth.getTimeInMillis()) {
                events.add(event);
            }
        }
        return events;
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

        Toast.makeText(this, "" + event.getName() + "  (" + event.getLocation() + " )", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {

    }

    @Override
    public void onEmptyViewClicked(Calendar time) {

    }

    // get year form specific date
    private int getYearFromDate(String agendaStartDate) {

        Date date = null;
        try {
            date = new SimpleDateFormat("dd-MM-yyyy").parse(agendaStartDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return year;

    }

    // get day form specific date
    private int getDayFromDate(String agendaDate) {
        Date date = null;
        try {
            date = new SimpleDateFormat("dd-MM-yyyy").parse(agendaDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    // get minute form specific date
    private int getMinFromTime(String agendaTime) {
        Date date = null;
        try {
            date = new SimpleDateFormat("HH:mm").parse(agendaTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int min = calendar.get(Calendar.MINUTE);
        return min;

    }

    // get hour form specific date
    private int getHourFromTime(String agendaTime) {

        Date date = null;
        try {
            date = new SimpleDateFormat("HH:mm").parse(agendaTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour;

    }

    // get month form specific date
    private int getMonthFromDate(String agendaDate) {

        Date date = null;
        try {
            date = new SimpleDateFormat("dd-MM-yyyy").parse(agendaDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1;
        return month;

    }


}
