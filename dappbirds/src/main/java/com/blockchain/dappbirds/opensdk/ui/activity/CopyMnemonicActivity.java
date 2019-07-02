package com.blockchain.dappbirds.opensdk.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blockchain.dappbirds.opensdk.R;
import com.blockchain.dappbirds.opensdk.db.AbSharedUtil;
import com.blockchain.dappbirds.opensdk.db.Contants;
import com.blockchain.dappbirds.opensdk.model.DataBean;
import com.blockchain.dappbirds.opensdk.ui.adapter.MneMonicChooseAdapter;
import com.blockchain.dappbirds.opensdk.ui.adapter.MnemonicNomalAdapter;
import com.blockchain.dappbirds.opensdk.utils.Kits;
import com.blockchain.dappbirds.opensdk.view.FlowLayoutManager;
import com.google.common.base.Joiner;
import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class CopyMnemonicActivity extends Activity {

    private ImmersionBar immersionBar;
    private List<DataBean> nomal = new ArrayList<>();
    private List<DataBean> choose = new ArrayList<>();
    private MnemonicNomalAdapter mnemonicNomalAdapter;
    private MneMonicChooseAdapter mneMonicChooseAdapter;
    private boolean is_next = false;
    private ImageView imageBack;
    private TextView title;
    private LinearLayout linVerification;
    private RecyclerView verificationRecy;
    private RecyclerView recyclerview;
    private Button btnState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_copy_mnemonic);
        initBar();
        initView();
        initAdapter();
        initData();
        setListener();
    }

    private void initBar() {
        if (immersionBar == null) {
            immersionBar = ImmersionBar.with(this);
        }
        immersionBar.barColor("#ffffff").statusBarDarkFont(true, 0.2f).init();
    }

    private void setListener() {
        btnState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_next) {
                    is_next = true;
                    btnState.setText(getString(R.string.commit));
                    title.setText(getString(R.string.save_mne_hint_2));
                    linVerification.setVisibility(View.VISIBLE);
                    Collections.shuffle(nomal);
                    mnemonicNomalAdapter.setList(nomal);
                } else {
                    if (Kits.Empty.check(choose)) {
                        Toast.makeText(CopyMnemonicActivity.this, getString(R.string.error_mnemonic), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<String> list = new ArrayList<>();
                    list.clear();
                    for (DataBean dataBean : choose) {
                        list.add(dataBean.value);
                    }
                    String join = Joiner.on(" ").join(list);
                    if (!join.equals(AbSharedUtil.getMnemonic(CopyMnemonicActivity.this))) {
                        Toast.makeText(CopyMnemonicActivity.this, getString(R.string.error_mnemonic), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(CopyMnemonicActivity.this, getString(R.string.save_mneno_success), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        mnemonicNomalAdapter.setListener(new MnemonicNomalAdapter.OnItemClickListener() {
            @Override
            public void onClickItem(DataBean dataBean) {
                if (!is_next) {
                    return;
                }
                if (!dataBean.is_check) {
                    choose.add(dataBean);
                    for (int i = 0; i < nomal.size(); i++) {
                        if (dataBean.value.equals(nomal.get(i).value)) {
                            dataBean.is_check = true;
                        }
                    }
                    mnemonicNomalAdapter.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < choose.size(); i++) {
                        if (choose.get(i).value.equals(dataBean.value)) {
                            choose.remove(i);
                        }
                    }
                    for (int i = 0; i < nomal.size(); i++) {
                        if (dataBean.value.equals(nomal.get(i).value)) {
                            dataBean.is_check = false;
                        }
                    }
                    mnemonicNomalAdapter.notifyDataSetChanged();
                }
                mneMonicChooseAdapter.setList(choose);
            }
        });

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initAdapter() {
        recyclerview.setLayoutManager(new FlowLayoutManager(this, true));
        mnemonicNomalAdapter = new MnemonicNomalAdapter();
        recyclerview.setAdapter(mnemonicNomalAdapter);
        verificationRecy.setLayoutManager(new FlowLayoutManager(this, true));
        mneMonicChooseAdapter = new MneMonicChooseAdapter();
        verificationRecy.setAdapter(mneMonicChooseAdapter);
    }

    private void initData() {
        String value = getIntent().getStringExtra(Contants.KEY_VALUE);
        List<String> strings = Arrays.asList(value.split(" "));
        nomal.clear();
        for (int i = 0; i < strings.size(); i++) {
            DataBean dataBean = new DataBean();
            dataBean.value = strings.get(i);
            dataBean.is_check = false;
            nomal.add(dataBean);
        }
        mnemonicNomalAdapter.setList(nomal);
    }

    private void initView() {
        imageBack = findViewById(R.id.image_back);
        title = findViewById(R.id.title);
        linVerification = findViewById(R.id.lin_verification);
        verificationRecy = findViewById(R.id.verification_recy);
        recyclerview = findViewById(R.id.recyclerview);
        btnState = findViewById(R.id.btn_state);
        linVerification.setVisibility(View.GONE);
        title.setText(getString(R.string.save_mne_hint_1));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (immersionBar != null) {
            immersionBar.destroy();
        }
    }
}
