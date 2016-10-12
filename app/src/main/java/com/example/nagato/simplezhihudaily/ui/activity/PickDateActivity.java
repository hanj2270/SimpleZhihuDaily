package com.example.nagato.simplezhihudaily.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.nagato.simplezhihudaily.R;
import com.example.nagato.simplezhihudaily.utils.CommonUtils;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by nagato hanj
 */
public class PickDateActivity extends BaseActivity {
    private static final Date birthday = new Date(13, 4, 19);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutResID = R.layout.activity_pick_date;

        super.onCreate(savedInstanceState);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Calendar nextDay = Calendar.getInstance();
        nextDay.add(Calendar.DAY_OF_YEAR, 1);

        CalendarPickerView calendarPickerView = (CalendarPickerView) findViewById(R.id.calendar_view);

        calendarPickerView.init(birthday, nextDay.getTime())
                .withSelectedDate(Calendar.getInstance().getTime());
        calendarPickerView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_YEAR, 1);

                Intent intent = new Intent(PickDateActivity.this, SingleDateActivity.class);
                intent.putExtra(DATE,
                        CommonUtils.simpleDateFormat.format(calendar.getTime()));
                startActivity(intent);
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
        calendarPickerView.setOnInvalidDateSelectedListener(date -> {
            if (date.after(new Date())) {
                showSnackbar(R.string.not_coming);
            } else {
                showSnackbar(R.string.not_born);
            }
        });
    }
}
