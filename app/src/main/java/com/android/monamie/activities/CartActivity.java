package com.android.monamie.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.monamie.R;
import com.android.monamie.adapters.CartAdapter;
import com.android.monamie.utils.CartManager;
import com.google.android.material.button.MaterialButton;
import java.text.NumberFormat;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCart;
    private LinearLayout llEmpty;
    private TextView     tvTotal;
    private MaterialButton btnCheckout;
    private CartAdapter  adapter;
    private NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("id","ID"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rvCart      = findViewById(R.id.rvCart);
        llEmpty     = findViewById(R.id.llCartEmpty);
        tvTotal     = findViewById(R.id.tvCartTotal);
        btnCheckout = findViewById(R.id.btnCheckout);

        findViewById(R.id.ivCartBack).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        adapter = new CartAdapter(CartManager.getInstance().getItems(), this::refreshUI);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(adapter);

        refreshUI();

        btnCheckout.setOnClickListener(v -> {
            startActivity(new Intent(this, CheckoutActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    @Override protected void onResume() { super.onResume(); refreshUI(); }

    private void refreshUI() {
        boolean empty = CartManager.getInstance().getCount() == 0;
        rvCart.setVisibility(empty ? View.GONE : View.VISIBLE);
        llEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        btnCheckout.setEnabled(!empty);

        int total = CartManager.getInstance().getTotal();
        tvTotal.setText("Rp. " + fmt.format(total));
        if (!empty) adapter.notifyDataSetChanged();
    }
}