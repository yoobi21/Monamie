package com.android.monamie.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.android.monamie.R;

public class OrderFailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_failed);

        findViewById(R.id.btnBackCart).setOnClickListener(v -> {
            finish();
        });
    }
}