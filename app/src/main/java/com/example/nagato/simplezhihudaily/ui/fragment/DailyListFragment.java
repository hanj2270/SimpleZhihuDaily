package com.example.nagato.simplezhihudaily.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nagato.simplezhihudaily.R;
import com.example.nagato.simplezhihudaily.adapter.StuffAdapter;
import com.example.nagato.simplezhihudaily.db.SaveNewsListTask;
import com.example.nagato.simplezhihudaily.db.bean.DailyStuff;
import com.example.nagato.simplezhihudaily.observable.DataSourseObservable;
import com.example.nagato.simplezhihudaily.ui.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by nagato hanj
 */
public class DailyListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Observer<List<DailyStuff>> {
    private static final String DATE = "date";
    private static final String IS_FIRST_PAGE = "first_page?";
    private List<DailyStuff> mStuffs=new ArrayList<>();
    private String date;
    private boolean isFirstPage;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTextView;
    private StuffAdapter mAdapter;
    private boolean isRefreshed;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if(savedInstanceState==null){
            Bundle bundle=getArguments();
            date=bundle.getString(DATE);
            isFirstPage=bundle.getBoolean(IS_FIRST_PAGE);
            setRetainInstance(true);
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_stuff_list,container,false);
        mTextView= (TextView) view.findViewById(R.id.swipe_hint);

        RecyclerView recyclerView=(RecyclerView)view.findViewById(R.id.stuff_list);
        recyclerView.setHasFixedSize(!isFirstPage);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        mAdapter=new StuffAdapter(mStuffs);
        recyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        return view;
    }

    @Override
    public void onResume() {
        DataSourseObservable.dailyStuffFromDatabase(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
        super.onResume();
    }

    @Override
    public void onRefresh() {
        if(isRefreshed&&!isFirstPage){
            mSwipeRefreshLayout.setRefreshing(false);
            ((BaseActivity)getActivity()).showSnackbar(R.string.is_refreshed);
        }
        else {
            mSwipeRefreshLayout.setRefreshing(true);
            DataSourseObservable.dailyStuffFromZhihu(date)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this);
        }
    }

    @Override
    public void onCompleted() {
        mSwipeRefreshLayout.setRefreshing(false);
        mTextView.setVisibility(View.GONE);
        isRefreshed= true;
        mAdapter.updateList(mStuffs);
        new SaveNewsListTask(mStuffs).execute();
    }

    @Override
    public void onError(Throwable e) {
        mSwipeRefreshLayout.setRefreshing(false);
        mTextView.setText(R.string.network_io);
    }

    @Override
    public void onNext(List<DailyStuff> dailyStuffList) {
        mStuffs=dailyStuffList;
    }
}
