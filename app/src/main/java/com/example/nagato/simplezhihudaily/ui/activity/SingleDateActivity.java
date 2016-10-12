package com.example.nagato.simplezhihudaily.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.nagato.simplezhihudaily.R;
import com.example.nagato.simplezhihudaily.ui.fragment.DailyListFragment;
import com.example.nagato.simplezhihudaily.utils.CommonUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nagato hanj
 */
public class SingleDateActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        Fragment newFragment = new DailyListFragment();

        String dateString = bundle.getString(DATE);
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = CommonUtils.simpleDateFormat.parse(dateString);
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        } catch (ParseException ignored) {

        }

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(DateFormat.getDateInstance().format(calendar.getTime()));

        bundle.putString(DATE, dateString);
        bundle.putBoolean(IS_FIRST_PAGE,
                isSameDay(calendar, Calendar.getInstance()));
        bundle.putBoolean(IS_SINGLE, true);

        newFragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .commit();
    }

    private boolean isSameDay(Calendar first, Calendar second) {
        return first.get(Calendar.YEAR) == second.get(Calendar.YEAR)
                && first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR);
    }
}
