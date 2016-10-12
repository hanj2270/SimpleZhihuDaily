package com.example.nagato.simplezhihudaily.ui.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.nagato.simplezhihudaily.R;

/**
 * Created by nagato hanj
 */
public class BaseActivity extends AppCompatActivity {
    protected CoordinatorLayout mCoordinatorLayout;
    protected Toolbar mToolBar;
    protected int layoutResID = R.layout.activity_base;

    protected static final String DATE = "date";
    protected static final String IS_SINGLE = "single?";
    protected static final String IS_FIRST_PAGE = "first_page?";
    protected static final int PAGE_COUNT = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResID);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void showSnackbar(int resId) {
        Snackbar.make(mCoordinatorLayout, resId, Snackbar.LENGTH_SHORT).show();
    }
}
