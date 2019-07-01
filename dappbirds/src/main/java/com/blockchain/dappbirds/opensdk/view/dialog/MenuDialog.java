package com.blockchain.dappbirds.opensdk.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.blockchain.dappbirds.opensdk.R;

public class MenuDialog extends Dialog {
    private String createType;
    private Context context;

    public MenuDialog(@NonNull Context context, String createType) {
        super(context);
        this.context = context;
        this.createType = createType;
    }

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(view);
        setContentView(R.layout.dialog_menu);
    }
}
