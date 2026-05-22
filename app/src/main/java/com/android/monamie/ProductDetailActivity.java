package com.android.monamie;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    // Keys untuk Intent extra
    public static final String EXTRA_PRODUCT_ID   = "product_id";
    public static final String EXTRA_PRODUCT_NAME = "product_name";
    public static final String EXTRA_PRODUCT_CAT  = "product_category";
    public static final String EXTRA_PRODUCT_PRICE= "product_price";
    public static final String EXTRA_PRODUCT_IMG  = "product_image";
    public static final String EXTRA_PRODUCT_DESC = "product_desc";

    private int qty = 1;
    private int price;
    private String productId, productName;

    private TextView tvQty, tvTotal;
    private NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("id","ID"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Terima data dari Intent
        productId   = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
        productName = getIntent().getStringExtra(EXTRA_PRODUCT_NAME);
        String category = getIntent().getStringExtra(EXTRA_PRODUCT_CAT);
        price           = getIntent().getIntExtra(EXTRA_PRODUCT_PRICE, 0);
        int    imageRes = getIntent().getIntExtra(EXTRA_PRODUCT_IMG, R.drawable.img_cookie_velvet);
        String desc     = getIntent().getStringExtra(EXTRA_PRODUCT_DESC);

        // Bind views
        ImageView      ivImage    = findViewById(R.id.ivDetailImage);
        TextView       tvCat      = findViewById(R.id.tvDetailCategory);
        TextView       tvName     = findViewById(R.id.tvDetailName);
        TextView       tvPrice    = findViewById(R.id.tvDetailPrice);
        TextView       tvDesc     = findViewById(R.id.tvDetailDesc);
        ImageView      ivBack     = findViewById(R.id.ivBack);
        ImageView      btnMinus   = findViewById(R.id.btnMinus);
        ImageView      btnPlus    = findViewById(R.id.btnPlus);
        MaterialButton btnAddCart = findViewById(R.id.btnAddToCart);
        tvQty   = findViewById(R.id.tvQty);
        tvTotal = findViewById(R.id.tvDetailTotal);

        // Set data
        ivImage.setImageResource(imageRes);
        tvCat.setText(category);
        tvName.setText(productName);
        tvPrice.setText("Rp. " + fmt.format(price));
        tvDesc.setText(desc);
        updateTotal();

        // Back
        ivBack.setOnClickListener(v -> finish());

        // Qty controls
        btnMinus.setOnClickListener(v -> {
            if (qty > 1) { qty--; tvQty.setText(String.valueOf(qty)); updateTotal(); }
        });
        btnPlus.setOnClickListener(v -> {
            qty++; tvQty.setText(String.valueOf(qty)); updateTotal();
        });

        // Add to cart
        btnAddCart.setOnClickListener(v -> {
            CartItem item = new CartItem(productId, productName, price, qty, imageRes);
            CartManager.getInstance().addItem(item);

            Snackbar.make(findViewById(android.R.id.content),
                            productName + " ditambahkan ke keranjang",
                            Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getColor(R.color.mon_amie_gold))
                    .setTextColor(getColor(android.R.color.white))
                    .setAction("Lihat", v2 ->
                            startActivity(new Intent(this, CartActivity.class)))
                    .show();
        });
    }

    private void updateTotal() {
        tvTotal.setText("Rp. " + fmt.format((long) price * qty));
    }
}