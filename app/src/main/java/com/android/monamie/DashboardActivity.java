package com.android.monamie;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private TextView     tvUserName;
    private RecyclerView rvForYou;
    private ImageView    ivCart, ivProfile;
    private ImageView    navHome, navWishlist, navOrders, navProfile;

    private final List<Product> forYouProducts = Arrays.asList(
            new Product(1, "Red Velvet Cookie", "Cookies", 8000, R.drawable.img_cookie_velvet),
            new Product(2, "Matcha Cookie",      "Cookies", 9000, R.drawable.img_cookie_velvet),
            new Product(3, "Choco Chip Cookie",  "Cookies", 7000, R.drawable.img_cookie_velvet),
            new Product(4, "Butter Cookie",      "Cookies", 6000, R.drawable.img_cookie_velvet)
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        initViews();
        setupGreeting();
        setupForYouList();
        setupNavigation();
        setupTopBarActions();
    }

    private void initViews() {
        tvUserName  = findViewById(R.id.tvUserName);
        rvForYou    = findViewById(R.id.rvForYou);
        ivCart      = findViewById(R.id.ivCart);
        ivProfile   = findViewById(R.id.ivProfile);

        navHome     = findViewById(R.id.navHome);
        navWishlist = findViewById(R.id.navWishlist);
        navOrders   = findViewById(R.id.navOrders);
        navProfile  = findViewById(R.id.navProfile);
    }

    private void setupGreeting() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = (user != null && user.getDisplayName() != null)
                ? user.getDisplayName() : "Pelanggan";
        tvUserName.setText(name + "!");
    }

    private void setupForYouList() {
        ProductCardAdapter adapter = new ProductCardAdapter(forYouProducts, product -> {
            // Klik card → buka ProductDetail
            openProductDetail(product);
        });

        rvForYou.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        rvForYou.setAdapter(adapter);
        new LinearSnapHelper().attachToRecyclerView(rvForYou);
    }

    private void openProductDetail(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID,    String.valueOf(product.getId()));
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_NAME,  product.getName());
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_CAT,   product.getCategory());
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_PRICE, product.getPrice());
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_IMG,   product.getImageRes());
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_DESC,  "Cookie lezat pilihan Mon Amie.");
        startActivity(intent);
    }

    private void setupTopBarActions() {
        ivCart.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));
        ivProfile.setOnClickListener(v -> {
            // TODO: ProfileActivity
        });
    }

    private void setupNavigation() {
        setNavActive(navHome);
        navHome.setOnClickListener(v -> setNavActive(navHome));
        navWishlist.setOnClickListener(v -> setNavActive(navWishlist));
        navOrders.setOnClickListener(v -> setNavActive(navOrders));
        navProfile.setOnClickListener(v -> setNavActive(navProfile));
    }

    private void setNavActive(ImageView active) {
        ImageView[] all = {navHome, navWishlist, navOrders, navProfile};
        for (ImageView nav : all) {
            nav.setBackgroundResource(0); // Remove background
            nav.setColorFilter(Color.parseColor("#9E8070")); // Inactive color (brown_light)
            nav.setAlpha(0.6f);
        }
        
        // Active state
        active.setBackgroundResource(R.drawable.bg_nav_active);
        active.setColorFilter(Color.WHITE); // White icon on gold background
        active.setAlpha(1.0f);
    }
}