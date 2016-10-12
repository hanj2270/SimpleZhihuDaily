package com.example.nagato.simplezhihudaily.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.nagato.simplezhihudaily.R;
import com.example.nagato.simplezhihudaily.ui.fragment.DailyListFragment;
import com.example.nagato.simplezhihudaily.utils.CommonUtils;

import java.text.DateFormat;
import java.util.Calendar;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutResID = R.layout.activity_main;
        super.onCreate(savedInstanceState);

        TabLayout tabs = (TabLayout) findViewById(R.id.main_pager_tabs);
        ViewPager viewPager = (ViewPager) findViewById(R.id.main_pager);
        viewPager.setOffscreenPageLimit(PAGE_COUNT);
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_pick_date);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareIntent(PickDateActivity.class);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_about:
                showAboutDialog();
                return true;
            case R.id.action_go_to_search:
                return prepareIntent(SearchActivity.class);
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private class MainPagerAdapter extends FragmentStatePagerAdapter {
        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Bundle bundle = new Bundle();
            Fragment newFragment = new DailyListFragment();

            Calendar dateToGetUrl = Calendar.getInstance();
            dateToGetUrl.add(Calendar.DAY_OF_YEAR, 1 - i);
            String date = CommonUtils.simpleDateFormat.format(dateToGetUrl.getTime());

            bundle.putString(DATE, date);
            bundle.putBoolean(IS_FIRST_PAGE, i == 0);
            bundle.putBoolean(IS_SINGLE, false);

            newFragment.setArguments(bundle);
            return newFragment;
        }
        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Calendar displayDate = Calendar.getInstance();
            displayDate.add(Calendar.DAY_OF_YEAR, -position);

            return (position == 0 ? getString(R.string.zhihu_daily_today): "")
                    + DateFormat.getDateInstance().format(displayDate.getTime());
        }
    }
    private boolean prepareIntent(Class clazz) {
        startActivity(new Intent(MainActivity.this, clazz));
        return true;
    }

    private void showAboutDialog() {
        final Dialog AboutDialog = new Dialog(MainActivity.this);
        AboutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        AboutDialog.setCancelable(true);
        AboutDialog.setContentView(R.layout.about_dialog);

        TextView textView = (TextView) AboutDialog.findViewById(R.id.dialog_text);

        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.about_header)).append("\n");

        String[] ThanksFor = getResources().getStringArray(R.array.thanks_for);

        for (String str : ThanksFor) {
            sb.append("• ").append(str).append("\n");
        }

        textView.setText(sb.toString());

        Button closeDialogButton = (Button) AboutDialog.findViewById(R.id.close_dialog_button);

        closeDialogButton.setOnClickListener(view -> AboutDialog.dismiss());
        AboutDialog.show();
    }
}
