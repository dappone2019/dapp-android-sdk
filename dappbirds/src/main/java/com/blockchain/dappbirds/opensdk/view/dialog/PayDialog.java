package com.blockchain.dappbirds.opensdk.view.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
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
import com.gyf.barlibrary.ImmersionBar;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

import static com.blockchain.dappbirds.opensdk.db.Contants.key_amount;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_contract_address;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_from_address;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_order_no;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_signature;
import static com.blockchain.dappbirds.opensdk.db.Contants.key_timestamp;

public class PayDialog extends Dialog {
    private LinearLayout top;
    private TextView tvCancel;
    private Button btnSubmit;
    private TextView bottomView;
    private Dialog loadDialog;
    private Bundle bundle;
    private boolean is_show;
    private Activity activity;
    private MainCallback mainCallback;
    private String address;
    private PasswordDialog passwordDialog;
    private Timer timer;
    private Message message = new Message();
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
                    AbSharedUtil.putString(activity, Contants.pay_success_time, System.currentTimeMillis() + "");
                }
            }
            dismissPorcess();
            dismiss();
        }
    };

    public PayDialog(Context context, Bundle bundle, boolean is_show, MainCallback mainCallback) {
        super(context, R.style.MyDialog);
        this.activity = (Activity) context;
        this.mainCallback = mainCallback;
        this.is_show = is_show;
        this.bundle = bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus();
        setContentView(R.layout.dialog_pay);
        initView();
        setListener();
    }

    private void payError(String msg) {
        message.what = 1;
        message.obj = msg;
        handler.sendMessage(message);
    }

    private void setListener() {
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_click = true;
                payError(activity.getResources().getString(R.string.cancel_pay));
            }
        });
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
                String time = AbSharedUtil.getString(activity, Contants.pay_success_time, "");
                if (Kits.Empty.check(time)) {
                    showPasswordDialog();
                } else {
                    long long_time = System.currentTimeMillis() - Long.parseLong(time);
                    if (long_time < 86400000 && !Kits.Empty.check(AbSharedUtil.getString(activity, Contants.key_pass, ""))) {
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
                                ontSdk.setRestful(BaseConfig.TEST_ONT_URL);
                                ontSdk.openWalletFile(activity.getSharedPreferences(Contants.SHARED_PATH, Context.MODE_PRIVATE));
                                Account account = ontSdk.getWalletMgr().getWallet().getAccount(address);
                                String multiply = DecimalUtil.multiplyWithScale(bundle.getString(key_amount), 1000000000 + "", 0);
                                Scrypt scrypt = new Scrypt();
                                Map map = WalletQR.exportAccountQRCode(scrypt, account);
                                String priKeyFromQrCode = WalletQR.getPriKeyFromQrCode(JSON.toJSONString(map), AbSharedUtil.getString(activity, Contants.key_pass, ""));
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
                                                payError(activity.getResources().getString(R.string.netword));
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
                                                    cancel();
                                                    payError(response1.msg);
                                                }
                                            }
                                        });
                                    }
                                }, 0, 1000);
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
        top = findViewById(R.id.top);
        TextView myAddress = findViewById(R.id.address);
        tvCancel = findViewById(R.id.tv_cancel);
        TextView amount = findViewById(R.id.amount);
        btnSubmit = findViewById(R.id.btn_submit);
        bottomView = findViewById(R.id.bottom_view);
        TextView contractAddress = findViewById(R.id.contract_address);
        TextView orderNo = findViewById(R.id.order_no);
        address = AbSharedUtil.getString(activity, Contants.key_address, "");
        myAddress.setText(address);
        amount.setText(bundle.getString(key_amount) + "\tONG");
        contractAddress.setText(bundle.getString(key_contract_address));
        orderNo.setText(bundle.getString(key_order_no));
    }

    private void setTranslucentStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            assert window != null;
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            Objects.requireNonNull(getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
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
        if (is_show) {
            bottomView.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) bottomView.getLayoutParams();
            layoutParams.height = ImmersionBar.getNavigationBarHeight(activity);
        } else {
            bottomView.setVisibility(View.GONE);
        }
        Objects.requireNonNull(getWindow()).getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
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
