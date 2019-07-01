package com.blockchain.dappbirds.opensdk.ont.ontio.common;

import android.util.Base64;

import com.alibaba.fastjson.JSON;
import com.blockchain.dappbirds.opensdk.ont.ontio.crypto.SignatureScheme;
import com.blockchain.dappbirds.opensdk.ont.ontio.sdk.exception.SDKException;
import com.blockchain.dappbirds.opensdk.ont.ontio.sdk.wallet.Account;
import com.blockchain.dappbirds.opensdk.ont.ontio.sdk.wallet.Control;
import com.blockchain.dappbirds.opensdk.ont.ontio.sdk.wallet.Identity;
import com.blockchain.dappbirds.opensdk.ont.ontio.sdk.wallet.Scrypt;
import com.blockchain.dappbirds.opensdk.ont.ontio.sdk.wallet.Wallet;

import java.util.HashMap;
import java.util.Map;

public class WalletQR {
    public static Map exportIdentityQRCode(Wallet walletFile, Identity identity) throws Exception {
        Control control = identity.controls.get(0);
        String address = identity.ontid.substring(8);
        Map map = new HashMap();
        map.put("type", "I");
        map.put("label",identity.label);
        map.put("key", control.key);
        map.put("parameters", control.parameters);
        map.put("algorithm", "ECDSA");
        map.put("scrypt", walletFile.getScrypt());
        map.put("address",address);
        map.put("salt", control.salt);
        return map;
    }
    public static Map exportIdentityQRCode(Scrypt scrypt, Identity identity) throws Exception {
        if(scrypt==null || identity == null){
            throw new SDKException(ErrorCode.ParamErr("scrypt or identity should not be null"));
        }
        Control control = identity.controls.get(0);
        String address = identity.ontid.substring(8);
        Map map = new HashMap();
        map.put("type", "I");
        map.put("label",identity.label);
        map.put("key", control.key);
        map.put("parameters", control.parameters);
        map.put("algorithm", "ECDSA");
        map.put("scrypt", scrypt);
        map.put("address",address);
        map.put("salt", control.salt);
        return map;
    }
    public static Map exportAccountQRCode(Wallet walletFile, Account account) throws Exception {
        Map map = new HashMap();
        map.put("type", "A");
        map.put("label", account.label);
        map.put("key", account.key);
        map.put("parameters", account.parameters);
        map.put("algorithm", "ECDSA");
        map.put("scrypt", walletFile.getScrypt());
        map.put("address",account.address);
        map.put("salt", account.salt);
        return map;
    }
    public static Map exportAccountQRCode(Scrypt scrypt, Account account) throws Exception {
        if(scrypt==null || account == null){
            throw new SDKException(ErrorCode.ParamErr("scrypt or account should not be null"));
        }
        Map map = new HashMap();
        map.put("type", "A");
        map.put("label", account.label);
        map.put("key", account.key);
        map.put("parameters", account.parameters);
        map.put("algorithm", "ECDSA");
        map.put("scrypt", scrypt);
        map.put("address",account.address);
        map.put("salt", account.salt);
        return map;
    }
    public static String getPriKeyFromQrCode(String qrcode,String password) throws SDKException {
        if(qrcode==null || qrcode.equals("") || password == null || password.equals("")){
            throw new SDKException(ErrorCode.ParamErr("qrcode or password should not be null"));
        }
        Map map = JSON.parseObject(qrcode,Map.class);
        String key = (String)map.get("key");
        String address = (String)map.get("address");
        String salt = (String)map.get("salt");
        int n = (int)((Map)map.get("scrypt")).get("n");
        try {
            return com.blockchain.dappbirds.opensdk.ont.ontio.account.Account.getGcmDecodedPrivateKey(key,password,address, Base64.decode(salt,Base64.NO_WRAP),n, SignatureScheme.SHA256WITHECDSA);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SDKException(ErrorCode.OtherError("password and qrcode not match"));
        }
    }
}
