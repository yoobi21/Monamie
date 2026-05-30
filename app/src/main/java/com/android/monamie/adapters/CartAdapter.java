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
import com.android.monamie.models.CartItem;
import com.android.monamie.utils.CartManager;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {

    public interface OnCartChangeListener {
        void onCartChanged();
    }

    private final List<CartItem>    items;
    private final OnCartChangeListener listener;
    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("id","ID"));

    public CartAdapter(List<CartItem> items, OnCartChangeListener listener) {
        this.items    = items;
        this.listener = listener;
    }

    public static class VH extends RecyclerView.ViewHolder {
        ImageView ivItem, ivPlus, ivMinus;
        TextView  tvName, tvPrice, tvOriginalPrice, tvQty;

        public VH(@NonNull View v) {
            super(v);
            ivItem  = v.findViewById(R.id.ivCartItem);
            ivPlus  = v.findViewById(R.id.ivCartPlus);
            ivMinus = v.findViewById(R.id.ivCartMinus);
            tvName  = v.findViewById(R.id.tvCartItemName);
            tvPrice = v.findViewById(R.id.tvCartItemPrice);
            tvOriginalPrice = v.findViewById(R.id.tvCartItemOriginalPrice);
            tvQty   = v.findViewById(R.id.tvCartQty);
        }
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        CartItem item = items.get(pos);
        
        Glide.with(h.itemView.getContext())
                .load(item.getImageRes())
                .into(h.ivItem);

        h.tvName.setText(item.getName());
        h.tvPrice.setText("Rp. " + fmt.format(item.getPrice()));
        h.tvQty.setText(String.valueOf(item.getQuantity()));

        if (item.getPrice() < item.getOriginalPrice()) {
            h.tvOriginalPrice.setText("Rp. " + fmt.format(item.getOriginalPrice()));
            h.tvOriginalPrice.setPaintFlags(h.tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            h.tvOriginalPrice.setVisibility(View.VISIBLE);
        } else {
            h.tvOriginalPrice.setVisibility(View.GONE);
        }

        // Enabled controls for all items (including promo)
        h.ivPlus.setAlpha(1.0f);
        h.ivMinus.setAlpha(1.0f);
        h.ivPlus.setEnabled(true);
        h.ivMinus.setEnabled(true);
        
        h.ivPlus.setOnClickListener(v -> {
            CartManager.getInstance().updateQty(item.getProductId(), item.getQuantity() + 1);
            notifyItemChanged(pos);
            if (listener != null) listener.onCartChanged();
        });

        h.ivMinus.setOnClickListener(v -> {
            int newQty = item.getQuantity() - 1;
            CartManager.getInstance().updateQty(item.getProductId(), newQty);
            if (newQty <= 0) {
                notifyItemRemoved(pos);
            } else {
                notifyItemChanged(pos);
            }
            if (listener != null) listener.onCartChanged();
        });
    }

    @Override public int getItemCount() { return items.size(); }
}