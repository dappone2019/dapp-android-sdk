package com.blockchain.dappbirds.dappbirdsdemo;


import android.annotation.SuppressLint;
import android.app.Application;

import com.blockchain.dappbirds.opensdk.DappBirdsSdk;
import com.blockchain.dappbirds.opensdk.wallet.DBWalletManager;


public class MyApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    public static DappBirdsSdk dappBirdsSdk;
    @SuppressLint("StaticFieldLeak")
    public static DBWalletManager dbWalletManager;

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * SDK初始化
         */
        initSDK();
        /**
         * 设置数据
         */
        setInitData();
        /**
         * 得到钱包管理类
         */
        getDBWallet();
    }

    private void getDBWallet() {
        dbWalletManager = dappBirdsSdk.getDbWalletManager();
    }

    private void setInitData() {
        /**
         * 设置appId
         */
        dappBirdsSdk.setAppId("39");
//        dappBirdsSdk.setAppId("1");
        /**
         * 设置openID
         */
        dappBirdsSdk.setOpenId("bToKcBXMarLnVxdP4AdVaGg7jHcxQfjic");
//        dappBirdsSdk.setOpenId("5X9qHSUPQgrZQ84mD188sd9kD3WkQN8vx");
        /**
         * 设置公链类型，目前仅支持本体 7
         */
        dappBirdsSdk.setChainType("10");
//        dappBirdsSdk.setChainType("7");
        /**
         * 是否打印日志
         */
        dappBirdsSdk.setEnableLog(true);
        /**
         * 是否是测试网络
         */
        dappBirdsSdk.setEnableTest(true);
    }

    /**
     * 初始化SDK
     */
    public void initSDK() {
        /**
         * @context : Context
         */
        dappBirdsSdk = DappBirdsSdk.initSDK(this);
    }

    public static void exit() {
        System.exit(0);
    }
}
