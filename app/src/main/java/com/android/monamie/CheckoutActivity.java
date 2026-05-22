package com.android.monamie;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    private static final int ONGKIR = 5000;

    private EditText       etNama, etAlamat, etNoHp;
    private RadioGroup     rgPayment;
    private TextView       tvSubtotal, tvOngkir, tvTotal;
    private MaterialButton btnOrder;
    private NumberFormat   fmt = NumberFormat.getNumberInstance(new Locale("id","ID"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        etNama     = findViewById(R.id.etAlamat);
        etAlamat   = findViewById(R.id.etAlamatDetail);
        etNoHp     = findViewById(R.id.etNoHp);
        rgPayment  = findViewById(R.id.rgPayment);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvOngkir   = findViewById(R.id.tvOngkir);
        tvTotal    = findViewById(R.id.tvCheckoutTotal);
        btnOrder   = findViewById(R.id.btnOrder);

        findViewById(R.id.ivCheckoutBack).setOnClickListener(v -> finish());

        int subtotal = CartManager.getInstance().getTotal();
        int total    = subtotal + ONGKIR;
        tvSubtotal.setText("Rp. " + fmt.format(subtotal));
        tvOngkir.setText("Rp. " + fmt.format(ONGKIR));
        tvTotal.setText("Rp. " + fmt.format(total));

        btnOrder.setOnClickListener(v -> submitOrder(subtotal, total));
    }

    private void submitOrder(int subtotal, int total) {
        String nama   = etNama.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String noHp   = etNoHp.getText().toString().trim();

        if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(alamat) || TextUtils.isEmpty(noHp)) {
            etNama.setError(TextUtils.isEmpty(nama) ? "Wajib diisi" : null);
            etAlamat.setError(TextUtils.isEmpty(alamat) ? "Wajib diisi" : null);
            etNoHp.setError(TextUtils.isEmpty(noHp) ? "Wajib diisi" : null);
            return;
        }

        int selectedPayment = rgPayment.getCheckedRadioButtonId();
        String paymentMethod = (selectedPayment == R.id.rbCOD) ? "COD" : "Transfer Bank";

        btnOrder.setEnabled(false);
        btnOrder.setText("Memproses...");

        // Simpan ke Firebase
        String uid     = FirebaseAuth.getInstance().getUid();
        String orderId = "MON-" + new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("orders").child(orderId);

        Map<String, Object> order = new HashMap<>();
        order.put("orderId", orderId);
        order.put("userId", uid);
        order.put("nama", nama);
        order.put("alamat", alamat);
        order.put("noHp", noHp);
        order.put("payment", paymentMethod);
        order.put("subtotal", subtotal);
        order.put("ongkir", ONGKIR);
        order.put("total", total);
        order.put("status", "pending");
        order.put("createdAt", new Date().toString());

        // Masukkan item
        Map<String, Object> itemsMap = new HashMap<>();
        for (CartItem item : CartManager.getInstance().getItems()) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("name", item.getName());
            itemData.put("price", item.getPrice());
            itemData.put("qty", item.getQuantity());
            itemData.put("subtotal", item.getSubtotal());
            itemsMap.put(item.getProductId(), itemData);
        }
        order.put("items", itemsMap);

        ref.setValue(order)
                .addOnSuccessListener(unused -> {
                    CartManager.getInstance().clear();  // kosongkan cart

                    Intent intent = new Intent(this, OrderSuccessActivity.class);
                    intent.putExtra("order_id", orderId);
                    intent.putExtra("total", total);
                    intent.putExtra("payment", paymentMethod);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    btnOrder.setEnabled(true);
                    btnOrder.setText("Buat Pesanan");
                    // TODO: tampilkan snackbar error
                });
    }
}