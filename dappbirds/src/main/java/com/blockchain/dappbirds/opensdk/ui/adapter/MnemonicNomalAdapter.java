package com.blockchain.dappbirds.opensdk.ui.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
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

public class MnemonicNomalAdapter extends RecyclerView.Adapter<MnemonicNomalAdapter.MyViewHolder> {

    private List<DataBean> list;
    private Context context;

    public void setList(List<DataBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_mnenomal, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final DataBean dataBean = list.get(i);
        if (!Kits.Empty.check(dataBean.value)) {
            myViewHolder.content.setText(dataBean.value);
        }
        GradientDrawable gradientDrawable = (GradientDrawable) myViewHolder.content.getBackground();
        if (!dataBean.is_check) {
            gradientDrawable.setColor(context.getResources().getColor(R.color.color_f0f0f0));
            myViewHolder.content.setTextColor(context.getResources().getColor(R.color.color_333));
        } else {
            gradientDrawable.setColor(context.getResources().getColor(R.color.color_428af9));
            myViewHolder.content.setTextColor(context.getResources().getColor(R.color.color_fff));
        }

        myViewHolder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickItem(dataBean);
                }
            }
        });
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

    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onClickItem(DataBean dataBean);
    }
}
