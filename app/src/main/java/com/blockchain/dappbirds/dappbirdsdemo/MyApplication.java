package com.blockchain.dappbirds.dappbirdsdemo;


import android.app.Application;

import com.blockchain.dappbirds.opensdk.DappBirdsSdk;
import com.blockchain.dappbirds.opensdk.wallet.DBWalletManager;

public class MyApplication extends Application {

    public static DappBirdsSdk dappBirdsSdk;
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
        dappBirdsSdk.setAppId("10027");
        /**
         * 设置openID
         */
        dappBirdsSdk.setOpenId("7S4jnunbmyKzVKDgDQRP4hWstL5HiKZEV");
        /**
         * 设置公链类型，目前仅支持本体 7
         */
        dappBirdsSdk.setChainType("7");
        /**
         * 是否打印日志
         */
        dappBirdsSdk.setEnableLog(false);
        /**
         * 是否是测试网络
         */
        dappBirdsSdk.setEnableTest(false);
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
