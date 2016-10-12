package com.example.nagato.simplezhihudaily.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.example.nagato.simplezhihudaily.R;
import com.example.nagato.simplezhihudaily.db.bean.DailyStuff;
import com.example.nagato.simplezhihudaily.observable.DataSourseObservable;
import com.example.nagato.simplezhihudaily.ui.fragment.SearchFragment;
import com.example.nagato.simplezhihudaily.ui.widget.MySearchView;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by nagato hanj
 */
public class SearchActivity extends BaseActivity implements Observer<List<DailyStuff>> {
    private MySearchView searchView;
    private SearchFragment searchFragment;
    private ProgressDialog dialog;

    private Subscription searchSubscription;
    private List<DailyStuff> mStuffs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initDialog();

        searchFragment = new SearchFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, searchFragment)
                .commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        searchFragment = null;

        super.onDestroy();
    }

    private void initView() {
        searchView = new MySearchView(this);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(query -> {;
            searchView.clearFocus();
            searchSubscription = DataSourseObservable.dailyStuffFromSearch(query)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(this::onSubscribe)
                    .doOnUnsubscribe(this::onUnsubscribe)
                    .subscribe(this);
            return true;
        });

        RelativeLayout relative = new RelativeLayout(this);
        relative.addView(searchView);

        mToolBar.addView(relative);

        setSupportActionBar(mToolBar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initDialog() {
        dialog = new ProgressDialog(SearchActivity.this);
        dialog.setMessage(getString(R.string.is_searching));
        dialog.setCancelable(true);
        dialog.setOnCancelListener(dialog -> {
            if (searchSubscription != null && !searchSubscription.isUnsubscribed()) {
                searchSubscription.unsubscribe();
            }
        });
    }

    private void onSubscribe() {
        dialog.show();
    }

    private void onUnsubscribe() {
        dialog.dismiss();
    }


    @Override
    public void onCompleted() {
        dialog.dismiss();
        searchFragment.updateContent(mStuffs);
    }

    @Override
    public void onError(Throwable e) {
        dialog.dismiss();
        showSnackbar(R.string.no_result_found);
    }

    @Override
    public void onNext(List<DailyStuff> dailyStuffList) {
        this.mStuffs=dailyStuffList;
        searchFragment.updateContent(mStuffs);
        }
}
