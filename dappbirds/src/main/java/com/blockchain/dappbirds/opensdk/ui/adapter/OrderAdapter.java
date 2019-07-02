package com.blockchain.dappbirds.opensdk.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blockchain.dappbirds.opensdk.R;
import com.blockchain.dappbirds.opensdk.db.AbSharedUtil;
import com.blockchain.dappbirds.opensdk.model.FourDataBean;
import com.blockchain.dappbirds.opensdk.utils.Kits;
import com.blockchain.dappbirds.opensdk.utils.TimeUtils;

import java.text.NumberFormat;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private List<FourDataBean> list;
    private String address;
    private Context context;


    public void setList(List<FourDataBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        address = AbSharedUtil.getAddress(context);
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_order, viewGroup, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final FourDataBean fourDataBean = list.get(i);
        if (!Kits.Empty.check(fourDataBean.FromAddress)) {
            if (fourDataBean.FromAddress.equals(address)) {
                myViewHolder.address.setText(fourDataBean.ToAddress);
                myViewHolder.amount.setText("-" + NumberFormat.getInstance().format(Float.parseFloat(fourDataBean.Amount)) + "\t" + fourDataBean.AssetName);
                myViewHolder.amount.setTextColor(context.getResources().getColor(R.color.color_fb283c));
            } else {
                myViewHolder.address.setText(fourDataBean.FromAddress);
                myViewHolder.amount.setText("+" + NumberFormat.getInstance().format(Float.parseFloat(fourDataBean.Amount)) + "\t" + fourDataBean.AssetName);
                myViewHolder.amount.setTextColor(context.getResources().getColor(R.color.color_03af4b));
            }
        }
        if (!Kits.Empty.check(fourDataBean.ConfirmFlag)) {
            if (fourDataBean.ConfirmFlag.equals("1")) {
                myViewHolder.state.setText(context.getResources().getString(R.string.order_success));
            } else {
                myViewHolder.state.setText(context.getResources().getString(R.string.order_faile));
            }
        } else {
            myViewHolder.state.setText(context.getResources().getString(R.string.order_wait));
        }
        if (!Kits.Empty.check(fourDataBean.TxnTime)) {
            myViewHolder.time.setText(TimeUtils.getSpaceTime(Long.parseLong(fourDataBean.TxnTime)));
        }
    }

    @Override
    public int getItemCount() {
        return Kits.Empty.check(list) ? 0 : list.size();
    }

    private void initView() {

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout orderItem;
        private TextView time;
        private TextView amount;
        private TextView state;
        private TextView address;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            orderItem = itemView.findViewById(R.id.order_item);
            time = itemView.findViewById(R.id.time);
            amount = itemView.findViewById(R.id.amount);
            state = itemView.findViewById(R.id.state);
        }
    }
}
