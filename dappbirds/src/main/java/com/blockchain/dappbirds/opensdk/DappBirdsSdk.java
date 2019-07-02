package com.blockchain.dappbirds.opensdk;

import android.content.Context;
import android.util.Log;

import com.blockchain.dappbirds.opensdk.db.AbSharedUtil;
import com.blockchain.dappbirds.opensdk.http.RequstInterceptor;
import com.blockchain.dappbirds.opensdk.utils.Kits;
import com.blockchain.dappbirds.opensdk.wallet.DBWalletManager;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class DappBirdsSdk {
    private static final String TAG = "DappBirdsSdk";
    private static DappBirdsSdk dappBirdsSdk;
    private static Context currentContext;
    private static boolean isTest = true;
    private static boolean isLog = true;
    private DBWalletManager dbWalletManager;

    public static DappBirdsSdk initSDK(Context context) {
        currentContext = context;
        initData(context);
        if (dappBirdsSdk == null) {
            dappBirdsSdk = new DappBirdsSdk();
        }
        return dappBirdsSdk;
    }

    private static void initData(Context context) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new RequstInterceptor())
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);
        String string = context.getString(R.string.welcome);
        if (isTest) {
            string = string.replace("content", context.getString(R.string.debug));
        } else {
            string = string.replace("content", context.getString(R.string.main));
        }
        if (isLog) {
            string = string.replace("state", context.getString(R.string.open));
        } else {
            string = string.replace("state", context.getString(R.string.close));
        }
        Log.e(TAG, string);
    }

    public DBWalletManager getDbWalletManager() {
        if (dbWalletManager == null) {
            dbWalletManager = new DBWalletManager(currentContext);
        }
        return dbWalletManager;
    }

    public static boolean isEnableTest() {
        return isTest;
    }

    public static boolean isEnableLog() {
        return isLog;
    }

    public static Context getContext() {
        return currentContext;
    }

    public void setAppId(String app_id) {
        String appID = AbSharedUtil.getAppID(currentContext);
        if (Kits.Empty.check(appID)) {
            AbSharedUtil.setPassword(currentContext, "");
        } else {
            if (!appID.equals(app_id)) {
                AbSharedUtil.setPassword(currentContext, "");
            }
        }
        AbSharedUtil.setAppID(currentContext, app_id);
    }

    public void setOpenId(String openId) {
        String openID = AbSharedUtil.getOpenID(currentContext);
        if (Kits.Empty.check(openID)) {
            AbSharedUtil.setPassword(currentContext, "");
        } else {
            if (!openID.equals(openId)) {
                AbSharedUtil.setPassword(currentContext, "");
            }
        }
        AbSharedUtil.setOpenID(currentContext, openId);
    }

    public void setChainType(String chainType) {
        String chaintype = AbSharedUtil.getChaintype(currentContext);
        if (Kits.Empty.check(chaintype)) {
            AbSharedUtil.setPassword(currentContext, "");
        } else {
            if (!chaintype.equals(chainType)) {
                AbSharedUtil.setPassword(currentContext, "");
            }
        }
        AbSharedUtil.setChainType(currentContext, chainType);
    }

    public void setEnableTest(boolean enableTest) {
        String enableTest1 = AbSharedUtil.getEnableTest(currentContext);
        if (Kits.Empty.check(enableTest1)) {
            AbSharedUtil.setPassword(currentContext, "");
        } else {
            if (enableTest) {
                if (!enableTest1.equals("1")) {
                    AbSharedUtil.setPassword(currentContext, "");
                }
            } else {
                if (!enableTest1.equals("0")) {
                    AbSharedUtil.setPassword(currentContext, "");
                }
            }
        }
        isTest = enableTest;
        if (enableTest) {
            AbSharedUtil.setEanbleTest(currentContext, "1");
        } else {
            AbSharedUtil.setEanbleTest(currentContext, "0");
        }
    }

    public void setEnableLog(boolean enableLog) {
        isLog = enableLog;
    }
}
