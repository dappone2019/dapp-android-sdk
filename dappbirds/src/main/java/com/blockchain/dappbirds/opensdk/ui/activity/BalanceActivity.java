package com.blockchain.dappbirds.opensdk.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.blockchain.dappbirds.opensdk.model.DataBean;
import com.blockchain.dappbirds.opensdk.model.FourDataBean;
import com.blockchain.dappbirds.opensdk.model.ThirdDataBean;
import com.blockchain.dappbirds.opensdk.ont.ontio.OntSdk;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.Helper;
import com.blockchain.dappbirds.opensdk.ont.ontio.common.WalletQR;
import com.blockchain.dappbirds.opensdk.ont.ontio.sdk.wallet.Account;
import com.blockchain.dappbirds.opensdk.ont.ontio.sdk.wallet.Scrypt;
import com.blockchain.dappbirds.opensdk.ont.ontio.sdk.wallet.Wallet;
import com.blockchain.dappbirds.opensdk.ui.adapter.OrderAdapter;
import com.blockchain.dappbirds.opensdk.utils.Kits;
import com.blockchain.dappbirds.opensdk.view.dialog.LoadingDailog;
import com.blockchain.dappbirds.opensdk.view.dialog.PasswordDialog;
import com.blockchain.dappbirds.opensdk.view.dlpopwindow.DLPopItem;
import com.blockchain.dappbirds.opensdk.view.dlpopwindow.DLPopupWindow;
import com.gyf.barlibrary.ImmersionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import okhttp3.Call;

public class BalanceActivity extends Activity {
    private ImageView imageBack;
    private TextView balance;
    private TextView copy;
    private SmartRefreshLayout smartrefresh;
    private RelativeLayout baseRelative;
    private ImageView baseImage;
    private TextView baseTextview;
    private RecyclerView recyclerview;
    private static final String TAG = "DappBirdsSdk";
    private Context context;
    public ImmersionBar immersionBar;
    private String address;
    private Timer timer;
    private Dialog loadDialog;
    private List<ThirdDataBean> thirdDataBeans = new ArrayList<>();
    private List<FourDataBean> fourDataBeans = new ArrayList<>();
    private OrderAdapter orderAdapter;
    private int page_number = 1;
    private int keyType = 0;
    private Wallet wallet;
    private String password = "";
    private PasswordDialog dialog;
    private ImageView mMenuIcon;
    private DLPopupWindow popupWindow;
    private List<DLPopItem> list = new ArrayList<>();
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                orderAdapter.setList(fourDataBeans);
            } else if (msg.what == 2) {
                orderAdapter.notifyDataSetChanged();
            } else if (msg.what == 3) {
                showDialogLoading();
                Account account = wallet.getAccount(address);
                switch (keyType) {
                    case 1:
                        try {
                            Scrypt scrypt = new Scrypt();
                            Map map = WalletQR.exportAccountQRCode(scrypt, account);
                            String priKeyFromQrCode = WalletQR.getPriKeyFromQrCode(JSON.toJSONString(map), password);
                            com.blockchain.dappbirds.opensdk.ont.ontio.account.Account account1 = new com.blockchain.dappbirds.opensdk.ont.ontio.account.Account(Helper.hexToBytes(priKeyFromQrCode), OntSdk.getInstance().defaultSignScheme);
                            String exportWif = account1.exportWif();
                            if (!Kits.Empty.check(exportWif)) {
                                dismissPorcess();
                                startActivity(createIntent(exportWif, 1));
                            }
                        } catch (Exception e) {
                            if (DappBirdsSdk.isEnableLog()) {
                                Log.e(TAG, e.getMessage());
                            }
                            dismissPorcess();
                            Toast.makeText(context, getString(R.string.error_password), Toast.LENGTH_SHORT).show();
                        }
                        if (dialog != null) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                        break;
                    case 2:
                        try {
                            Scrypt scrypt = new Scrypt();
                            Map map = WalletQR.exportAccountQRCode(scrypt, account);
                            String priKeyFromQrCode = WalletQR.getPriKeyFromQrCode(JSON.toJSONString(map), password);
                            if (!Kits.Empty.check(priKeyFromQrCode)) {
                                dismissPorcess();
                                startActivity(createIntent(JSON.toJSONString(map), 2));
                            }
                        } catch (Exception e) {
                            if (DappBirdsSdk.isEnableLog()) {
                                Log.e(TAG, e.getMessage());
                            }
                            dismissPorcess();
                            Toast.makeText(context, getString(R.string.error_password), Toast.LENGTH_SHORT).show();
                        }
                        if (dialog != null) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                        break;
                    case 3:
                        try {
                            Scrypt scrypt = new Scrypt();
                            Map map = WalletQR.exportAccountQRCode(scrypt, account);
                            String priKeyFromQrCode = WalletQR.getPriKeyFromQrCode(JSON.toJSONString(map), password);
                            if (!Kits.Empty.check(priKeyFromQrCode)) {
                                dismissPorcess();
                                startActivity(createIntent(AbSharedUtil.getString(context, Contants.key_mnemonic, ""), 3));
                            }
                        } catch (Exception e) {
                            if (DappBirdsSdk.isEnableLog()) {
                                Log.e(TAG, e.getMessage());
                            }
                            dismissPorcess();
                            Toast.makeText(context, getString(R.string.error_password), Toast.LENGTH_SHORT).show();
                        }
                        if (dialog != null) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private Intent createIntent(String msg, int type) {
        Intent intent;
        if (type == 1 || type == 2) {
            intent = new Intent(this, CopyActivity.class);
        } else {
            intent = new Intent(this, CopyMnemonicActivity.class);
        }
        intent.putExtra(Contants.KEY_VALUE, msg);
        intent.putExtra(Contants.KEY_TYPE, keyType);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        context = this;
        initBar();
        initView();
        initAdapter();
        showDialogLoading();
        getList();
        setListener();
    }

    private void setListener() {
        smartrefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page_number = 1;
                getList();
            }
        });

        smartrefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getList();
            }
        });

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                assert clipboardManager != null;
                clipboardManager.setText(address);
                copy.setEnabled(false);
                copy.setText(getString(R.string.is_copy));
            }
        });

        mMenuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAsDropDown(v, 0, 0);
            }
        });

        popupWindow.setOnItemClickListener(new DLPopupWindow.OnItemClickListener() {
            @Override
            public void OnClick(int position) {
                DLPopItem dlPopItem = list.get(position);
                if (dlPopItem.getText().equals(getString(R.string.saveprivatekey))) {
                    keyType = 1;
                    showPawweordDialog();
                } else {
                    keyType = 3;
                    showPawweordDialog();
                }
            }
        });
    }

    private void showPawweordDialog() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }
        Bundle bundle = new Bundle();
        bundle.putString(Contants.key_from, Contants.key_activity);
        dialog = new PasswordDialog(context, bundle, null);
        dialog.show();
        dialog.setListener(new PasswordDialog.OnClickListener() {
            @Override
            public void context(String pass) {
                password = pass;
                handler.sendEmptyMessage(3);
            }
        });
    }

    private void initAdapter() {
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter();
        recyclerview.setAdapter(orderAdapter);
    }

    private void initBar() {
        if (immersionBar == null) {
            immersionBar = ImmersionBar.with(this);
        }
        immersionBar.barColor("#ffffff").statusBarDarkFont(true, 0.2f).init();
    }

    private void getList() {
        if (page_number == 1) {
            hideEmpty();
            thirdDataBeans.clear();
            fourDataBeans.clear();
        }
        String page_size = "20";
        NetApi.getOrderList(address, page_size, String.valueOf(page_number), new JsonCallback<DataBean>() {
            @Override
            public void onFail(Call call, Exception e, int id) {
                dismissPorcess();
                if (page_number == 1) {
                    showEmpty();
                    smartrefresh.finishRefresh();
                } else {
                    smartrefresh.finishLoadMore();
                }
            }

            @Override
            public void onResponse(DataBean response, int id) {
                dismissPorcess();
                if (page_number == 1) {
                    smartrefresh.finishRefresh();
                } else {
                    smartrefresh.finishLoadMore();
                }
                if (!Kits.Empty.check(response.Error)) {
                    if (response.Error.equals("0")) {
                        if (page_number == 1) {
                            if (!Kits.Empty.check(response.Result.AssetBalance)) {
                                List<ThirdDataBean> thirdDataBeans = response.Result.AssetBalance;
                                for (ThirdDataBean thirdDataBean : thirdDataBeans) {
                                    if (thirdDataBean.AssetName.equals("ong")) {
                                        balance.setText(thirdDataBean.Balance);
                                    }
                                }
                            }
                        }
                        if (!Kits.Empty.check(response.Result.TxnList)) {
                            thirdDataBeans.addAll(response.Result.TxnList);
                            for (ThirdDataBean thirdDataBean : response.Result.TxnList) {
                                for (FourDataBean fourDataBean : thirdDataBean.TransferList) {
                                    fourDataBean.TxnHash = thirdDataBean.TxnHash;
                                    fourDataBean.TxnTime = thirdDataBean.TxnTime;
                                    fourDataBean.ConfirmFlag = thirdDataBean.ConfirmFlag;
                                    if (fourDataBean.AssetName.equals("ong") && thirdDataBean.ConfirmFlag.equals("1")) {
                                        fourDataBeans.add(fourDataBean);
                                    }
                                }
                            }
                            if (page_number == 1) {
                                handler.sendEmptyMessage(1);
                            } else {
                                handler.sendEmptyMessage(2);
                            }
                            if (thirdDataBeans.size() >= 20) {
                                page_number++;
                                smartrefresh.setEnableLoadMore(true);
                            } else {
                                smartrefresh.setEnableLoadMore(false);
                            }
                        } else {
                            if (page_number == 1) {
                                showEmpty();
                            } else {
                                smartrefresh.finishLoadMoreWithNoMoreData();
                            }
                        }
                    } else {
                        if (page_number == 1) {
                            showEmpty();
                        }
                    }
                } else {
                    if (page_number == 1) {
                        showEmpty();
                    }
                }
            }
        });
    }

    private void initView() {
        imageBack = findViewById(R.id.image_back);
        mMenuIcon = findViewById(R.id.menu_icon);
        balance = findViewById(R.id.balance);
        TextView walletAddress = findViewById(R.id.wallet_address);
        copy = findViewById(R.id.copy);
        smartrefresh = findViewById(R.id.smartrefresh);
        baseRelative = findViewById(R.id.base_relative);
        baseImage = findViewById(R.id.base_image);
        baseTextview = findViewById(R.id.base_textview);
        recyclerview = findViewById(R.id.recyclerview);
        address = AbSharedUtil.getString(context, Contants.key_address, "");
        String string = AbSharedUtil.getString(this, Contants.create_type, "");
        list.clear();
        DLPopItem dlPopItem = new DLPopItem(getString(R.string.saveprivatekey), getResources().getColor(R.color.color_fff));
        DLPopItem dlPopItem1 = new DLPopItem(getString(R.string.savemnemonic), getResources().getColor(R.color.color_fff));
        list.add(dlPopItem);
        if (!string.equals("1")) {
            list.add(dlPopItem1);
        }
        popupWindow = new DLPopupWindow(context, list, DLPopupWindow.STYLE_WEIXIN);
        walletAddress.setText(address);
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
                    ontSdk.openWalletFile(getSharedPreferences(Contants.SHARED_PATH, Context.MODE_PRIVATE));
                    wallet = ontSdk.getWalletMgr().getWallet();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (DappBirdsSdk.isEnableLog()) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        }).start();
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

    public void showEmpty() {
        baseRelative.setVisibility(View.VISIBLE);
        baseTextview.setText(getString(R.string.no_data));
        baseImage.setImageDrawable(getResources().getDrawable(R.mipmap.no_read_collect));
    }

    public void hideEmpty() {
        smartrefresh.setEnableLoadMore(false);
        smartrefresh.resetNoMoreData();
        baseRelative.setVisibility(View.GONE);
    }
}
