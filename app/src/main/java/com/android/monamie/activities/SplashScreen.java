package com.android.monamie.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.monamie.R;

public class SplashScreen extends AppCompatActivity {

    private ImageView ivLogo;

    private ImageView ivLogo2;
    private LinearLayout layoutText;
    private View layoutContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivLogo = findViewById(R.id.ivLogo);
        ivLogo2 = findViewById(R.id.ivLogo2);
        layoutText = findViewById(R.id.layoutText);
        layoutContent = findViewById(R.id.layoutContent);

        // Run Animations
        Animation fadeUp = AnimationUtils.loadAnimation(this, R.anim.splash_fade_up);
        ivLogo2.startAnimation(fadeUp);
        ivLogo.startAnimation(fadeUp);
        layoutText.startAnimation(fadeUp);

        // Auto transition after 3 seconds
        new android.os.Handler().postDelayed(this::onNextClicked, 3000);
    }

    // ══════════════════════════════════════════════════════
    //  SETUP
    // ══════════════════════════════════════════════════════

    // ACTIONS
    // ══════════════════════════════════════════════════════

    private void onNextClicked() {
        // Cek apakah user sudah login
        if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Jika sudah login, langsung ke Dashboard
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
        } else {
            // Jika belum login, ke halaman Login
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish(); // tutup splash agar tidak bisa back
    }
}