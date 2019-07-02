package com.blockchain.dappbirds.opensdk.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class PhoneInfo {
    /*
     * 获取版本号
     */
    public static int getVersionCode(Context context) {
        PackageManager pManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pManager.getPackageInfo(context.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo.versionCode;
    }
}