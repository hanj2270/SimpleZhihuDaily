package com.example.nagato.simplezhihudaily.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.example.nagato.simplezhihudaily.R;
import com.example.nagato.simplezhihudaily.adapter.DateHeaderAdapter;
import com.example.nagato.simplezhihudaily.adapter.StuffAdapter;
import com.example.nagato.simplezhihudaily.db.bean.DailyStuff;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nagato hanj
 */
public class SearchFragment extends Fragment {
    private List<DailyStuff> mStuffs=new ArrayList<DailyStuff>();
    private StuffAdapter mAdapter;
    private DateHeaderAdapter mDateHeaderAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search,container,false);

        RecyclerView mRecyclerView=(RecyclerView)view.findViewById(R.id.search_result_list);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mAdapter = new StuffAdapter(mStuffs);
        mDateHeaderAdapter = new DateHeaderAdapter(mStuffs);

        StickyHeadersItemDecoration header = new StickyHeadersBuilder()
                .setAdapter(mAdapter)
                .setRecyclerView(mRecyclerView)
                .setStickyHeadersAdapter(mDateHeaderAdapter)
                .build();

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(header);

        return view;
    }

    public void updateContent(List<DailyStuff> dailyStuffList) {
        mDateHeaderAdapter.setmStuffs(dailyStuffList);
        mAdapter.updateList(dailyStuffList);
    }
}
