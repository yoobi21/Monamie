package com.android.monamie;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductCardAdapter extends RecyclerView.Adapter<ProductCardAdapter.VH> {

    public interface OnAddToCartListener {
        void onAddToCart(Product product);
    }

    private final List<Product>       items;
    private final OnAddToCartListener listener;

    public ProductCardAdapter(List<Product> items, OnAddToCartListener listener) {
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

        holder.ivProduct.setImageResource(product.getImageRes());
        holder.tvCategory.setText(product.getCategory());
        holder.tvName.setText(product.getName());

        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        holder.tvPrice.setText("Rp. " + fmt.format(product.getPrice()));

        holder.ivAddToCart.setOnClickListener(v -> {
            if (listener != null) listener.onAddToCart(product);
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
}