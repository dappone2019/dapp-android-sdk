package com.blockchain.dappbirds.opensdk.http;

public interface RequstPath {
    //保存openid和钱包地址
    String WALLETSTORE = "/app/wallet/store";
    //统一下单
    String ORDERPAY = "/trade/order/apply";
    //查询支付状态
    String ORDERCHECK = "/trade/order/check";
}
