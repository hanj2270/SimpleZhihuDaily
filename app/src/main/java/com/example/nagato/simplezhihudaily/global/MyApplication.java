package com.example.nagato.simplezhihudaily.global;

import android.app.Application;
import android.content.pm.PackageManager;

import com.example.nagato.simplezhihudaily.db.Dailydatabase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by nagato
 */
public class MyApplication extends Application {
    private static PackageManager mpackageManager;
    private static Dailydatabase mDailydatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mpackageManager=this.getPackageManager();
        mDailydatabase=new Dailydatabase(getApplicationContext());
        initImageLoaderConfiguration();
    }

    private void initImageLoaderConfiguration(){
        ImageLoaderConfiguration configuration=new ImageLoaderConfiguration.Builder(this)
                .denyCacheImageMultipleSizesInMemory()
                .threadPriority(Thread.NORM_PRIORITY-2)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(configuration);
    }


    public static PackageManager fetchPackageManager() {
        return mpackageManager;
    }

    public static Dailydatabase getmDailydatabase() {
        return mDailydatabase;
    }


}
