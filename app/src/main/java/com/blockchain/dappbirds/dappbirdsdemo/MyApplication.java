package com.blockchain.dappbirds.dappbirdsdemo;


import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.blockchain.dappbirds.opensdk.DappBirdsSdk;
import com.blockchain.dappbirds.opensdk.wallet.DBWalletManager;

public class MyApplication extends Application {

    public static DBWalletManager dbWalletManager;

    @Override
    public void onCreate() {
        super.onCreate();
        initSDK();
    }

    /**
     * 初始化SDK
     */
    public void initSDK() {
        /**
         * @Boolean: 切换主网络和测试网
         */
        DappBirdsSdk.setIs_debug(true);
        /**
         * 是否打印日志
         */
        DappBirdsSdk.setEnableLog(true);
        /**
         * @app_id: 平台分配的app_id
         * @openid: 平台分配的openid
         * @chain_type: 公链的类型
         * @context : Context
         */
        DappBirdsSdk dappBirdsSdk = DappBirdsSdk.create("1", "5X9qHSUPQgrZQ84mD188sd9kD3WkQN8vx", "7", this);
        /**
         * 钱包管理类
         */
        dbWalletManager = dappBirdsSdk.getDbWalletManager();
    }

    public static void exit() {
        System.exit(0);
    }
}
