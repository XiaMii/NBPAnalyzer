package com.example.nbpanalyzer.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;

import com.example.nbpanalyzer.R;

public class CalendarActivity extends AppCompatActivity {
    public static String CALENDAR_DATE = "Calendar_DATE";
    private CalendarView calendarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Intent intent = new Intent();
                if(month<10){
                    if(dayOfMonth<10){
                        intent.putExtra(CALENDAR_DATE,year+"-0"+(month+1)+"-0"+dayOfMonth);
                    }else {
                        intent.putExtra(CALENDAR_DATE,year+"-0"+(month+1)+"-"+dayOfMonth);
                    }
                }
                else if (dayOfMonth<10){
                    intent.putExtra(CALENDAR_DATE,year+"-"+(month+1)+"-0"+dayOfMonth);
                }

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

    }

}
