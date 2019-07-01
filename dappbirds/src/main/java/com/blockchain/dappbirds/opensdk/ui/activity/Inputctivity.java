package com.blockchain.dappbirds.opensdk.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blockchain.dappbirds.opensdk.R;
import com.gyf.barlibrary.ImmersionBar;

import static com.blockchain.dappbirds.opensdk.db.Contants.KEY_TYPE;

public class Inputctivity extends Activity {
    private ImmersionBar immersionBar;
    private ImageView imageBack;
    private LinearLayout privatekey;
    private LinearLayout keystore;
    private LinearLayout mnemonic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        initBar();
        initView();
        setListener();
    }

    private void setListener() {
        privatekey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create(1);
            }
        });

        keystore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create(2);
            }
        });

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mnemonic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create(3);
            }
        });
    }

    public void create(int type) {
        Intent intent = new Intent(this, CreateActivity.class);
        intent.putExtra(KEY_TYPE, type);
        startActivityForResult(intent, 1);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        } else if (requestCode == 500) {
            setResult(500);
            finish();
        }
    }

    private void initView() {
        imageBack = findViewById(R.id.image_back);
        privatekey = findViewById(R.id.privatekey);
        keystore = findViewById(R.id.keystore);
        mnemonic = findViewById(R.id.mnemonic);
    }
}
