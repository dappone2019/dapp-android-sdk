package com.blockchain.dappbirds.opensdk;

import android.content.Context;
import android.util.Log;

import com.blockchain.dappbirds.opensdk.db.AbSharedUtil;
import com.blockchain.dappbirds.opensdk.http.RequstInterceptor;
import com.blockchain.dappbirds.opensdk.wallet.DBWalletManager;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.blockchain.dappbirds.opensdk.db.Contants.key_app_id;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_chain_type;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_openid;

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
        AbSharedUtil.putString(currentContext, key_app_id, app_id);
    }

    public void setOpenId(String openId) {
        AbSharedUtil.putString(currentContext, key_openid, openId);
    }

    public void setChainType(String chainType) {
        AbSharedUtil.putString(currentContext, key_chain_type, chainType);
    }

    public void setEnableTest(boolean enableTest) {
        isTest = enableTest;
    }

    public void setEnableLog(boolean enableLog) {
        isLog = enableLog;
    }
}
