package com.android.monamie;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;

public class SplashScreen extends AppCompatActivity {

    // ─── Views ───────────────────────────────────────────
    private Button btnNext;

    // ─────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupWindowInsets();
        setupViews();
        setupListeners();
    }

    // ══════════════════════════════════════════════════════
    //  SETUP
    // ══════════════════════════════════════════════════════

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars =
                            insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );
                    return insets;
                }
        );
    }

    private void setupViews() {
        btnNext = findViewById(R.id.btnNext);
    }

    private void setupListeners() {
        btnNext.setOnClickListener(v -> onNextClicked());
    }

    // ══════════════════════════════════════════════════════
    //  ACTIONS
    // ══════════════════════════════════════════════════════

    private void onNextClicked() {
        // Arahkan ke halaman berikutnya
         Intent intent = new Intent(this, Login.class);
         startActivity(intent);
         finish(); // tutup splash agar tidak bisa back
    }
}