package com.blockchain.dappbirds.opensdk.view.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.blockchain.dappbirds.opensdk.utils.GeneralUtils;
import com.blockchain.dappbirds.opensdk.utils.Kits;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

import static com.blockchain.dappbirds.opensdk.db.Contants.key_amount;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_contract_address;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_from;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_from_address;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_order_no;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_signature;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_timestamp;

public class PasswordDialog extends Dialog {
    private static final String TAG = "DappBirdsSdk";
    private TextView tvCancel;
    private EditText edPassword;
    private Button btnSubmit;
    private CheckBox freepassword;
    private String address;
    private MainCallback payCallBack;
    private Context context;
    private Activity activity;
    private Timer timer;
    private Dialog loadDialog;
    private String from;
    private Bundle bundle;
    private boolean is_click = false;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (DappBirdsSdk.isEnableLog()) {
                    Log.e(TAG, (String) msg.obj);
                }
                if (payCallBack != null) {
                    payCallBack.onError(1, (String) msg.obj);
                }
            } else if (msg.what == 2) {
                if (payCallBack != null) {
                    payCallBack.onSuccess();
                    if (edPassword != null) {
                        if (!Kits.Empty.check(edPassword.getText().toString()) && freepassword.isChecked()) {
                            AbSharedUtil.putString(context, Contants.key_pass, edPassword.getText().toString());
                        } else {
                            AbSharedUtil.remove(context, Contants.key_pass);
                        }
                    }
                    AbSharedUtil.putString(context, Contants.pay_success_time, System.currentTimeMillis() + "");
                }
            }
            dismissPorcess();
            dismiss();
        }
    };

    public PasswordDialog(@NonNull Context context, Bundle bundle, MainCallback payCallBack) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.activity = (Activity) context;
        this.payCallBack = payCallBack;
        this.bundle = bundle;
        this.from = bundle.getString(key_from);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_password);
        initView();
        setListener();
    }

    private Message message = new Message();

    private void payError(String msg) {
        message.what = 1;
        message.obj = msg;
        handler.sendMessage(message);
    }

    private void setListener() {
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_click = true;
                payError(context.getResources().getString(R.string.cancel_pay));
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Kits.Empty.check(edPassword.getText().toString())) {
                    Toast.makeText(context, context.getResources().getString(R.string.empty_password), Toast.LENGTH_SHORT).show();
                    return;
                }
                closeKey();
                if (!from.equals(Contants.key_dialog)) {
                    if (listener != null) {
                        listener.context(edPassword.getText().toString());
                    }
                    return;
                }
                is_click = true;
                unitPay();
            }
        });
    }

    private void initView() {
        tvCancel = findViewById(R.id.tv_cancel);
        edPassword = findViewById(R.id.ed_password);
        btnSubmit = findViewById(R.id.btn_submit);
        freepassword = findViewById(R.id.freepassword);
        address = AbSharedUtil.getString(context, Contants.key_address, "");
        GeneralUtils.openKeybord(edPassword, context);
        if (from.equals(Contants.key_dialog)) {
            freepassword.setVisibility(View.VISIBLE);
            freepassword.setChecked(false);
        } else {
            freepassword.setVisibility(View.GONE);
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

    @Override
    public void dismiss() {
        closeKey();
        super.dismiss();
        if (!is_click) {
            if (payCallBack != null) {
                payCallBack.onError(1, activity.getResources().getString(R.string.cancel_pay));
            }
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

    private void closeKey() {
        View view = getCurrentFocus();
        if (view instanceof TextView) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert mInputMethodManager != null;
            mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
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

    private void unitPay() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        showDialogLoading();
        final HashMap<String, String> hashMap = SetPublicParam.getInstance().setSomeParam(context);
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
                                ontSdk.setRestful(BaseConfig.TEST_ONT_URL);
                                ontSdk.openWalletFile(activity.getSharedPreferences(Contants.SHARED_PATH, Context.MODE_PRIVATE));
                                Account account = ontSdk.getWalletMgr().getWallet().getAccount(address);
                                String multiply = DecimalUtil.multiplyWithScale(bundle.getString(key_amount), 1000000000 + "", 0);
                                Scrypt scrypt = new Scrypt();
                                Map map = WalletQR.exportAccountQRCode(scrypt, account);
                                String priKeyFromQrCode = WalletQR.getPriKeyFromQrCode(JSON.toJSONString(map), edPassword.getText().toString());
                                com.blockchain.dappbirds.opensdk.ont.ontio.account.Account sendAcct = new com.blockchain.dappbirds.opensdk.ont.ontio.account.Account(Helper.hexToBytes(priKeyFromQrCode), ontSdk.defaultSignScheme);
                                Address parse = Address.parse(bundle.getString(key_contract_address));
                                String toBase58 = parse.toBase58();
                                ontSdk.neovm().oep4().setContractAddress(bundle.getString(key_contract_address));
                                String transfer = ontSdk.neovm().oep4().sendTransfer(sendAcct, toBase58, Long.parseLong(multiply), sendAcct, 20000, 500, bundle.getString(key_order_no));
                                hashMap.put("txid", transfer);
                                if (timer == null) {
                                    timer = new Timer();
                                }
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        NetApi.orderCheck(hashMap, new JsonCallback<BaseDataBean>() {
                                            @Override
                                            public void onFail(Call call, Exception e, int id1) {
                                                cancel();
                                                payError(e.getMessage());
                                            }

                                            @Override
                                            public void onResponse(BaseDataBean response1, int id1) {
                                                if (response1.code == 0) {
                                                    String recult = response1.data.check_result;
                                                    if (!Kits.Empty.check(recult)) {
                                                        if (!recult.equals("TRADE_WAIT")) {
                                                            cancel();
                                                            if (recult.equals("TRADE_SUCCESS")) {
                                                                handler.sendEmptyMessage(2);
                                                            } else {
                                                                payError(activity.getResources().getString(R.string.pay_error));
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    payError(response1.msg);
                                                }
                                            }
                                        });
                                    }
                                }, 0, 1000);
                            } catch (Exception e) {
                                if (!Kits.Empty.check(e.getMessage())) {
                                    if (e.getMessage().contains("password")) {
                                        payError(activity.getResources().getString(R.string.error_password));
                                    } else if (e.getMessage().contains("balance")) {
                                        payError(context.getString(R.string.empty_balance));
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

    public interface OnClickListener {
        void context(String password);
    }

    private OnClickListener listener;

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }
}
