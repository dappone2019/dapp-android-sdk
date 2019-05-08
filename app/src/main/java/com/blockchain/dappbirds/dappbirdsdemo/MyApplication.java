package com.blockchain.dappbirds.dappbirdsdemo;


import android.app.Application;
import android.content.Context;

import com.blockchain.dappbirds.opensdk.DappBirdsSdk;
import com.blockchain.dappbirds.opensdk.wallet.DBWalletManager;

public class MyApplication extends Application {

    public static DBWalletManager dbWalletManager;

    @Override
    public void onCreate() {
        super.onCreate();
        DappBirdsSdk.setIs_debug(true);
        dbWalletManager = DappBirdsSdk.create("1", "5X9qHSUPQgrZQ84mD188sd9kD3WkQN8vx", "7", this).getDbWalletManager();
    }
}
