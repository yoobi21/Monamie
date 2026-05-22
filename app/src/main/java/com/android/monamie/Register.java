package com.android.monamie;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private Button btnBuatAkun;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register); // Gunakan XML Anda

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnBuatAkun = findViewById(R.id.btnLogin); // ID btnLogin tapi teksnya "Buat Akun"
        tvLogin = findViewById(R.id.tvRegister); // ID tvRegister tapi teksnya "Login"
    }

    private void setupClickListeners() {
        btnBuatAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegister();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kembali ke halaman Login
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void handleRegister() {
        String email = etUsername.getText().toString().trim(); // pake email, bukan username
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Lengkapi data!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simpan data registrasi (contoh sederhana)
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registrasi berhasil!
                        Toast.makeText(Register.this, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show();

                        // Simpan user ke Firestore (opsional, buat profil)
                        String userId = mAuth.getCurrentUser().getUid();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        Map<String, Object> user = new HashMap<>();
                        user.put("email", email);
                        user.put("userId", userId);

                        db.collection("users").document(userId).set(user);

                        // Pindah ke Login
                        startActivity(new Intent(Register.this, Login.class));
                        finish();
                    } else {
                        Toast.makeText(Register.this, "Gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}