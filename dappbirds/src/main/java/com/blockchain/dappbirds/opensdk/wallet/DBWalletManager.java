package com.blockchain.dappbirds.opensdk.wallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.blockchain.dappbirds.opensdk.R;
import com.blockchain.dappbirds.opensdk.callback.MainCallback;
import com.blockchain.dappbirds.opensdk.db.AbSharedUtil;
import com.blockchain.dappbirds.opensdk.http.JsonCallback;
import com.blockchain.dappbirds.opensdk.http.NetApi;
import com.blockchain.dappbirds.opensdk.http.SetPublicParam;
import com.blockchain.dappbirds.opensdk.model.BaseModel;
import com.blockchain.dappbirds.opensdk.ui.activity.BalanceActivity;
import com.blockchain.dappbirds.opensdk.ui.activity.HomeActivity;
import com.blockchain.dappbirds.opensdk.utils.DecimalUtil;
import com.blockchain.dappbirds.opensdk.utils.Kits;
import com.blockchain.dappbirds.opensdk.view.dialog.PayDialog;

import java.util.HashMap;

import okhttp3.Call;

import static com.blockchain.dappbirds.opensdk.db.Contants.key_amount;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_contract_address;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_dialog;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_from;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_from_address;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_order_no;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_signature;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_timestamp;

public class DBWalletManager {
    private Context context;
    private PayDialog payDialog;
    public static MainCallback callback;

    public DBWalletManager(Context context) {
        this.context = context;
    }

    private void checkArgs(MainCallback callback) {
        if (Kits.Empty.check(AbSharedUtil.getAppID(context))) {
            callback.onError(1, context.getString(R.string.empty_appid));
            return;
        }
        if (Kits.Empty.check(AbSharedUtil.getOpenID(context))) {
            callback.onError(1, context.getString(R.string.empty_open_id));
            return;
        }
        if (Kits.Empty.check(AbSharedUtil.getChaintype(context))) {
            callback.onError(1, context.getString(R.string.empty_chaintype));
            return;
        }
    }

    public void getAccount(AccountCallBack accountCallBack) {
        callback = new MainCallback<AccountCallBack, String>(accountCallBack);
        checkArgs(callback);
        final String address = AbSharedUtil.getAddress(context);
        if (Kits.Empty.check(address)) {
            callback.onError(1, context.getResources().getString(R.string.empty_wallet));
            return;
        }
        HashMap<String, String> hashMap = SetPublicParam.getInstance().setSomeParam(context);
        hashMap.put(key_from_address, address);
        NetApi.walletStore(hashMap, new JsonCallback<BaseModel>() {
            @Override
            public void onFail(Call call, Exception e, int id) {
                callback.onError(1, e.getMessage());
            }

            @Override
            public void onResponse(BaseModel response, int id) {
                if (response.code == 0) {
                    callback.onSuccess(address);
                    AbSharedUtil.setAddress(context, address);
                } else {
                    callback.onError(response.code, response.msg);
                }
            }
        });
    }

    public void createAccount(Activity activity, AccountCallBack accountCallBack) {
        callback = new MainCallback<AccountCallBack, String>(accountCallBack);
        checkArgs(callback);
        activity.startActivity(new Intent(activity, HomeActivity.class));
    }

    public void openDetail(Activity activity) {
        String address = AbSharedUtil.getAddress(context);
        if (Kits.Empty.check(address)) {
            Toast.makeText(this.context, this.context.getResources().getString(R.string.empty_wallet), Toast.LENGTH_SHORT).show();
            return;
        }
        activity.startActivity(new Intent(activity, BalanceActivity.class));
    }

    public void unitePay(final Activity activity, final String timestamp, final String signature, final String order_no, final String amount, final String contract_address, final PayCallBack payCallBack) {
        String address = AbSharedUtil.getAddress(context);
        if (Kits.Empty.check(address)) {
            Toast.makeText(context, context.getResources().getString(R.string.empty_wallet), Toast.LENGTH_SHORT).show();
            return;
        }
        if (Kits.Empty.check(timestamp)) {
            Toast.makeText(context, context.getResources().getString(R.string.empty_timestamp), Toast.LENGTH_SHORT).show();
            return;
        }
        if (Kits.Empty.check(signature)) {
            Toast.makeText(context, context.getResources().getString(R.string.empty_signature), Toast.LENGTH_SHORT).show();
            return;
        }
        if (Kits.Empty.check(order_no)) {
            Toast.makeText(context, context.getResources().getString(R.string.empty_order), Toast.LENGTH_SHORT).show();
            return;
        }
        if (Kits.Empty.check(amount)) {
            Toast.makeText(context, context.getResources().getString(R.string.empty_amount), Toast.LENGTH_SHORT).show();
            return;
        }
        if (Kits.Empty.check(contract_address)) {
            Toast.makeText(context, context.getResources().getString(R.string.empty_contracr_address), Toast.LENGTH_SHORT).show();
            return;
        }
        if (timestamp.length() != 10) {
            Toast.makeText(context, context.getResources().getString(R.string.error_timestamp), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Long.parseLong(timestamp);
        } catch (Exception e) {
            Toast.makeText(context, context.getResources().getString(R.string.error_timestamp), Toast.LENGTH_SHORT).show();
            return;
        }
        if (signature.length() != 32) {
            Toast.makeText(context, context.getResources().getString(R.string.error_signature), Toast.LENGTH_SHORT).show();
            return;
        }
        String amountnew;
        try {
            amountnew = amount;
            String[] split = amountnew.split("\\.");
            if (split.length > 2) {
                Toast.makeText(context, context.getResources().getString(R.string.error_amount), Toast.LENGTH_SHORT).show();
                return;
            }
            if (split.length == 2) {
                int length = split[1].length();
                if (length > 9) {
                    split[1] = split[1].substring(0, 9);
                }
                amountnew = split[0] + "." + split[1];
            }
            String multiply = DecimalUtil.multiplyWithScale(amountnew + "", 1000000000 + "", 0);
            if (Kits.Empty.check(multiply)) {
                Toast.makeText(context, context.getResources().getString(R.string.error_amount), Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (Long.parseLong(multiply) < 1) {
                    Toast.makeText(context, context.getResources().getString(R.string.small), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, context.getResources().getString(R.string.error_amount), Toast.LENGTH_SHORT).show();
            return;
        }
        if (contract_address.length() != 40) {
            Toast.makeText(context, context.getResources().getString(R.string.error_contract_address), Toast.LENGTH_SHORT).show();
            return;
        }
        callback = new MainCallback<PayCallBack, String>(payCallBack);
        if (payDialog != null) {
            if (payDialog.isShowing()) {
                payDialog.dismiss();
            }
            payDialog = null;
        }
        Bundle bundle = new Bundle();
        bundle.putString(key_timestamp, timestamp);
        bundle.putString(key_signature, signature);
        bundle.putString(key_order_no, order_no);
        bundle.putString(key_amount, amountnew);
        bundle.putString(key_contract_address, contract_address);
        bundle.putString(key_from, key_dialog);
        payDialog = new PayDialog(activity, bundle, callback);
        payDialog.show();
    }

    public interface AccountCallBack {
        void onError(int errCode, String errInfo);

        void onSuccess(String addr);
    }

    public interface PayCallBack {
        void onError(int errCode, String errInfo);

        void onSuccess();
    }

    public static MainCallback getCallback() {
        return callback;
    }
}
