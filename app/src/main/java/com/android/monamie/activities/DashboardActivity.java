package com.android.monamie.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.android.monamie.R;
import com.android.monamie.fragments.HistoryFragment;
import com.android.monamie.fragments.HomeFragment;
import com.android.monamie.fragments.ProfileFragment;
import com.android.monamie.fragments.TeamFragment;
import com.android.monamie.utils.CartManager;

public class DashboardActivity extends AppCompatActivity {

    private ImageView navHome, navOrders, navTeam, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        initViews();
        setupNavigation();

        // Load Home Fragment by default
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Promo items are now persistent
    }

    private void initViews() {
        navHome = findViewById(R.id.navHome);
        navOrders = findViewById(R.id.navOrders);
        navTeam = findViewById(R.id.navTeam);
        navProfile = findViewById(R.id.navProfile);
    }

    private void setupNavigation() {
        setNavActive(navHome);
        
        navHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment(), true);
            setNavActive(navHome);
        });
        
        navOrders.setOnClickListener(v -> {
            loadFragment(new HistoryFragment(), true);
            setNavActive(navOrders);
        });

        navTeam.setOnClickListener(v -> {
            loadFragment(new TeamFragment(), true);
            setNavActive(navTeam);
        });
        
        navProfile.setOnClickListener(v -> {
            loadFragment(new ProfileFragment(), true);
            setNavActive(navProfile);
        });
    }

    public void switchToProfile() {
        loadFragment(new ProfileFragment(), true);
        setNavActive(navProfile);
    }

    private void loadFragment(Fragment fragment, boolean animate) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        
        if (animate) {
            transaction.setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            );
        }
        
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void setNavActive(ImageView active) {
        ImageView[] all = {navHome, navOrders, navTeam, navProfile};
        for (ImageView nav : all) {
            if (nav != null) {
                nav.setBackgroundResource(0);
                nav.setColorFilter(Color.parseColor("#9E8070"));
                nav.setAlpha(0.6f);
            }
        }

        if (active != null) {
            active.setBackgroundResource(R.drawable.bg_nav_active);
            active.setColorFilter(Color.WHITE);
            active.setAlpha(1.0f);
        }
    }
}