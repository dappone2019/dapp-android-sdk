package com.blockchain.dappbirds.opensdk.ui.activity;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blockchain.dappbirds.opensdk.R;
import com.blockchain.dappbirds.opensdk.db.Contants;
import com.gyf.barlibrary.ImmersionBar;


public class CopyActivity extends Activity {

    private int keyType;
    private boolean is_copy = false;
    public ImmersionBar immersionBar;
    private ImageView imageBack;
    private TextView privatekey;
    private TextView copy;
    private Button finsh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy);
        initBar();
        initView();
        setListener();
    }

    private void setListener() {
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                assert clipboardManager != null;
                clipboardManager.setText(privatekey.getText().toString());
                copy.setEnabled(false);
                copy.setText(getString(R.string.is_copy));
                is_copy = true;
            }
        });

        finsh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCopy();
            }
        });

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void checkCopy() {
        if (is_copy) {
            finish();
        } else {
            if (keyType == 1) {
                showDialog(getString(R.string.please_private));
            } else {
                showDialog(getString(R.string.please_keystore));
            }
        }
    }

    public void showDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.hint))
                .setMessage(msg)
                .setNegativeButton(getString(R.string.commit), null)
                .show();
    }

    private void initView() {
        imageBack = findViewById(R.id.image_back);
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView title = findViewById(R.id.title);
        privatekey = findViewById(R.id.privatekey);
        copy = findViewById(R.id.copy);
        finsh = findViewById(R.id.finsh);
        keyType = getIntent().getIntExtra(Contants.KEY_TYPE, 0);
        String value = getIntent().getStringExtra(Contants.KEY_VALUE);
        String string = getString(R.string.copy_title);
        String content = getString(R.string.btn_content);
        if (keyType == 1) {
            tvTitle.setText(getString(R.string.copy_privatekey));
            string = string.replace("content", getString(R.string.privatekey));
            content = content.replace("content", getString(R.string.privatekey));
        } else {
            tvTitle.setText(getString(R.string.copy_keystore));
            string = string.replace("content", "Keystore");
            content = content.replace("content", "Keystore");
        }
        title.setText(string);
        finsh.setText(content);
        privatekey.setText(value);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (immersionBar != null) {
            immersionBar.destroy();
        }
    }

    private void initBar() {
        if (immersionBar == null) {
            immersionBar = ImmersionBar.with(this);
        }
        immersionBar.barColor("#ffffff").statusBarDarkFont(true, 0.2f).init();
    }

}
