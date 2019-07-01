package com.blockchain.dappbirds.opensdk.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blockchain.dappbirds.opensdk.R;
import com.blockchain.dappbirds.opensdk.model.DataBean;
import com.blockchain.dappbirds.opensdk.utils.Kits;

import java.util.List;

public class MneMonicChooseAdapter extends RecyclerView.Adapter<MneMonicChooseAdapter.MyViewHolder> {

    private List<DataBean> list;

    public void setList(List<DataBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_mnenochoose, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        DataBean dataBean = list.get(i);
        if (!Kits.Empty.check(dataBean.value)) {
            myViewHolder.content.setText(dataBean.value);
        }
    }

    @Override
    public int getItemCount() {
        return Kits.Empty.check(list) ? 0 : list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView content;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
        }
    }
}
