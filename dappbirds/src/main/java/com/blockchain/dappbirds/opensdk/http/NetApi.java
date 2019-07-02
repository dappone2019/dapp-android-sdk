package com.blockchain.dappbirds.opensdk.http;

import com.blockchain.dappbirds.opensdk.DappBirdsSdk;
import com.blockchain.dappbirds.opensdk.config.BaseConfig;
import com.blockchain.dappbirds.opensdk.model.BaseDataBean;
import com.blockchain.dappbirds.opensdk.model.BaseModel;
import com.blockchain.dappbirds.opensdk.model.DataBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.util.HashMap;

public class NetApi implements BaseConfig, RequstPath {

    public static void walletStore(HashMap hashMap, JsonCallback<BaseModel> callback) {
        invokePost(getUrl(WALLETSTORE), hashMap, callback);
    }

    public static void orderPay(HashMap hashMap, JsonCallback<BaseModel> callback) {
        invokePost(getUrl(ORDERPAY), hashMap, callback);
    }

    public static void orderCheck(HashMap hashMap, JsonCallback<BaseDataBean> callback) {
        invokePost(getUrl(ORDERCHECK), hashMap, callback);
    }

    public static void getOrderList(String address, String page_size, String page_number, JsonCallback<DataBean> callback) {
        invokeGet(getExplorerUrl("/api/v1/explorer/address/") + address + "/" + page_size + "/" + page_number, callback);
    }

    private static void invokePost(String url, HashMap params, Callback callback) {
        OkHttpUtils.post()
                .url(url)
                .params(params == null ? new HashMap() : params)
                .build()
                .execute(callback);
    }

    private static void invokeGet(String url, Callback callback) {
        OkHttpUtils.get()
                .url(url)
                .build().execute(callback);
    }

    /**
     * 基础Api
     *
     * @param action
     * @return
     */
    public static String getUrl(String action) {
        if (DappBirdsSdk.isEnableTest()) {
            return new StringBuilder(TEST_BASE_URL).append(action).toString();
        } else {
            return new StringBuilder(MAIN_BASE_URL).append(action).toString();
        }
    }

    public static String getExplorerUrl(String action) {
        if (DappBirdsSdk.isEnableTest()) {
            return new StringBuilder(TEST_EXPLORE_URL).append(action).toString();
        } else {
            return new StringBuilder(MAIN_EXPLORE_URL).append(action).toString();
        }
    }
}
