package com.blockchain.dappbirds.opensdk.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.blockchain.dappbirds.opensdk.DappBirdsSdk;

public class AbSharedUtil {

    public static String SHARED_PATH = Contants.SHARED_PATH;

    private static SharedPreferences sharedPreferences;

    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PATH, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static void setOpenID(Context context, String openid) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        Editor edit = sharedPreferences.edit();
        edit.putString(Contants.key_openid, openid);
        edit.apply();
    }

    public static String getOpenID(Context context) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getString(Contants.key_openid, "");
    }

    public static void setAppID(Context context, String appid) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        Editor edit = sharedPreferences.edit();
        edit.putString(Contants.key_app_id, appid);
        edit.apply();
    }

    public static String getAppID(Context context) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getString(Contants.key_app_id, "");
    }

    public static void setChainType(Context context, String chain_type) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        Editor edit = sharedPreferences.edit();
        edit.putString(Contants.key_chain_type, chain_type);
        edit.apply();
    }

    public static String getChaintype(Context context) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getString(Contants.key_chain_type, "");
    }

    public static String getBaseKey(Context context) {
        String appID = getAppID(context);
        String openID = getOpenID(context);
        String chaintype = getChaintype(context);
        return appID + openID + chaintype + DappBirdsSdk.isEnableTest();
    }

    public static void setPassword(Context context, String password) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        Editor edit = sharedPreferences.edit();
        edit.putString(Contants.key_pass + getBaseKey(context), password);
        edit.apply();
    }

    public static String getPassword(Context context) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getString(Contants.key_pass + getBaseKey(context), "");
    }

    public static void setAddress(Context context, String adderss) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        Editor edit = sharedPreferences.edit();
        edit.putString(Contants.key_address + getBaseKey(context), adderss);
        edit.apply();
    }

    public static String getAddress(Context context) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getString(Contants.key_address + getBaseKey(context), "");
    }

    public static void setPassSuccessTime(Context context, String time) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        Editor edit = sharedPreferences.edit();
        edit.putString(Contants.pay_success_time + getBaseKey(context), time);
        edit.apply();
    }

    public static String getSuccessPayTime(Context context) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getString(Contants.pay_success_time + getBaseKey(context), "");
    }

    public static void setMnemonic(Context context, String value) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        Editor edit = sharedPreferences.edit();
        edit.putString(Contants.key_mnemonic + getBaseKey(context) + getAddress(context), value);
        edit.apply();
    }

    public static String getMnemonic(Context context) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getString(Contants.key_mnemonic + getBaseKey(context) + getAddress(context), "");
    }

    public static void setCreateType(Context context, String type) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        Editor edit = sharedPreferences.edit();
        edit.putString(Contants.create_type + getBaseKey(context), type);
        edit.apply();
    }

    public static String getCreateType(Context context) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getString(Contants.create_type + getBaseKey(context), "");
    }

    public static void setEanbleTest(Context context, String val) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        Editor edit = sharedPreferences.edit();
        edit.putString("test", val);
        edit.apply();
    }

    public static String getEnableTest(Context context) {
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getString("test", "");
    }
}
