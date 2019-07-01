package com.blockchain.dappbirds.opensdk.view.dlpopwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blockchain.dappbirds.opensdk.R;

import java.util.List;

public class PopAdapter extends BaseAdapter {
    private List<DLPopItem> mList;

    PopAdapter(List<DLPopItem> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder mHolder;
        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pop_item, parent, false);
            mHolder.imgLine = convertView.findViewById(R.id.img_line);
            mHolder.txt = convertView.findViewById(R.id.txt);

            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        DLPopItem mItem = mList.get(position);
        mHolder.txt.setText(mItem.getText());
        int cd = mItem.getColor();
        if (cd < 0x01000000) {
            cd = 0xff000000 + cd;
        }
        int cc = cd & 0x33ffffff;
        mHolder.imgLine.setBackgroundColor(cc);
        mHolder.txt.setTextColor(cd);
        if (position == mList.size() - 1) {
            mHolder.imgLine.setVisibility(View.GONE);
        } else {
            mHolder.imgLine.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView imgLine;
        TextView txt;
    }
}
