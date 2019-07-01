package com.blockchain.dappbirds.opensdk.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blockchain.dappbirds.opensdk.R;
import com.gyf.barlibrary.ImmersionBar;

public class HomeActivity extends Activity {


    private Context context;
    public ImmersionBar immersionBar;
    private ImageView imageBack;
    private LinearLayout btnCreate;
    private LinearLayout btnInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = this;
        initBar();
        initView();
        setListner();
    }

    private void setListner() {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, CreateActivity.class), 1);
            }
        });

        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, Inputctivity.class), 1);
            }
        });

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initBar() {
        if (immersionBar == null) {
            immersionBar = ImmersionBar.with(this);
        }
        immersionBar.barColor("#ffffff").statusBarDarkFont(true, 0.2f).init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            finish();
        } else if (resultCode == 500) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (immersionBar != null) {
            immersionBar.destroy();
        }
    }

    private void initView() {
        imageBack = findViewById(R.id.image_back);
        btnCreate = findViewById(R.id.btn_create);
        btnInput = findViewById(R.id.btn_input);
    }
}
