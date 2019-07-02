package com.blockchain.dappbirds.opensdk.view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.blockchain.dappbirds.opensdk.DappBirdsSdk;
import com.blockchain.dappbirds.opensdk.R;
import com.blockchain.dappbirds.opensdk.callback.MainCallback;
import com.blockchain.dappbirds.opensdk.config.BaseConfig;
import com.blockchain.dappbirds.opensdk.db.AbSharedUtil;
import com.blockchain.dappbirds.opensdk.db.Contants;
import com.blockchain.dappbirds.opensdk.http.JsonCallback;
import com.blockchain.dappbirds.opensdk.http.NetApi;
import com.blockchain.dappbirds.opensdk.http.SetPublicParam;
import com.blockchain.dappbirds.opensdk.model.BaseDataBean;
import com.blockchain.dappbirds.opensdk.model.BaseModel;
import com.blockchain.dappbirds.opensdk.ont.ontio.OntSdk;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.Address;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.Helper;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.WalletQR;
import com.blockchain.dappbirds.opensdk.ont.ontio.sdk.wallet.Account;
import com.blockchain.dappbirds.opensdk.ont.ontio.sdk.wallet.Scrypt;
import com.blockchain.dappbirds.opensdk.utils.DecimalUtil;
import com.blockchain.dappbirds.opensdk.utils.Kits;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;

import okhttp3.Call;

import static com.blockchain.dappbirds.opensdk.db.Contants.key_amount;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_contract_address;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_from_address;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_order_no;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_signature;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_timestamp;

public class PayDialog extends Dialog {
    private TextView tvCancel;
    private Button btnSubmit;
    private Dialog loadDialog;
    private Bundle bundle;
    private Context activity;
    private MainCallback mainCallback;
    private String address;
    private PasswordDialog passwordDialog;
    private Timer timer;
    private boolean is_success = false;
    private boolean is_click = false;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String error = (String) msg.obj;
                if (mainCallback != null) {
                    mainCallback.onError(1, error);
                }
            } else if (msg.what == 2) {
                is_success = true;
                if (mainCallback != null) {
                    mainCallback.onSuccess();
                    AbSharedUtil.setPassSuccessTime(activity, System.currentTimeMillis() + "");
                }
            }
            dismissPorcess();
            dismiss();
        }
    };

    public PayDialog(Context context, Bundle bundle, MainCallback mainCallback) {
        super(context, R.style.MyDialog);
        this.activity = context;
        this.mainCallback = mainCallback;
        this.bundle = bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pay);
        initView();
        setListener();
    }

    private void payError(String msg) {
        Message message = new Message();
        message.what = 1;
        message.obj = msg;
        handler.sendMessage(message);
    }

    private void setListener() {
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_click = true;
                payError(activity.getResources().getString(R.string.cancel_pay));
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_click = true;
                String time = AbSharedUtil.getSuccessPayTime(activity);
                if (Kits.Empty.check(time)) {
                    showPasswordDialog();
                } else {
                    long long_time = System.currentTimeMillis() - Long.parseLong(time);
                    if (long_time < 86400000 && !Kits.Empty.check(AbSharedUtil.getPassword(activity))) {
                        unitPay();
                    } else {
                        showPasswordDialog();
                    }
                }
            }
        });
    }

    private void unitPay() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        showDialogLoading();
        final HashMap<String, String> hashMap = SetPublicParam.getInstance().setSomeParam(activity);
        hashMap.put("_timestamp", bundle.getString(key_timestamp));
        hashMap.put("_signature", bundle.getString(key_signature));
        hashMap.put(key_from_address, address);
        hashMap.put(key_order_no, bundle.getString(key_order_no));
        hashMap.put(key_amount, bundle.getString(key_amount));
        hashMap.put(key_contract_address, bundle.getString(key_contract_address));
        NetApi.orderPay(hashMap, new JsonCallback<BaseModel>() {
            @Override
            public void onFail(Call call, Exception e, int id) {
                payError(e.getMessage());
            }

            @Override
            public void onResponse(BaseModel response, int id) {
                if (response.code == 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OntSdk ontSdk = OntSdk.getInstance();
                                if (DappBirdsSdk.isEnableTest()) {
                                    ontSdk.setRestful(BaseConfig.TEST_ONT_URL);
                                } else {
                                    ontSdk.setRestful(BaseConfig.MAIN_ONT_URL);
                                }
                                ontSdk.openWalletFile(activity.getSharedPreferences(Contants.SHARED_PATH + AbSharedUtil.getBaseKey(activity), Context.MODE_PRIVATE));
                                Account account = ontSdk.getWalletMgr().getWallet().getAccount(address);
                                String multiply = DecimalUtil.multiplyWithScale(bundle.getString(key_amount), 1000000000 + "", 0);
                                Scrypt scrypt = new Scrypt();
                                Map map = WalletQR.exportAccountQRCode(scrypt, account);
                                String priKeyFromQrCode = WalletQR.getPriKeyFromQrCode(JSON.toJSONString(map), AbSharedUtil.getPassword(activity));
                                com.blockchain.dappbirds.opensdk.ont.ontio.account.Account sendAcct = new com.blockchain.dappbirds.opensdk.ont.ontio.account.Account(Helper.hexToBytes(priKeyFromQrCode), ontSdk.defaultSignScheme);
                                Address parse = Address.parse(bundle.getString(key_contract_address));
                                String toBase58 = parse.toBase58();
                                ontSdk.neovm().oep4().setContractAddress(bundle.getString(key_contract_address));
                                String transfer = ontSdk.neovm().oep4().sendTransfer(sendAcct, toBase58, Long.parseLong(multiply), sendAcct, 20000, 500, bundle.getString(key_order_no));
                                hashMap.put("txid", transfer);
                                checkResult(hashMap);
                            } catch (Exception e) {
                                if (!Kits.Empty.check(e.getMessage())) {
                                    if (e.getMessage().contains("password")) {
                                        showPasswordDialog();
                                    } else if (e.getMessage().contains("balance")) {
                                        payError(activity.getResources().getString(R.string.empty_balance));
                                    } else {
                                        payError(e.getMessage());
                                    }
                                }
                            }
                        }
                    }).start();
                } else {
                    payError(response.msg);
                }
            }
        });
    }


    private void checkResult(final HashMap hashMap) {
        NetApi.orderCheck(hashMap, new JsonCallback<BaseDataBean>() {
            @Override
            public void onFail(Call call, Exception e, int id) {
                payError(activity.getResources().getString(R.string.netword));
            }

            @Override
            public void onResponse(BaseDataBean response, int id) {
                if (response.code == 0) {
                    if (!Kits.Empty.check(response.data.check_result)) {
                        String result = response.data.check_result;
                        if (!result.equals("TRADE_WAIT")) {
                            if (result.equals("TRADE_SUCCESS")) {
                                handler.sendEmptyMessage(2);
                            } else {
                                payError(response.msg);
                            }
                        } else {
                            checkResult(hashMap);
                        }
                    } else {
                        checkResult(hashMap);
                    }
                } else {
                    payError(response.msg);
                }
            }
        });
    }

    private void showPasswordDialog() {
        dismiss();
        if (passwordDialog != null) {
            if (passwordDialog.isShowing()) {
                passwordDialog.dismiss();
            }
            passwordDialog = null;
        }
        passwordDialog = new PasswordDialog(activity, bundle, mainCallback);
        passwordDialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        TextView myAddress = findViewById(R.id.address);
        tvCancel = findViewById(R.id.tv_cancel);
        TextView amount = findViewById(R.id.amount);
        btnSubmit = findViewById(R.id.btn_submit);
        TextView contractAddress = findViewById(R.id.contract_address);
        TextView orderNo = findViewById(R.id.order_no);
        address = AbSharedUtil.getAddress(activity);
        myAddress.setText(address);
        amount.setText(bundle.getString(key_amount) + "\tONG");
        contractAddress.setText(bundle.getString(key_contract_address));
        orderNo.setText(bundle.getString(key_order_no));
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (!is_success && !is_click) {
            mainCallback.onError(1, activity.getResources().getString(R.string.cancel_pay));
        }
        try {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void show() {
        super.show();
        Objects.requireNonNull(getWindow()).getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(layoutParams);
    }

    private void showDialogLoading() {
        if (loadDialog == null) {
            LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(activity)
                    .setMessage(activity.getResources().getString(R.string.loading))
                    .setCancelable(true)
                    .setCancelOutside(false);
            loadDialog = loadBuilder.create();
        }
        loadDialog.show();
    }

    private void dismissPorcess() {
        if (this.loadDialog != null) {
            if (this.loadDialog.isShowing()) {
                this.loadDialog.dismiss();
            }
            this.loadDialog = null;
        }
    }
}
