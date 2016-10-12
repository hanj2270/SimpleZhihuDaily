package com.example.nagato.simplezhihudaily.utils;

import android.content.Intent;
import android.content.pm.PackageManager;

import com.example.nagato.simplezhihudaily.global.MyApplication;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by nagato hanj
 */
public class CommonUtils {
    private static final String ZHIHU_PACKAGE_ID = "com.zhihu.android";
    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
    public static boolean isClientInstalled() {
        try {
            return MyApplication.fetchPackageManager().getPackageInfo(ZHIHU_PACKAGE_ID, PackageManager.GET_ACTIVITIES) != null;
        } catch (PackageManager.NameNotFoundException ignored) {
            return false;
        }
    }
    public static boolean intentAvailable(Intent intent){
        return MyApplication.fetchPackageManager().queryIntentActivities(intent,0).size()>0;
    }


}
