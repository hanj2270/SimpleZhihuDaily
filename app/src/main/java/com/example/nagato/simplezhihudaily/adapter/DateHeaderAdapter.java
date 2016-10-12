package com.example.nagato.simplezhihudaily.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;
import com.example.nagato.simplezhihudaily.R;
import com.example.nagato.simplezhihudaily.db.bean.DailyStuff;
import com.example.nagato.simplezhihudaily.utils.CommonUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

/**
 * Created by nagato hanj
 */
public class DateHeaderAdapter implements StickyHeadersAdapter<DateHeaderAdapter.ViewHolder> {
    private List<DailyStuff> mStuffs;
    private DateFormat dateFormat = DateFormat.getDateInstance();


    public DateHeaderAdapter(List<DailyStuff> mStuffs) {
        this.mStuffs = mStuffs;
    }

    public void setmStuffs(List<DailyStuff> mStuffs) {
        this.mStuffs = mStuffs;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.date_header_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Calendar calendar = Calendar.getInstance();

        try {
            calendar.setTime(CommonUtils.simpleDateFormat.parse(mStuffs.get(position).getDate()));
            calendar.add(Calendar.DAY_OF_YEAR, -1);//日报信息标记时间为发表时间第二天
        } catch (ParseException ignored) {

        }

        viewHolder.title.setText(dateFormat.format(calendar.getTime()));
    }


    @Override
    public long getHeaderId(int position) {
        return mStuffs.get(position).getDate().hashCode();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.date_text);
        }
    }
}
