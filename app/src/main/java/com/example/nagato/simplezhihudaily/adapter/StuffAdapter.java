package com.example.nagato.simplezhihudaily.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.example.nagato.simplezhihudaily.R;
import com.example.nagato.simplezhihudaily.db.bean.DailyStuff;
import com.example.nagato.simplezhihudaily.db.bean.Question;
import com.example.nagato.simplezhihudaily.utils.CommonUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nagato hanj
 */
public class StuffAdapter extends RecyclerView.Adapter<StuffAdapter.ViewHolder> {
    private static final String ZHIHU_PACKAGE_ID = "com.zhihu.android";
    private static final String SHARE_FROM = " 分享自知乎网";
    private static final String SHARE_TO="分享至…";
    private static final String MULTIPLE_DISCUSSION = "这里包含多个知乎问题，请点击后选择";

    private List<DailyStuff> mStuffs;
    private ImageLoader mImageLoader=ImageLoader.getInstance();;
    private ImageLoadingListener mAnimateListener = new AnimateFirstDisplayListener();
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.noimage)
            .showImageOnFail(R.drawable.noimage)
            .showImageForEmptyUri(R.drawable.lks_for_blank_url)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();

    public StuffAdapter(List<DailyStuff> mStuffs) {
        this.mStuffs = mStuffs;
        setHasStableIds(true);
    }
    public void updateList(List<DailyStuff> mStuffs){
        this.mStuffs=mStuffs;
        notifyDataSetChanged();
    }

    @Override
    public StuffAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();

        View itemView = LayoutInflater
                .from(context)
                .inflate(R.layout.stuff_list_item, parent, false);

        return new ViewHolder(itemView, new ViewHolder.ClickResponseListener() {
            @Override
            public void onWholeClick(int position) {
                showQuestion(context, position);
            }

            @Override
            public void onOverflowClick(View v, int position) {
                PopupMenu popup = new PopupMenu(context, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.share_list, popup.getMenu());
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.action_share_url:
                            share(context, position);
                            break;
                    }
                    return true;
                });
                popup.show();
            }
        });
    }

    private void showQuestion(Context context, int position) {
        DailyStuff dailyStuff = mStuffs.get(position);

        if (dailyStuff.hasMultipleQuestions()) {
            AlertDialog dialog = createDialog(context,
                    dailyStuff,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                String questionUrl = dailyStuff.getQuestions().get(which).getUrl();
                                goToZhihu(context, questionUrl);
                        }
                    });
            dialog.show();
        } else {
            goToZhihu(context, dailyStuff.getQuestions().get(0).getUrl());
        }
    }
    private AlertDialog createDialog(Context context, DailyStuff dailyStuff, DialogInterface.OnClickListener listener) {

        String[] questionTitles = Stream.of(dailyStuff.getQuestions()).map(Question::getTitle).toArray(String[]::new);

        return new AlertDialog.Builder(context)
                .setTitle(dailyStuff.getDailyTitle())
                .setItems(questionTitles, listener)
                .create();
    }



    private void goToZhihu(Context context, String url) {
        if (CommonUtils.isClientInstalled()) {
            openUsingZhihuClient(context, url);
        } else {
            openUsingBrowser(context, url);
        }
    }
    private void openUsingBrowser(Context context, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        if (CommonUtils.intentAvailable(browserIntent)) {
            context.startActivity(browserIntent);
        } else {
            Toast.makeText(context, context.getString(R.string.no_browser), Toast.LENGTH_SHORT).show();
        }
    }

    private void openUsingZhihuClient(Context context, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        browserIntent.setPackage(ZHIHU_PACKAGE_ID);
        context.startActivity(browserIntent);
    }

    private void share(Context context, int position) {
        DailyStuff dailyStuff = mStuffs.get(position);

        if (dailyStuff.hasMultipleQuestions()) {
            AlertDialog dialog = createDialog(context,
                    dailyStuff,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String questionTitle = dailyStuff.getQuestions().get(which).getTitle(),
                                    questionUrl = dailyStuff.getQuestions().get(which).getUrl();
                            shareQuestion(context, questionTitle, questionUrl);
                        }
                    });
            dialog.show();
        } else {
            shareQuestion(context,
                    dailyStuff.getQuestions().get(0).getTitle(),
                    dailyStuff.getQuestions().get(0).getUrl());
        }
    }
    private void shareQuestion(Context context, String questionTitle, String questionUrl) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_TEXT,
                questionTitle + " " + questionUrl + SHARE_FROM);
        context.startActivity(Intent.createChooser(share, SHARE_TO));
    }

    @Override
    public void onBindViewHolder(StuffAdapter.ViewHolder holder, int position) {
        DailyStuff dailyStuff = mStuffs.get(position);
        mImageLoader.displayImage(dailyStuff.getThumbnailUrl(), holder.newsImage, options, mAnimateListener);

        if (dailyStuff.getQuestions().size() > 1) {
            holder.questionTitle.setText(dailyStuff.getDailyTitle());
            holder.dailyTitle.setText(MULTIPLE_DISCUSSION);
        } else {
            holder.questionTitle.setText(dailyStuff.getQuestions().get(0).getTitle());
            holder.dailyTitle.setText(dailyStuff.getDailyTitle());
        }
    }

    @Override
    public int getItemCount() {
        return mStuffs.size();
    }

    @Override
    public long getItemId(int position) {
        return mStuffs.get(position).hashCode();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView newsImage;
        public TextView questionTitle;
        public TextView dailyTitle;
        public ImageView overflow;

        private ClickResponseListener mClickResponseListener;

        public ViewHolder(View itemView, ClickResponseListener clickResponseListener) {
            super(itemView);

            this.mClickResponseListener = clickResponseListener;

            newsImage = (ImageView) itemView.findViewById(R.id.thumbnail_image);
            questionTitle = (TextView) itemView.findViewById(R.id.question_title);
            dailyTitle = (TextView) itemView.findViewById(R.id.daily_title);
            overflow = (ImageView) itemView.findViewById(R.id.card_share_overflow);

            itemView.setOnClickListener(this);
            overflow.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == overflow) {
                mClickResponseListener.onOverflowClick(v, getAdapterPosition());
            } else {
                mClickResponseListener.onWholeClick(getAdapterPosition());
            }
        }

        public interface ClickResponseListener {
            void onWholeClick(int position);

            void onOverflowClick(View v, int position);
        }
    }

    public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean isFirstDisplay = !displayedImages.contains(imageUri);
                if (isFirstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
