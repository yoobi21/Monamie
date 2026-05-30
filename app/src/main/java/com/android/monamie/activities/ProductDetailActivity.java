package com.android.monamie.activities;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.android.monamie.R;
import com.android.monamie.models.CartItem;
import com.android.monamie.utils.CartManager;
import com.google.android.material.button.MaterialButton;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.ViewGroup;
import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID   = "product_id";
    public static final String EXTRA_PRODUCT_NAME = "product_name";
    public static final String EXTRA_PRODUCT_CAT  = "product_category";
    public static final String EXTRA_PRODUCT_PRICE= "product_price";
    public static final String EXTRA_PRODUCT_IMG  = "product_image";
    public static final String EXTRA_PRODUCT_DESC = "product_desc";

    private int qty = 1;
    private int price;
    private int stock = 0;
    private String productId, productName;

    private android.widget.EditText etQty;
    private TextView tvTotal, tvCartBadge, tvStockInfo;
    private ImageView ivCartDetail;
    private NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("id","ID"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productId   = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
        productName = getIntent().getStringExtra(EXTRA_PRODUCT_NAME);
        String category = getIntent().getStringExtra(EXTRA_PRODUCT_CAT);
        price           = getIntent().getIntExtra(EXTRA_PRODUCT_PRICE, 0);
        int    imageRes = getIntent().getIntExtra(EXTRA_PRODUCT_IMG, R.drawable.img_cookie_velvet);
        String desc     = getIntent().getStringExtra(EXTRA_PRODUCT_DESC);
        stock           = getIntent().getIntExtra("product_stock", 0);

        ImageView      ivImage    = findViewById(R.id.ivDetailImage);
        TextView       tvCat      = findViewById(R.id.tvDetailCategory);
        TextView       tvName     = findViewById(R.id.tvDetailName);
        TextView       tvPrice    = findViewById(R.id.tvDetailPrice);
        TextView       tvDesc     = findViewById(R.id.tvDetailDesc);
        ImageView      ivBack     = findViewById(R.id.ivBack);
        ImageView      btnMinus   = findViewById(R.id.btnMinus);
        ImageView      btnPlus    = findViewById(R.id.btnPlus);
        MaterialButton btnAddCart = findViewById(R.id.btnAddToCart);
        etQty   = findViewById(R.id.etQty);
        tvTotal = findViewById(R.id.tvDetailTotal);
        tvCartBadge = findViewById(R.id.tvCartBadgeDetail);
        ivCartDetail = findViewById(R.id.ivCartDetail);
        tvStockInfo = findViewById(R.id.tvStockInfo);

        // Adjust for system navigation bar (Edge-to-Edge feel)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutBottomBar), (v, windowInsets) -> {
            androidx.core.graphics.Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), insets.bottom + 16);
            return windowInsets;
        });

        Glide.with(this)
                .load(imageRes)
                .into(ivImage);

        tvCat.setText(category);
        tvName.setText(productName);
        tvPrice.setText("Rp. " + fmt.format(price));
        tvDesc.setText(desc);
        tvStockInfo.setText("Stok: " + stock);
        
        etQty.setText(String.valueOf(qty));
        updateTotal();
        updateCartBadge();

        ivBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        ivCartDetail.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        btnMinus.setOnClickListener(v -> {
            if (qty > 1) { 
                qty--; 
                etQty.setText(String.valueOf(qty)); 
                updateTotal(); 
            }
        });

        btnPlus.setOnClickListener(v -> {
            if (qty < stock) {
                qty++; 
                etQty.setText(String.valueOf(qty)); 
                updateTotal();
            } else {
                android.widget.Toast.makeText(this, "Stok tidak mencukupi!", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        etQty.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                String val = s.toString();
                if (val.isEmpty()) return;
                try {
                    int inputQty = Integer.parseInt(val);
                    if (stock == 0) {
                        qty = 0;
                        if (!val.equals("0")) {
                            etQty.setText("0");
                            etQty.setSelection(1);
                        }
                    } else if (inputQty > stock) {
                        qty = stock;
                        etQty.setText(String.valueOf(qty));
                        etQty.setSelection(etQty.getText().length());
                        android.widget.Toast.makeText(ProductDetailActivity.this, "Maksimal stok: " + stock, android.widget.Toast.LENGTH_SHORT).show();
                    } else if (inputQty < 1) {
                        qty = 1;
                        etQty.setText("1");
                        etQty.setSelection(1);
                    } else {
                        qty = inputQty;
                    }
                    updateTotal();
                } catch (NumberFormatException e) {
                    qty = 1;
                    etQty.setText("1");
                    updateTotal();
                }
            }
        });

        btnAddCart.setOnClickListener(v -> {
            if (stock <= 0) {
                android.widget.Toast.makeText(this, "Stok habis!", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            if (qty > stock) {
                android.widget.Toast.makeText(this, "Jumlah melebihi stok!", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            CartItem item = new CartItem(productId, productName, price, qty, imageRes);
            CartManager.getInstance().addItem(item);

            startActivity(new Intent(this, CartActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    private void updateCartBadge() {
        if (tvCartBadge == null) return;
        int count = CartManager.getInstance().getTotalQuantity();
        if (count > 0) {
            tvCartBadge.setText(String.valueOf(count));
            tvCartBadge.setVisibility(android.view.View.VISIBLE);
        } else {
            tvCartBadge.setVisibility(android.view.View.GONE);
        }
    }

    private void updateTotal() {
        tvTotal.setText("Rp. " + fmt.format((long) price * qty));
    }
}