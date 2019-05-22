package com.blockchain.dappbirds.dappbirdsdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blockchain.dappbirds.opensdk.wallet.DBWalletManager;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.wallet_address)
    Button walletAddress;
    @BindView(R.id.create_wallet)
    Button createWallet;
    @BindView(R.id.wallet_detail)
    Button walletDetail;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        setListener();
    }

    private void initData() {
        /**
         * 获取账户
         */
        MyApplication.dbWalletManager.getAccount(new DBWalletManager.AccountCallBack() {
            @Override
            public void onError(int errCode, String errInfo) {
                createWallet.setVisibility(View.VISIBLE);
                walletDetail.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.GONE);
                walletAddress.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(String addr) {
                //addr:钱包地址
                createWallet.setVisibility(View.GONE);
                walletDetail.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.VISIBLE);
                walletAddress.setVisibility(View.VISIBLE);
                walletAddress.setText(addr);
            }
        });
    }

    private void setListener() {
        /**
         * 创建钱包账户
         */
        createWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.dbWalletManager.createAccount(MainActivity.this, new DBWalletManager.AccountCallBack() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        createWallet.setVisibility(View.VISIBLE);
                        walletDetail.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.GONE);
                        walletAddress.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String addr) {
                        //addr:钱包地址
                        createWallet.setVisibility(View.GONE);
                        walletDetail.setVisibility(View.VISIBLE);
                        btnSubmit.setVisibility(View.VISIBLE);
                        walletAddress.setVisibility(View.VISIBLE);
                        walletAddress.setText(addr);
                        Toast.makeText(MainActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        /**
         * 查看余额和交易明细
         */
        walletDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.dbWalletManager.openDetail(MainActivity.this);
            }
        });

        /**
         * 统一下单
         */
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * @Context：上下文
                 * @timestamp：时间戳（精确到秒）
                 * @signature：签名字符串
                 * @order_no：订单号
                 * @amount：支付数量
                 * @contract_address：合约地址
                 */
                MyApplication.dbWalletManager.unitePay(MainActivity.this, "1556543707", "cf38f9b6d2dc07784e727066f2fdac77", System.currentTimeMillis() + "", "1", "48628e2aa44a7e7f2d8e9fbe4001d731713789ca", new DBWalletManager.PayCallBack() {
                    @Override
                    public void onError(int errCode, String errInfo) {
                        Toast.makeText(MainActivity.this, "支付失败：" + errInfo, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            MyApplication.exit();
        }
        return true;
    }
}
