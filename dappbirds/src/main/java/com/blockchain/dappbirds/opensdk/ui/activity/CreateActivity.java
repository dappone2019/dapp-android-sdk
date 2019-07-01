package com.blockchain.dappbirds.opensdk.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.blockchain.dappbirds.opensdk.DappBirdsSdk;
import com.blockchain.dappbirds.opensdk.R;
import com.blockchain.dappbirds.opensdk.config.BaseConfig;
import com.blockchain.dappbirds.opensdk.db.AbSharedUtil;
import com.blockchain.dappbirds.opensdk.db.Contants;
import com.blockchain.dappbirds.opensdk.http.JsonCallback;
import com.blockchain.dappbirds.opensdk.http.NetApi;
import com.blockchain.dappbirds.opensdk.http.SetPublicParam;
import com.blockchain.dappbirds.opensdk.model.BaseModel;
import com.blockchain.dappbirds.opensdk.model.DataBean;
import com.blockchain.dappbirds.opensdk.ont.ontio.OntSdk;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.Helper;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.WalletQR;
import com.blockchain.dappbirds.opensdk.ont.ontio.crypto.MnemonicCode;
import com.blockchain.dappbirds.opensdk.ont.ontio.sdk.manager.WalletMgr;
import com.blockchain.dappbirds.opensdk.ont.ontio.sdk.wallet.Account;
import com.blockchain.dappbirds.opensdk.ont.ontio.sdk.wallet.Scrypt;
import com.blockchain.dappbirds.opensdk.ui.adapter.CreateAdapter;
import com.blockchain.dappbirds.opensdk.utils.GeneralUtils;
import com.blockchain.dappbirds.opensdk.utils.Kits;
import com.blockchain.dappbirds.opensdk.view.dialog.LoadingDailog;
import com.blockchain.dappbirds.opensdk.wallet.DBWalletManager;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.regex.Pattern;

import okhttp3.Call;

public class CreateActivity extends Activity {

    private static final String TAG = "DappBirdsSdk";

    private List<DataBean> list = new ArrayList<>();
    private CreateAdapter createAdapter;
    private Context context;
    private WalletMgr walletMgr;
    private Account currentAccout;
    private List<Account> accounts;
    public Dialog loadDialog;
    public ImmersionBar immersionBar;
    private boolean is_check = false;
    private boolean is_copy = false;
    private boolean is_success = false;
    private Timer timer;
    private int type;
    private String app_id;
    private String open_id;
    private String chain_type;
    private ImageView imageBack;
    private LinearLayout linSuccess;
    private TextView privatekey;
    private TextView copy;
    private Button finsh;
    private EditText inputContent;
    private EditText edName;
    private EditText edPassword;
    private RecyclerView recyclerview;
    private EditText edPasswordTwo;
    private Button btnCreateaccount;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    createAccount();
                    break;
                case 1:
                    dismissPorcess();
                    if (type == 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Scrypt scrypt = new Scrypt();
                                    Map map = WalletQR.exportAccountQRCode(scrypt, currentAccout);
                                    final String priKeyFromQrCode = WalletQR.getPriKeyFromQrCode(JSON.toJSONString(map), edPassword.getText().toString());
                                    com.blockchain.dappbirds.opensdk.ont.ontio.account.Account account = new com.blockchain.dappbirds.opensdk.ont.ontio.account.Account(Helper.hexToBytes(priKeyFromQrCode), OntSdk.getInstance().defaultSignScheme);
                                    final String exportWif = account.exportWif();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            privatekey.setText(exportWif);
                                            linSuccess.setVisibility(View.VISIBLE);
                                            imageBack.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                } catch (Exception e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showDisclaimer();
                                        }
                                    });
                                    if (DappBirdsSdk.isEnableLog()) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                }
                            }
                        }).start();
                    } else {
                        Myfinish();
                    }
                    break;
                case 2:
                    dismissPorcess();
                    String error = (String) msg.obj;
                    if (error.contains("password")) {
                        Toast.makeText(context, getString(R.string.error_password), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 4:
                    dismissPorcess();
                    Toast.makeText(context, getString(R.string.netword), Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    dismissPorcess();
                    String message = (String) msg.obj;
                    DBWalletManager.callback.onError(1, message);
                    setResult(500);
                    finish();
                    break;
            }
        }
    };

    private void createAccount() {
        showDialogLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                clearAccount();
                switch (type) {
                    case 0:
                        createByNomal();
                        break;
                    case 1:
                        createByPriateKey();
                        break;
                    case 2:
                        createByKeyStore();
                        break;
                    case 3:
                        createByMnemonic();
                        break;
                }
                checkCreate();
            }
        }).start();
    }

    public void checkCreate() {
        accounts = walletMgr.getWallet().getAccounts();
        if (!Kits.Empty.check(accounts) && accounts.size() > 0) {
            for (Account account : accounts) {
                currentAccout = account;
            }
            createSuccess();
            return;
        }
        checkCreate();
    }

    private void clearAccount() {
        accounts = walletMgr.getWallet().getAccounts();
        if (!Kits.Empty.check(accounts) && accounts.size() > 0) {
            for (Account account : accounts) {
                walletMgr.getWallet().removeAccount(account.address);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        context = this;
        initBar();
        initView();
        initAdapter();
        setListener();
    }

    private void setListener() {

        edName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                chengeBtn();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Kits.Empty.check(s.toString())) {
                    for (DataBean dataBean : list) {
                        dataBean.is_right = false;
                    }
                    createAdapter.notifyDataSetChanged();
                    chengeBtn();
                } else {
                    is_check = (s.toString().length() >= 8) && Pattern.compile("[0-9]+").matcher(s.toString()).find() && Pattern.compile("[a-z]+").matcher(s.toString()).find() && Pattern.compile("[A-Z]+").matcher(s.toString()).find();
                    list.get(0).is_right = s.toString().length() >= 8;
                    list.get(3).is_right = Pattern.compile("[0-9]+").matcher(s.toString()).find();
                    list.get(2).is_right = Pattern.compile("[a-z]+").matcher(s.toString()).find();
                    list.get(1).is_right = Pattern.compile("[A-Z]+").matcher(s.toString()).find();
                    createAdapter.notifyDataSetChanged();
                    chengeBtn();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edPasswordTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                chengeBtn();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnCreateaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type != 2) {
                    if (Kits.Empty.check(edName.getText().toString())) {
                        showError(getString(R.string.empty_name));
                        return;
                    }
                }
                if (type == 1) {
                    if (Kits.Empty.check(inputContent.getText().toString())) {
                        showError(getString(R.string.empty_privatekey));
                        return;
                    }
                }
                if (type == 2) {
                    if (Kits.Empty.check(inputContent.getText().toString())) {
                        showError(getString(R.string.empty_keystore));
                        return;
                    }
                }
                if (type == 3) {
                    if (Kits.Empty.check(inputContent.getText().toString())) {
                        showError(getString(R.string.empty_mnemonic));
                        return;
                    }
                }
                if (Kits.Empty.check(edPassword.getText().toString())) {
                    showError(getString(R.string.empty_password));
                    return;
                }
                if (Kits.Empty.check(edPasswordTwo.getText().toString())) {
                    showError(getString(R.string.empty_passwordtwo));
                    return;
                }
                if (!edPassword.getText().toString().equals(edPasswordTwo.getText().toString())) {
                    showError(getString(R.string.password_commit));
                    return;
                }
                GeneralUtils.closeKeyBordes(context);
                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.commit_password))
                        .setMessage(getString(R.string.password_content))
                        .setNegativeButton(getString(R.string.cancel), null)
                        .setPositiveButton(getString(R.string.commit), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handler.sendEmptyMessage(0);
                            }
                        })
                        .show();
            }
        });
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        finsh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCopy();
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                assert clipboardManager != null;
                Objects.requireNonNull(clipboardManager).setText(privatekey.getText().toString());
                copy.setEnabled(false);
                copy.setText(getString(R.string.is_copy));
                is_copy = true;
            }
        });
    }

    private void createSuccess() {
        String toJson = new Gson().toJson(currentAccout);
        AbSharedUtil.putString(context, Contants.key_address, currentAccout.address);
        AbSharedUtil.putString(context, DappBirdsSdk.isEnableTest() + "" + app_id + open_id + chain_type, toJson);
        final HashMap<String, String> hashMap = SetPublicParam.getInstance().setSomeParam(context);
        hashMap.put(Contants.key_from_address, currentAccout.address);
        NetApi.walletStore(hashMap, new JsonCallback<BaseModel>() {
            @Override
            public void onFail(Call call, Exception e, int id) {
                handler.sendEmptyMessage(4);
                is_success = false;
            }

            @Override
            public void onResponse(BaseModel response, int id) {
                if (response.code == 0) {
                    is_success = true;
                    handler.sendEmptyMessage(1);
                } else {
                    is_success = false;
                    Message message = new Message();
                    message.what = 5;
                    message.obj = response.msg;
                    handler.sendMessage(message);
                }
            }
        });
    }

    private void createByMnemonic() {
        try {
            byte[] prikey = MnemonicCode.getPrikeyFromMnemonicCodesStrBip44(inputContent.getText().toString());
            AbSharedUtil.putString(context, Contants.key_mnemonic, inputContent.getText().toString());
            walletMgr.createAccountFromPriKey(edName.getText().toString(), edPassword.getText().toString(), Helper.toHexString(prikey));
            walletMgr.writeWallet();
            AbSharedUtil.putString(this, Contants.create_type, "3");
        } catch (Exception e) {
            createError(e);
        }
    }

    private void createByKeyStore() {
        try {
            String priKeyFromQrCode = WalletQR.getPriKeyFromQrCode(inputContent.getText().toString(), edPassword.getText().toString());
            walletMgr.createAccountFromPriKey(edPassword.getText().toString(), priKeyFromQrCode);
            walletMgr.writeWallet();
            AbSharedUtil.putString(this, Contants.create_type, "2");
        } catch (Exception e) {
            createError(e);
        }
    }

    private void createByPriateKey() {
        try {
            int length = inputContent.getText().toString().length();
            if (length != 52) {
                Toast.makeText(context, "WIF格式错误", Toast.LENGTH_SHORT).show();
                return;
            }
            byte[] bytes = com.blockchain.dappbirds.opensdk.ont.ontio.account.Account.getPrivateKeyFromWIF(inputContent.getText().toString());
            walletMgr.createAccountFromPriKey(edName.getText().toString(), edPassword.getText().toString(), Helper.toHexString(bytes));
            walletMgr.writeWallet();
            AbSharedUtil.putString(this, Contants.create_type, "1");
        } catch (Exception e) {
            createError(e);
        }
    }

    private void createByNomal() {
        try {
            String codesStr = MnemonicCode.generateMnemonicCodesStr();
            AbSharedUtil.putString(context, Contants.key_mnemonic, codesStr);
            byte[] prikey = MnemonicCode.getPrikeyFromMnemonicCodesStrBip44(codesStr);
            walletMgr.createAccountFromPriKey(edName.getText().toString(), edPassword.getText().toString(), Helper.toHexString(prikey));
            walletMgr.writeWallet();
            AbSharedUtil.putString(this, Contants.create_type, "0");
        } catch (Exception e) {
            createError(e);
        }
    }

    public void createError(Exception e) {
        if (DappBirdsSdk.isEnableLog()) {
            Log.e(TAG, e.getMessage());
        }
        Message message = new Message();
        message.what = 2;
        message.obj = e.getMessage();
        handler.sendMessage(message);
    }

    public void checkCopy() {
        if (!is_copy && type == 0 && is_success) {
            showDialog();
        } else {
            showDisclaimer();
        }
    }

    public void Myfinish() {
        DBWalletManager.getCallback().onSuccess(currentAccout.address);
        setResult(RESULT_OK);
        finish();
    }

    public void showDialog() {
        new AlertDialog.Builder(context)
                .setTitle(getString(R.string.hint))
                .setMessage(getString(R.string.copy_wif))
                .setNegativeButton(getString(R.string.commit), null)
                .show();
    }

    public void showDisclaimer() {
        new AlertDialog.Builder(context)
                .setTitle(getString(R.string.disclaimer))
                .setMessage(getString(R.string.disclaimer_content))
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.commit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Myfinish();
                    }
                })
                .show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Kits.Empty.check(currentAccout)) {
                finish();
            } else {
                checkCopy();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        imageBack = findViewById(R.id.image_back);
        TextView tvTitle = findViewById(R.id.tv_title);
        linSuccess = findViewById(R.id.lin_success);
        privatekey = findViewById(R.id.privatekey);
        copy = findViewById(R.id.copy);
        finsh = findViewById(R.id.finsh);
        LinearLayout linInput = findViewById(R.id.lin_input);
        TextView inputTitle = findViewById(R.id.input_title);
        inputContent = findViewById(R.id.input_content);
        LinearLayout linName = findViewById(R.id.lin_name);
        edName = findViewById(R.id.ed_name);
        edPassword = findViewById(R.id.ed_password);
        recyclerview = findViewById(R.id.recyclerview);
        edPasswordTwo = findViewById(R.id.ed_password_two);
        btnCreateaccount = findViewById(R.id.btn_createaccount);
        app_id = AbSharedUtil.getString(context, Contants.key_app_id, "");
        open_id = AbSharedUtil.getString(context, Contants.key_openid, "");
        chain_type = AbSharedUtil.getString(context, Contants.key_chain_type, "");
        type = getIntent().getIntExtra(Contants.KEY_TYPE, 0);
        switch (type) {
            case 0:
                tvTitle.setText(getString(R.string.create_wallet));
                linInput.setVisibility(View.GONE);
                break;
            case 1:
                btnCreateaccount.setText(getString(R.string.input) + getString(R.string.wallet));
                tvTitle.setText(getString(R.string.privatekey) + getString(R.string.input));
                inputTitle.setText(getString(R.string.privatekey));
                break;
            case 2:
                btnCreateaccount.setText(getString(R.string.input) + getString(R.string.wallet));
                tvTitle.setText("Keystore" + getString(R.string.input));
                inputTitle.setText("Keystore");
                linName.setVisibility(View.GONE);
                break;
            case 3:
                btnCreateaccount.setText(getString(R.string.input) + getString(R.string.wallet));
                tvTitle.setText(getString(R.string.mnemonic) + getString(R.string.input));
                inputTitle.setText(getString(R.string.mnemonic));
                inputContent.setHint(getString(R.string.mnemonic_hint));
                break;
        }
        btnCreateaccount.setEnabled(false);
        try {
            OntSdk ontSdk = OntSdk.getInstance();
            if (DappBirdsSdk.isEnableTest()) {
                ontSdk.setRestful(BaseConfig.TEST_ONT_URL);
            } else {
                ontSdk.setRestful(BaseConfig.MAIN_ONT_URL);
            }
            ontSdk.openWalletFile(getSharedPreferences(Contants.SHARED_PATH, Context.MODE_PRIVATE));
            walletMgr = ontSdk.getWalletMgr();
        } catch (Exception e) {
            e.printStackTrace();
            if (DappBirdsSdk.isEnableLog()) {
                Log.e(TAG, e.getMessage());
            }
        }

    }

    private void initBar() {
        if (immersionBar == null) {
            immersionBar = ImmersionBar.with(this);
        }
        immersionBar.barColor("#ffffff").statusBarDarkFont(true, 0.2f).init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (immersionBar != null) {
            immersionBar.destroy();
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void initAdapter() {
        list.clear();
        DataBean dataBean = new DataBean();
        dataBean.is_right = false;
        dataBean.value = getString(R.string.length8);
        dataBean.id = "0";
        DataBean dataBean1 = new DataBean();
        dataBean1.is_right = false;
        dataBean1.value = getString(R.string.big_letters);
        dataBean1.id = "1";
        DataBean dataBean2 = new DataBean();
        dataBean2.is_right = false;
        dataBean2.value = getString(R.string.small_letters);
        dataBean2.id = "2";
        DataBean dataBean3 = new DataBean();
        dataBean3.is_right = false;
        dataBean3.value = getString(R.string.digital);
        dataBean3.id = "3";
        list.add(dataBean);
        list.add(dataBean1);
        list.add(dataBean2);
        list.add(dataBean3);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        createAdapter = new CreateAdapter();
        recyclerview.setAdapter(createAdapter);
        createAdapter.setList(list);
    }

    public void showDialogLoading() {
        if (loadDialog == null) {
            LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                    .setMessage(getString(R.string.loading))
                    .setCancelable(true)
                    .setCancelOutside(false);
            loadDialog = loadBuilder.create();
        }
        loadDialog.show();
    }

    public void dismissPorcess() {
        if (this.loadDialog != null) {
            if (this.loadDialog.isShowing()) {
                this.loadDialog.dismiss();
            }
            this.loadDialog = null;
        }
    }

    public void showError(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void chengeBtn() {
        if (type == 2) {
            if (!Kits.Empty.check(inputContent.getText().toString()) && is_check && !Kits.Empty.check(edPassword.getText().toString()) && !Kits.Empty.check(edPasswordTwo.getText().toString()) && edPassword.getText().toString().equals(edPasswordTwo.getText().toString())) {
                btnCreateaccount.setEnabled(true);
            } else {
                btnCreateaccount.setEnabled(false);
            }
        } else if (type == 1 || type == 3) {
            if (!Kits.Empty.check(edName.getText().toString()) && !Kits.Empty.check(inputContent.getText().toString()) && is_check && !Kits.Empty.check(edPassword.getText().toString()) && !Kits.Empty.check(edPasswordTwo.getText().toString()) && edPassword.getText().toString().equals(edPasswordTwo.getText().toString())) {
                btnCreateaccount.setEnabled(true);
            } else {
                btnCreateaccount.setEnabled(false);
            }
        } else {
            if (!Kits.Empty.check(edName.getText().toString()) && is_check && !Kits.Empty.check(edPassword.getText().toString()) && !Kits.Empty.check(edPasswordTwo.getText().toString()) && edPassword.getText().toString().equals(edPasswordTwo.getText().toString())) {
                btnCreateaccount.setEnabled(true);
            } else {
                btnCreateaccount.setEnabled(false);
            }
        }
    }
}
