package com.blockchain.dappbirds.opensdk.view.dlpopwindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.blockchain.dappbirds.opensdk.R;

import java.util.List;

public class DLPopupWindow extends PopupWindow {
    public interface OnItemClickListener {
        void OnClick(int position);
    }

    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener on) {
        this.onItemClickListener = on;
    }

    public static final int STYLE_WEIXIN = 1;

    public DLPopupWindow(Context context, List<DLPopItem> list, int style) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View mContentView = mInflater.inflate(R.layout.pop_window, null, false);
        setContentView(mContentView);
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable());
        setOutsideTouchable(true);
        ListView listView = mContentView.findViewById(R.id.listview);
        PopAdapter mAdapter = new PopAdapter(list);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClickListener.OnClick(position);
                dismiss();
            }
        });
        LinearLayout llBG = mContentView.findViewById(R.id.ll_bg);
        if (style == STYLE_WEIXIN) {
            llBG.setBackgroundResource(R.drawable.menu_open_weixin);
        } else {
            llBG.setBackgroundResource(R.drawable.menu_open);
        }
    }
}
