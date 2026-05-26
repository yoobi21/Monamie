package com.android.monamie.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import android.graphics.Typeface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.android.monamie.adapters.BannerAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.android.monamie.models.CartItem;
import com.android.monamie.utils.CartManager;
import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import com.android.monamie.R;
import com.android.monamie.activities.CartActivity;
import com.android.monamie.activities.DashboardActivity;
import com.android.monamie.activities.ProductDetailActivity;
import com.android.monamie.adapters.ProductCardAdapter;
import com.android.monamie.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvUserName, tvSectionTitle;
    private RecyclerView rvProducts;
    private ImageView ivCart, ivProfileTop;
    private TextView tvCartBadge;
    private ViewPager2 vpBanner;
    private LinearLayout layoutIndicators;
    private EditText etSearch;
    private TextView catAll, catCookies, catKopi;
    private BannerAdapter bannerAdapter;
    private final List<Integer> bannerImages = Arrays.asList(
            R.drawable.banner_monamie,
            R.drawable.img_cookie_velvet,
            R.drawable.img_cookie_matcha
    );

    private final List<Product> allProducts = Arrays.asList(
            new Product(1, "Red Velvet Cookie", "Kukis", 8000, R.drawable.img_cookie_velvet),
            new Product(2, "Matcha Cookie",      "Kukis", 9000, R.drawable.img_cookie_matcha),
            new Product(3, "Choco Chip Cookie",  "Kukis", 7000, R.drawable.img_cookie_choco),
            new Product(4, "Butter Cookie",      "Kukis", 6000, R.drawable.img_cookie_butter),
            new Product(5, "Americano",          "Kopi",    15000, R.drawable.coffee_americano),
            new Product(6, "Caffè Latte",        "Kopi",    12000, R.drawable.coffee_latte),
            new Product(7, "Matcha",         "Kopi",    16000, R.drawable.coffee_matcha)
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        setupGreeting();
        setupCategoryListeners();
        showCategory("All");
        setupTopBarActions();
        setupBanner();
        setupSearch();
        updateCartBadge();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartBadge();
    }

    private void updateCartBadge() {
        if (tvCartBadge == null) return;
        int count = CartManager.getInstance().getTotalQuantity();
        if (count > 0) {
            tvCartBadge.setText(String.valueOf(count));
            tvCartBadge.setVisibility(View.VISIBLE);
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }

    private void initViews(View v) {
        tvUserName = v.findViewById(R.id.tvUserName);
        tvSectionTitle = v.findViewById(R.id.tvSectionTitle);
        rvProducts = v.findViewById(R.id.rvProducts);
        ivCart = v.findViewById(R.id.ivCart);
        ivProfileTop = v.findViewById(R.id.ivProfileTop);
        tvCartBadge = v.findViewById(R.id.tvCartBadge);
        vpBanner = v.findViewById(R.id.vpBanner);
        layoutIndicators = v.findViewById(R.id.layoutIndicators);
        etSearch = v.findViewById(R.id.etSearch);
        
        catAll = v.findViewById(R.id.catAll);
        catCookies = v.findViewById(R.id.catCookies);
        catKopi = v.findViewById(R.id.catKopi);
    }

    private void setupGreeting() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name;
        if (user != null) {
            name = user.getDisplayName();
            if (name == null || name.isEmpty()) {
                name = user.getEmail();
                if (name != null && name.contains("@")) {
                    name = name.split("@")[0];
                }
            }
            if (name == null) name = "User";
        } else {
            name = "Guest";
        }
        tvUserName.setText(getString(R.string.greeting_user, name));
    }

    private void setupCategoryListeners() {
        catAll.setOnClickListener(v -> showCategory("All"));
        catCookies.setOnClickListener(v -> showCategory("Kukis"));
        catKopi.setOnClickListener(v -> showCategory("Kopi"));
    }

    private void showCategory(String category) {
        resetCategoryStyles();
        
        List<Product> filteredList = new java.util.ArrayList<>();
        Typeface boldFont = ResourcesCompat.getFont(getContext(), R.font.lato_bold);

        if (category.equals("All")) {
            catAll.setSelected(true);
            catAll.setTextColor(android.graphics.Color.parseColor("#C8873A"));
            catAll.setTypeface(boldFont);
            tvSectionTitle.setText("For You");
            filteredList.addAll(allProducts);
        } else {
            TextView selectedView = null;
            if (category.equals("Kukis")) selectedView = catCookies;
            else if (category.equals("Kopi")) selectedView = catKopi;

            if (selectedView != null) {
                selectedView.setSelected(true);
                selectedView.setTextColor(android.graphics.Color.parseColor("#C8873A"));
                selectedView.setTypeface(boldFont);
            }
            tvSectionTitle.setText(category);
            
            for (Product p : allProducts) {
                if (p.getCategory().equalsIgnoreCase(category)) {
                    filteredList.add(p);
                }
            }
        }

        updateRecyclerView(filteredList, false);
    }

    private void resetCategoryStyles() {
        int normalText = android.graphics.Color.parseColor("#9E8070");
        Typeface regularFont = ResourcesCompat.getFont(getContext(), R.font.lato_regular);
        
        catAll.setSelected(false);
        catAll.setTextColor(normalText);
        catAll.setTypeface(regularFont);
        
        catCookies.setSelected(false);
        catCookies.setTextColor(normalText);
        catCookies.setTypeface(regularFont);
        
        catKopi.setSelected(false);
        catKopi.setTextColor(normalText);
        catKopi.setTypeface(regularFont);
    }

    private void updateRecyclerView(List<Product> list, boolean isEndless) {
        ProductCardAdapter adapter = new ProductCardAdapter(list, this::openProductDetail);

        // Always use GridLayoutManager with 2 columns
        rvProducts.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(getContext(), 2));
        
        rvProducts.setAdapter(adapter);
        rvProducts.scheduleLayoutAnimation();
    }

    private void openProductDetail(Product product) {
        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID,    String.valueOf(product.getId()));
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_NAME,  product.getName());
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_CAT,   product.getCategory());
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_PRICE, product.getPrice());
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_IMG,   product.getImageRes());
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_DESC,  "Cookie lezat pilihan Mon Amie.");
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    private void setupTopBarActions() {
        ivCart.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CartActivity.class));
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        ivProfileTop.setOnClickListener(v -> {
            if (getActivity() instanceof DashboardActivity) {
                ((DashboardActivity) getActivity()).switchToProfile();
            }
        });
    }

    private void setupBanner() {
        bannerAdapter = new BannerAdapter(bannerImages, position -> {
            int index = position % bannerImages.size();
            String comboId;
            String comboName;
            int promoPrice;
            int originalPrice;
            int comboImage = bannerImages.get(index);

            if (index == 0) {
                // Banner 1: Combo Mon Amie 1 (1 Kopi + 1 Kukis)
                comboId = "PROMO_COMBO_1";
                comboName = "Combo Mon Amie 1 (1 Kopi + 1 Kukis)";
                promoPrice = 15000;
                originalPrice = 20000;
            } else if (index == 1) {
                // Banner 2: Combo Mon Amie 2 (1 Kopi + 2 Kukis)
                comboId = "PROMO_COMBO_2";
                comboName = "Combo Mon Amie 2 (1 Kopi + 2 Kukis)";
                promoPrice = 22000;
                originalPrice = 30000;
            } else {
                // Banner 3: Combo Mon Amie 3 (2 Kopi + 3 Kukis)
                comboId = "PROMO_COMBO_3";
                comboName = "Combo Mon Amie 3 (2 Kopi + 3 Kukis)";
                promoPrice = 45000;
                originalPrice = 60000;
            }

            // Agar saat diklik berulang kali jumlahnya bertambah (akumulasi)
            // kita tidak lagi menghapus item lama sebelum menambah yang baru.

            CartManager.getInstance().addItem(new CartItem(
                    comboId,
                    comboName,
                    promoPrice,
                    originalPrice,
                    1,
                    comboImage,
                    true // isPromo = true
            ));
            
            updateCartBadge();
            Toast.makeText(getContext(), comboName + " ditambahkan!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), CartActivity.class));
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        vpBanner.setAdapter(bannerAdapter);

        // Set to middle so it can scroll both ways
        int middle = Integer.MAX_VALUE / 2;
        int startPos = middle - (middle % bannerImages.size());
        vpBanner.setCurrentItem(startPos, false);

        setupIndicators(bannerImages.size());
        updateIndicators(0);

        vpBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicators(position % bannerImages.size());
            }
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void filterProducts(String query) {
        List<Product> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            // If search is empty, go back to showing the current selected category or "All"
            // For simplicity, let's just show what "showCategory" would show
            // Or just call showCategory again?
            // Let's assume searching is a global action.
            showCategory("All");
            return;
        }

        for (Product p : allProducts) {
            if (p.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(p);
            }
        }

        tvSectionTitle.setText("Hasil Pencarian: " + query);
        updateRecyclerView(filteredList, false);
        
        // Deselect categories when searching
        resetCategoryStyles();
    }

    private void setupIndicators(int count) {
        layoutIndicators.removeAllViews();
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 0, 8, 0);

        for (int i = 0; i < count; i++) {
            indicators[i] = new ImageView(getContext());
            indicators[i].setImageResource(R.drawable.indicator_dot);
            indicators[i].setLayoutParams(params);
            layoutIndicators.addView(indicators[i]);
        }
    }

    private void updateIndicators(int index) {
        int childCount = layoutIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicators.getChildAt(i);
            imageView.setSelected(i == index);
        }
    }
}