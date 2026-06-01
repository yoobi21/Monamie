package com.android.monamie.activities;

import android.os.Handler;
import android.os.Looper;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.monamie.R;
import com.android.monamie.models.CartItem;
import com.android.monamie.utils.CartManager;
import com.android.monamie.utils.ProductManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import android.view.ViewGroup;
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
    private TextView       tvSubtotal, tvOngkir, tvTotal, tvOriginalTotal, tvLoadingStatus, tvVAInfo, btnCopyVA;
    private MaterialButton btnOrder, btnCancelOrder, btnConfirmPayment;
    private View           layoutLoading, layoutVA;
    private NumberFormat   fmt = NumberFormat.getNumberInstance(new Locale("id","ID"));
    
    private Handler countdownHandler = new Handler(Looper.getMainLooper());
    private Runnable countdownRunnable;
    private int secondsRemaining = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        etNama     = findViewById(R.id.etNamaPenerima);
        etAlamat   = findViewById(R.id.etAlamatLengkap);
        etNoHp     = findViewById(R.id.etNoHp);
        rgPayment  = findViewById(R.id.rgPayment);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvOngkir   = findViewById(R.id.tvOngkir);
        tvTotal    = findViewById(R.id.tvCheckoutTotal);
        tvOriginalTotal = findViewById(R.id.tvOriginalTotal);
        btnOrder   = findViewById(R.id.btnOrder);
        
        // Strikethrough for original price
        if (tvOriginalTotal != null) {
            tvOriginalTotal.setPaintFlags(tvOriginalTotal.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        }

        layoutLoading   = findViewById(R.id.layoutLoading);
        tvLoadingStatus = findViewById(R.id.tvLoadingStatus);
        tvVAInfo        = findViewById(R.id.tvVAInfo);
        layoutVA        = findViewById(R.id.layoutVA);
        btnCancelOrder  = findViewById(R.id.btnCancelOrder);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        btnCopyVA       = findViewById(R.id.btnCopyVA);

        btnCopyVA.setOnClickListener(v -> {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("VA Number", "8873081290679905");
            clipboard.setPrimaryClip(clip);
            android.widget.Toast.makeText(this, "Nomor VA disalin", android.widget.Toast.LENGTH_SHORT).show();
        });

        btnCancelOrder.setOnClickListener(v -> cancelOrderProcess());
        btnConfirmPayment.setOnClickListener(v -> {
            // Langsung eksekusi setelah user klik bayar
            if (countdownHandler != null && countdownRunnable != null) {
                countdownHandler.removeCallbacks(countdownRunnable);
            }
            int subtotal = CartManager.getInstance().getTotal();
            executeOrder(subtotal, subtotal + ONGKIR, etNama.getText().toString(), etAlamat.getText().toString(), etNoHp.getText().toString(), "Transfer Bank");
        });

        findViewById(R.id.ivCheckoutBack).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        int subtotal = CartManager.getInstance().getTotal();
        int total    = subtotal + ONGKIR;
        int originalTotal = CartManager.getInstance().getOriginalTotal() + ONGKIR;

        tvSubtotal.setText("Rp. " + fmt.format(subtotal));
        tvOngkir.setText("Rp. " + fmt.format(ONGKIR));
        tvTotal.setText("Rp. " + fmt.format(total));

        if (total < originalTotal) {
            tvOriginalTotal.setText("Rp. " + fmt.format(originalTotal));
            tvOriginalTotal.setVisibility(View.VISIBLE);
        } else {
            tvOriginalTotal.setVisibility(View.GONE);
        }

        // Adjust for system navigation bar (Edge-to-Edge)
        ViewCompat.setOnApplyWindowInsetsListener(btnOrder, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            // Memberikan margin bawah yang aman (16dp + tinggi system navigation bar)
            mlp.bottomMargin = insets.bottom + (int) (16 * getResources().getDisplayMetrics().density);
            v.setLayoutParams(mlp);
            return windowInsets;
        });

        btnOrder.setOnClickListener(v -> submitOrder(subtotal, total));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countdownHandler != null && countdownRunnable != null) {
            countdownHandler.removeCallbacks(countdownRunnable);
        }
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

        startLoadingCountdown(subtotal, total, nama, alamat, noHp, paymentMethod);
    }

    private void startLoadingCountdown(int subtotal, int total, String nama, String alamat, String noHp, String paymentMethod) {
        layoutLoading.setVisibility(View.VISIBLE);
        btnCancelOrder.setVisibility(View.VISIBLE);
        
        if (paymentMethod.equals("Transfer Bank")) {
            secondsRemaining = 30;
            layoutVA.setVisibility(View.VISIBLE);
            btnConfirmPayment.setVisibility(View.VISIBLE);
            tvLoadingStatus.setText("Selesaikan Pembayaran (" + secondsRemaining + "s)");
        } else {
            secondsRemaining = 6;
            layoutVA.setVisibility(View.GONE);
            btnConfirmPayment.setVisibility(View.GONE);
            tvLoadingStatus.setText("Memproses Pesanan (" + secondsRemaining + "s)");
        }

        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                secondsRemaining--;
                if (secondsRemaining > 0) {
                    if (paymentMethod.equals("Transfer Bank")) {
                        tvLoadingStatus.setText("Selesaikan Pembayaran (" + secondsRemaining + "s)");
                    } else {
                        tvLoadingStatus.setText("Memproses Pesanan (" + secondsRemaining + "s)");
                    }
                    countdownHandler.postDelayed(this, 1000);
                } else {
                    if (paymentMethod.equals("Transfer Bank")) {
                        // Jika waktu habis dan belum bayar, anggap batal/gagal
                        cancelOrderProcess();
                    } else {
                        executeOrder(subtotal, total, nama, alamat, noHp, paymentMethod);
                    }
                }
            }
        };
        countdownHandler.postDelayed(countdownRunnable, 1000);
    }

    private void cancelOrderProcess() {
        if (countdownHandler != null && countdownRunnable != null) {
            countdownHandler.removeCallbacks(countdownRunnable);
        }
        layoutLoading.setVisibility(View.GONE);
        btnOrder.setEnabled(true);
        btnOrder.setText("Buat Pesanan");
    }

    private void executeOrder(int subtotal, int total, String nama, String alamat, String noHp, String paymentMethod) {
        tvLoadingStatus.setText("Mengirim Pesanan...");
        btnCancelOrder.setVisibility(View.GONE);
        btnConfirmPayment.setVisibility(View.GONE);
        layoutVA.setVisibility(View.GONE);

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
        order.put("status", "selesai");
        order.put("createdAt", new Date().toString());

        Map<String, Object> itemsMap = new HashMap<>();
        for (CartItem item : CartManager.getInstance().getItems()) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("name", item.getName());
            itemData.put("price", item.getPrice());
            itemData.put("qty", item.getQuantity());
            itemData.put("subtotal", item.getSubtotal());
            itemData.put("imageRes", item.getImageRes());
            itemsMap.put(item.getProductId(), itemData);
        }
        order.put("items", itemsMap);

        // WA Message
        StringBuilder sb = new StringBuilder();
        sb.append("*PESANAN BARU - MON AMIE*\n\n");
        sb.append("🆔 *ID Pesanan:* ").append(orderId).append("\n");
        sb.append("👤 *Nama:* ").append(nama).append("\n");
        sb.append("📞 *No. HP:* ").append(noHp).append("\n");
        sb.append("📍 *Alamat:* ").append(alamat).append("\n\n");
        sb.append("*Detail Menu:*\n");
        for (CartItem item : CartManager.getInstance().getItems()) {
            sb.append("- ").append(item.getName()).append(" (x").append(item.getQuantity()).append(")\n");
        }
        sb.append("\n💰 *Total Bayar: Rp ").append(fmt.format(total)).append("*\n");
        sb.append("💳 *Metode:* ").append(paymentMethod);
        String messageText = sb.toString();

        Handler timeoutHandler = new Handler(Looper.getMainLooper());
        final boolean[] isProcessed = {false};
        
        Runnable timeoutRunnable = () -> {
            if (!isProcessed[0]) {
                isProcessed[0] = true;
                layoutLoading.setVisibility(View.GONE);
                btnOrder.setEnabled(true);
                btnOrder.setText("Buat Pesanan");
                Intent intent = new Intent(this, OrderFailedActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        };
        timeoutHandler.postDelayed(timeoutRunnable, 5000);

        ref.setValue(order)
                .addOnSuccessListener(unused -> {
                    if (isProcessed[0]) return;
                    isProcessed[0] = true;
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    
                    // Kurangi stok produk
                    for (CartItem item : CartManager.getInstance().getItems()) {
                        ProductManager.getInstance().decreaseStock(item.getName(), item.getQuantity());
                    }
                    
                    CartManager.getInstance().clear();
                    
                    Intent intent = new Intent(this, OrderSuccessActivity.class);
                    intent.putExtra("order_id", orderId);
                    intent.putExtra("total", total);
                    intent.putExtra("payment", paymentMethod);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    if (paymentMethod.equals("COD")) {
                        try {
                            String adminPhone = "Your Number Phone";
                            String url = "https://api.whatsapp.com/send?phone=" + adminPhone + "&text=" + Uri.encode(messageText);
                            Intent waIntent = new Intent(Intent.ACTION_VIEW);
                            waIntent.setData(Uri.parse(url));
                            startActivity(waIntent);
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                })
                .addOnFailureListener(e -> {
                    if (isProcessed[0]) return;
                    isProcessed[0] = true;
                    timeoutHandler.removeCallbacks(timeoutRunnable);
                    layoutLoading.setVisibility(View.GONE);
                    btnOrder.setEnabled(true);
                    btnOrder.setText("Buat Pesanan");
                });
    }
}
