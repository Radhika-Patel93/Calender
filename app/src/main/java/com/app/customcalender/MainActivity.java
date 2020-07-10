package com.app.customcalender;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private int startGroup = -1;
    private int endGroup = -1;
    private int startChild = -1;
    private int endChild = -1;
    TextView txtCustomDate, txtDate, txtTime, txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCustomDate = findViewById(R.id.txtCustomDate);
        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);
        txtResult = findViewById(R.id.txtResult);

        txtCustomDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCustomDatePicker(view, txtResult);
            }
        });

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(MainActivity.this, R.style.DialogDateTheme, txtResult, Calendar.getInstance());
            }
        });

        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(MainActivity.this, R.style.DialogDateTheme, txtResult, Calendar.getInstance());
            }
        });
    }


    public void showDatePickerDialog(Activity activity, int themeResId, final TextView result, Calendar calendar) {
        // Create an instance of the DatePickerDialog dialog directly and display it
        new DatePickerDialog(activity, themeResId, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                result.setText("Selected date：" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + "");
            }
        }       // Set initial date
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


    public void showTimePickerDialog(Activity activity, int themeResId, final TextView result, Calendar calendar) {
        // Create a TimePickerDialog instance and display it
        new TimePickerDialog(activity, themeResId,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        result.setText("Selected time：" + hourOfDay + ":" + minute + "");
                    }
                }
                // Set initial time
                , calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE)
                , true).show();
    }

    private void createCustomDatePicker(View view, final TextView result) {
        new DatePopupWindow
                .Builder(MainActivity.this, Calendar.getInstance().getTime(), view)
                .setInitSelect(startGroup, startChild, endGroup, endChild)
                .setInitDay(false)
                .setDateOnClickListener(new DatePopupWindow.DateOnClickListener() {
                    @Override
                    public void getDate(String startDate, String endDate, int startGroupPosition, int startChildPosition, int endGroupPosition, int endChildPosition) {
                        startGroup = startGroupPosition;
                        startChild = startChildPosition;
                        endGroup = endGroupPosition;
                        endChild = endChildPosition;
                        String mStartTime = CalendarUtil.FormatDateYMD(startDate);
                        String mEndTime = CalendarUtil.FormatDateYMD(endDate);
                        result.setText("Selected date from " + mStartTime + " To " + mEndTime);
                    }
                }).builder();
    }
}
