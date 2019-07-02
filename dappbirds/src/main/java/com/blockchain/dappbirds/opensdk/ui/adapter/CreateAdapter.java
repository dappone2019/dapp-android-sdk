package com.blockchain.dappbirds.opensdk.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blockchain.dappbirds.opensdk.R;
import com.blockchain.dappbirds.opensdk.model.DataBean;
import com.blockchain.dappbirds.opensdk.utils.Kits;

import java.util.List;

public class CreateAdapter extends RecyclerView.Adapter<CreateAdapter.MyViewHolder> {


    private Context context;
    private List<DataBean> list;


    public void setList(List<DataBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_create, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataBean dataBean = list.get(position);
        if (!Kits.Empty.check(dataBean.value)) {
            holder.value.setText(dataBean.value);
        }
        if (dataBean.is_right) {
            holder.stateIv.setImageDrawable(context.getResources().getDrawable(R.mipmap.yes_selected));
        } else {
            holder.stateIv.setImageDrawable(context.getResources().getDrawable(R.mipmap.yes_nomal));
        }
    }

    @Override
    public int getItemCount() {
        return Kits.Empty.check(list) ? 0 : list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView stateIv;
        private TextView value;

        MyViewHolder(View itemView) {
            super(itemView);
            stateIv = itemView.findViewById(R.id.state_iv);
            value = itemView.findViewById(R.id.value);
        }
    }
}
