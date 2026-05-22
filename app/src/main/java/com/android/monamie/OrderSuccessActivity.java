package com.android.monamie;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.text.NumberFormat;
import java.util.Locale;

public class OrderSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        String orderId  = getIntent().getStringExtra("order_id");
        int    total    = getIntent().getIntExtra("total", 0);
        String payment  = getIntent().getStringExtra("payment");

        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("id","ID"));

        ((TextView) findViewById(R.id.tvOrderId)).setText("Order #" + orderId);
        ((TextView) findViewById(R.id.tvSuccessTotal)).setText("Rp. " + fmt.format(total));
        ((TextView) findViewById(R.id.tvSuccessPayment)).setText(payment);

        // Kembali ke Dashboard, bersihkan back stack
        ((MaterialButton) findViewById(R.id.btnBackHome)).setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}