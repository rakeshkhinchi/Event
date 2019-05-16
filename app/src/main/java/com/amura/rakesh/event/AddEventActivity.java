package com.amura.rakesh.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import static com.amura.rakesh.event.Utility.convertDate;

public class AddEventActivity extends AppCompatActivity {

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mMinute;
    private int mHour;
    private EditText etAgenda;
    private EditText etEmail;
    private Button btCancel;
    private Button btCreate;
    private TextView tvAgendaStartDate;
    private TextView tvAgendaStartTime;
    private TextView tvAgendaEndDate;
    private TextView tvAgendaEndTime;
    private String agenda;
    private String email;
    private String agendaStartDate;
    private String agendaStartTime;
    private String agendaEndDate;
    private String agendaEndTime;
    private String action;
    private int eventColor;
    private long eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_event);

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        etAgenda = (EditText) findViewById(R.id.et_agenda);

        etEmail = (EditText) findViewById(R.id.et_email);

        tvAgendaStartDate = (TextView) findViewById(R.id.tv_agenda_start_date);

        tvAgendaStartTime = (TextView) findViewById(R.id.tv_agenda_start_time);

        tvAgendaEndDate = (TextView) findViewById(R.id.tv_agenda_end_date);

        tvAgendaEndTime = (TextView) findViewById(R.id.tv_agenda_end_time);

        btCreate = (Button) findViewById(R.id.btn_create);

        btCancel = (Button) findViewById(R.id.btn_cancel);

        action = getIntent().getStringExtra("ACTION");


        // for add event
        if (action.equalsIgnoreCase("ADD")) {

            getSupportActionBar().setTitle("Add Event");

            tvAgendaStartDate.setText("" + Utility.currentDate());
            tvAgendaEndDate.setText("" + currentDatePlusOneHour());
            tvAgendaStartTime.setText("" + currentTime());
            tvAgendaEndTime.setText("" + currentTimePlusOneHour());

            btCreate.setText("CREATE EVENT");

        }
        // for update event
        else {

            getSupportActionBar().setTitle("Update Event");

            eventId = getIntent().getLongExtra("EVENT_ID", 0);
            agenda = getIntent().getStringExtra("AGENDA");
            email = getIntent().getStringExtra("EMAIL");
            agendaStartDate = getIntent().getStringExtra("AGENDA_START_DATE");
            agendaStartTime = getIntent().getStringExtra("AGENDA_START_TIME");
            agendaEndDate = getIntent().getStringExtra("AGENDA_END_DATE");
            agendaEndTime = getIntent().getStringExtra("AGENDA_END_TIME");

            eventColor = getIntent().getIntExtra("EVENT_COLOR", 0);

            etAgenda.setText(agenda);
            etEmail.setText(email);
            tvAgendaStartDate.setText("" + agendaStartDate);
            tvAgendaEndDate.setText("" + agendaEndDate);
            tvAgendaStartTime.setText("" + agendaStartTime);
            tvAgendaEndTime.setText("" + agendaEndTime);

            btCreate.setText("UPDATE EVENT");
        }


        tvAgendaStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerDialog(0);

            }
        });

        tvAgendaStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog(0);
            }
        });

        tvAgendaEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog(1);
            }
        });

        tvAgendaEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog(1);
            }
        });

        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etAgenda.getText().toString().equalsIgnoreCase("")) {

                    Toast.makeText(getBaseContext(), "Add Agenda", Toast.LENGTH_LONG).show();

                } else if (etEmail.getText().toString().equalsIgnoreCase("")) {

                    Toast.makeText(getBaseContext(), "Add Email", Toast.LENGTH_LONG).show();

                } else if (!isValidEmail(etEmail.getText().toString())) {

                    Toast.makeText(getBaseContext(), "Email address is not valid", Toast.LENGTH_LONG).show();

                } else if (!compareStartEndDate()) {

                    Toast.makeText(getBaseContext(), "Start datetime must be before to end datetime", Toast.LENGTH_LONG).show();

                } else {
                    addEvent();
                }


            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:

                finish();

                return true;

            case R.id.action_delete:

                deleteDialog();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // for delete dialog for deleting event
    private void deleteDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Delete event");
        alert.setMessage("Are you sure you want to delete this event?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // continue with delete

                Intent intent = new Intent();

                intent.putExtra("EVENT_ID", eventId);
                intent.putExtra("AGENDA", etAgenda.getText().toString());
                intent.putExtra("EMAIL", etEmail.getText().toString());
                intent.putExtra("AGENDA_START_DATE", tvAgendaStartDate.getText().toString());
                intent.putExtra("AGENDA_START_TIME", tvAgendaStartTime.getText().toString());
                intent.putExtra("AGENDA_END_DATE", tvAgendaEndDate.getText().toString());
                intent.putExtra("AGENDA_END_TIME", tvAgendaEndTime.getText().toString());
                intent.putExtra("ACTION", "delete");

                setResult(2, intent);
                finish();//finishing activity

            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        alert.show();


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.action_delete);

        if (action.equalsIgnoreCase("add")) {
            item.setVisible(false);
        } else {
            item.setVisible(true);

        }

        //Also you can do this for sub menu

        return super.onPrepareOptionsMenu(menu);
    }


    // compare start and end date , start date must be before end date
    private boolean compareStartEndDate() {

        String startDateTime = tvAgendaStartDate.getText().toString() + " " + tvAgendaStartTime.getText().toString();
        String endDateTime = tvAgendaEndDate.getText().toString() + " " + tvAgendaEndTime.getText().toString();

        Date date1 = null, date2 = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            date1 = sdf.parse(startDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            date2 = sdf.parse(endDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date1.compareTo(date2) > 0) {
            System.out.println("Date1 is after Date2");
            return false;

        } else if (date1.compareTo(date2) < 0) {
            System.out.println("Date1 is before Date2");
            return true;

        } else if (date1.compareTo(date2) == 0) {
            System.out.println("Date1 is equal to Date2");
            return false;

        } else {
            System.out.println("How to get here?");
            return false;
        }


    }


    // add one hour to current date
    private String currentDatePlusOneHour() {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);

        String currentTime = dateFormat.format(calendar.getTime());

        return currentTime;
    }


    // add one hour to current time
    private String currentTimePlusOneHour() {

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);

        String currentTime = dateFormat.format(calendar.getTime());

        return currentTime;

    }

    // For adding and updating event result code 1 is for adding event and result code 2 is for updating event
    private void addEvent() {

        Intent intent = new Intent();
        intent.putExtra("AGENDA", etAgenda.getText().toString());
        intent.putExtra("EMAIL", etEmail.getText().toString());
        intent.putExtra("AGENDA_START_DATE", tvAgendaStartDate.getText().toString());
        intent.putExtra("AGENDA_START_TIME", tvAgendaStartTime.getText().toString());
        intent.putExtra("AGENDA_END_DATE", tvAgendaEndDate.getText().toString());
        intent.putExtra("AGENDA_END_TIME", tvAgendaEndTime.getText().toString());

        if (action.equalsIgnoreCase("Add")) {

            setResult(1, intent);

        } else {

            intent.putExtra("EVENT_ID", eventId);
            intent.putExtra("ACTION", "EDIT");
            intent.putExtra("EVENT_COLOR", eventColor);
            setResult(2, intent);

        }

        finish();//finishing activity

    }

    // to pick date from dialog
    private void datePickerDialog(int i) {

        final int flag = i;
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        if (flag == 0)
                            tvAgendaStartDate.setText("" + convertDate(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year));
                        else
                            tvAgendaEndDate.setText("" + convertDate(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year));

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    // to pick time from time picker dialog
    private void timePickerDialog(int i) {

        final int flag = i;

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if (flag == 0)
                            tvAgendaStartTime.setText("" + convertTime(hourOfDay + ":" + minute));
                        else
                            tvAgendaEndTime.setText("" + convertTime(hourOfDay + ":" + minute));
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();

    }

    // To convert time into specific format
    private String convertTime(String s) {

        Date initDate = null;
        try {
            initDate = new SimpleDateFormat("HH:mm").parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String parsedTime = formatter.format(initDate);

        return parsedTime;

    }

    // if the email is valid or not
    private boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    // To find current time
    private String currentTime() {

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();

        String currentTime = dateFormat.format(calendar.getTime());


        return currentTime;
    }
}
