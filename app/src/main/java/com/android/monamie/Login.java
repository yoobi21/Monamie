package com.android.monamie;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private TextInputEditText etEmail;      // Ganti nama dari etUsername
    private TextInputEditText etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Inisialisasi Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etUsername);      // Sesuai ID di XML kamu
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validasi input
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email tidak boleh kosong");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }

        // Login ke Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login berhasil
                        Toast.makeText(Login.this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, DashboardActivity.class));
                        finish();
                    } else {
                        // Login gagal
                        String errorMsg = task.getException() != null ?
                                task.getException().getMessage() : "Login gagal!";
                        Toast.makeText(Login.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}