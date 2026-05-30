package com.android.monamie.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.android.monamie.R;
import com.android.monamie.models.HistoryItem;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<HistoryItem> historyList;

    public HistoryAdapter(List<HistoryItem> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = historyList.get(position);
        holder.tvOrderId.setText(item.getOrderId());
        holder.tvDate.setText(item.getDate());
        holder.tvStatus.setText(item.getStatus());
        holder.tvTotal.setText(item.getTotalAmount());
        
        Glide.with(holder.itemView.getContext())
                .load(item.getImageRes())
                .into(holder.ivItem);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItem;
        TextView tvOrderId, tvDate, tvStatus, tvTotal;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.ivHistoryItem);
            tvOrderId = itemView.findViewById(R.id.tvHistoryOrderId);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            tvStatus = itemView.findViewById(R.id.tvHistoryStatus);
            tvTotal = itemView.findViewById(R.id.tvHistoryTotal);
        }
    }
}