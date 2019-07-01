package com.blockchain.dappbirds.opensdk.wallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.blockchain.dappbirds.opensdk.DappBirdsSdk;
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

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;

import static com.blockchain.dappbirds.opensdk.db.Contants.key_address;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_amount;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_app_id;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_chain_type;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_contract_address;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_dialog;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_from;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_from_address;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_openid;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_order_no;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_signature;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_timestamp;

public class DBWalletManager {
    private Context context;
    private PayDialog payDialog;
    private boolean isSHow;
    private String address;
    private String amountnew;
    public static MainCallback callback;

    public DBWalletManager(Context context) {
        this.context = context;
    }

    public void getAccount(AccountCallBack accountCallBack) {
        callback = new MainCallback<AccountCallBack, String>(accountCallBack);
        String app_id = AbSharedUtil.getString(context, key_app_id, "");
        String open_id = AbSharedUtil.getString(context, key_openid, "");
        String chain_type = AbSharedUtil.getString(context, key_chain_type, "");
        if (Kits.Empty.check(app_id)) {
            callback.onError(1, context.getString(R.string.empty_appid));
            AbSharedUtil.putString(context, key_address, "");
            return;
        }
        if (Kits.Empty.check(open_id)) {
            callback.onError(1, context.getString(R.string.empty_open_id));
            AbSharedUtil.putString(context, key_address, "");
            return;
        }
        if (Kits.Empty.check(chain_type)) {
            callback.onError(1, context.getString(R.string.empty_chaintype));
            AbSharedUtil.putString(context, key_address, "");
            return;
        }
        String walletInfo = AbSharedUtil.getString(context, DappBirdsSdk.isEnableTest() + "" + app_id + open_id + chain_type, "");
        if (Kits.Empty.check(walletInfo)) {
            callback.onError(1, context.getResources().getString(R.string.empty_wallet));
            AbSharedUtil.putString(context, key_address, "");
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(walletInfo);
            final String address = jsonObject.getString(key_address);
            HashMap<String, String> hashMap = SetPublicParam.getInstance().setSomeParam(context);
            hashMap.put(key_from_address, address);
            NetApi.walletStore(hashMap, new JsonCallback<BaseModel>() {
                @Override
                public void onFail(Call call, Exception e, int id) {
                    callback.onError(1, e.getMessage());
                    AbSharedUtil.putString(context, key_address, "");
                }

                @Override
                public void onResponse(BaseModel response, int id) {
                    if (response.code == 0) {
                        callback.onSuccess(address);
                        AbSharedUtil.putString(context, key_address, address);
                    } else {
                        callback.onError(response.code, response.msg);
                    }
                }
            });
        } catch (Exception e) {
            callback.onError(e.hashCode(), e.getMessage());
        }
    }

    public void createAccount(Activity activity, AccountCallBack accountCallBack) {
        callback = new MainCallback<AccountCallBack, String>(accountCallBack);
        activity.startActivity(new Intent(activity, HomeActivity.class));
    }

    public void openDetail(Activity activity) {
        address = AbSharedUtil.getString(this.context, key_address, "");
        if (Kits.Empty.check(address)) {
            Toast.makeText(this.context, this.context.getResources().getString(R.string.empty_wallet), Toast.LENGTH_SHORT).show();
            return;
        }
        activity.startActivity(new Intent(activity, BalanceActivity.class));
    }

    public void unitePay(final Activity activity, final String timestamp, final String signature, final String order_no, final String amount, final String contract_address, final PayCallBack payCallBack) {
        address = AbSharedUtil.getString(context, key_address, "");
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
        final View view = activity.findViewById(android.R.id.content);
        view.post(new Runnable() {
            @Override
            public void run() {
                int i = view.getRootView().getHeight() - view.getHeight();
                isSHow = i > 100;
                Bundle bundle = new Bundle();
                bundle.putString(key_timestamp, timestamp);
                bundle.putString(key_signature, signature);
                bundle.putString(key_order_no, order_no);
                bundle.putString(key_amount, amountnew);
                bundle.putString(key_contract_address, contract_address);
                bundle.putString(key_from, key_dialog);
                payDialog = new PayDialog(activity, bundle, isSHow, callback);
                payDialog.show();
            }
        });
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
