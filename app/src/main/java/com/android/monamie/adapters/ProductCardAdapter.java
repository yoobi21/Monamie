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
import com.android.monamie.models.Product;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductCardAdapter extends RecyclerView.Adapter<ProductCardAdapter.VH> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final List<Product>          items;
    private final OnProductClickListener listener;

    public ProductCardAdapter(List<Product> items, OnProductClickListener listener) {
        this.items    = items;
        this.listener = listener;
    }

    public static class VH extends RecyclerView.ViewHolder {
        ImageView ivProduct, ivAddToCart;
        TextView  tvCategory, tvName, tvPrice;

        public VH(@NonNull View view) {
            super(view);
            ivProduct   = view.findViewById(R.id.ivProduct);
            tvCategory  = view.findViewById(R.id.tvCategory);
            tvName      = view.findViewById(R.id.tvProductName);
            tvPrice     = view.findViewById(R.id.tvPrice);
            ivAddToCart = view.findViewById(R.id.ivAddToCart);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_card, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Product product = items.get(position);

        Glide.with(holder.itemView.getContext())
                .load(product.getImageRes())
                .into(holder.ivProduct);

        holder.tvCategory.setText(product.getCategory());
        holder.tvName.setText(product.getName());

        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        holder.tvPrice.setText("Rp. " + fmt.format(product.getPrice()));

        // Klik seluruh card untuk ke detail
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProductClick(product);
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
}