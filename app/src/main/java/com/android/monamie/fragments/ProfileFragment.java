package com.android.monamie.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.android.monamie.R;
import com.android.monamie.activities.Login;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private TextView tvEmailDisplay, tvProfileStatus;
    private EditText etEmail, etPassword;
    private MaterialButton btnUpdate, btnLogout;
    private ProgressBar pbProfile;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        mAuth = FirebaseAuth.getInstance();
        initViews(view);
        loadUserData();
        setupActions();
        
        return view;
    }

    private void initViews(View v) {
        tvEmailDisplay = v.findViewById(R.id.tvProfileEmailDisplay);
        tvProfileStatus = v.findViewById(R.id.tvProfileStatus);
        etEmail = v.findViewById(R.id.etProfileEmail);
        etPassword = v.findViewById(R.id.etProfilePassword);
        btnUpdate = v.findViewById(R.id.btnUpdateProfile);
        btnLogout = v.findViewById(R.id.btnLogout);
        pbProfile = v.findViewById(R.id.pbProfile);
        
        tvProfileStatus.setVisibility(View.GONE);
        pbProfile.setVisibility(View.GONE);
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.getEmail() != null) {
                tvEmailDisplay.setText(user.getEmail());
                etEmail.setText(user.getEmail());
            }

            user.reload().addOnCompleteListener(task -> {
                if (isAdded() && user.getEmail() != null) {
                    tvEmailDisplay.setText(user.getEmail());
                    etEmail.setText(user.getEmail());
                }
            });
        }
    }

    private void setupActions() {
        btnUpdate.setOnClickListener(v -> {
            String newEmail = etEmail.getText().toString().trim();
            String newPassword = etPassword.getText().toString().trim();
            
            if (newEmail.isEmpty()) {
                etEmail.setError("Email tidak boleh kosong");
                return;
            }
            
            showReauthDialog(newEmail, newPassword);
        });

        btnLogout.setOnClickListener(v -> logoutAndRedirect());
    }

    private void logoutAndRedirect() {
        if (mAuth != null) mAuth.signOut();
        Intent intent = new Intent(getActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void showReauthDialog(String newEmail, String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        EditText etConfirm = new EditText(getContext());
        etConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etConfirm.setHint("Masukkan Password Saat Ini");

        FrameLayout container = new FrameLayout(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(60, 20, 60, 0);
        etConfirm.setLayoutParams(params);
        container.addView(etConfirm);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Verifikasi Akun")
                .setMessage("Demi keamanan, masukkan password Anda untuk mengizinkan perubahan email/password.")
                .setView(container)
                .setPositiveButton("Konfirmasi", (dialog, which) -> {
                    String pw = etConfirm.getText().toString().trim();
                    if (pw.isEmpty()) return;
                    
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), pw);
                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            performDirectUpdate(newEmail, newPassword);
                        } else {
                            Toast.makeText(getContext(), "Password salah!", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void performDirectUpdate(String newEmail, String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        pbProfile.setVisibility(View.VISIBLE);
        tvProfileStatus.setVisibility(View.VISIBLE);
        tvProfileStatus.setText("Sedang menyimpan...");

        // 1. Update Password (jika diisi)
        if (!newPassword.isEmpty()) {
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Password diperbarui", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 2. Update Email secara langsung (Direct Flow)
        if (!newEmail.equalsIgnoreCase(user.getEmail())) {
            user.updateEmail(newEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    tvProfileStatus.setText("Berhasil! Mengalihkan...");
                    Toast.makeText(getContext(), "Email berhasil diubah. Silakan login kembali.", Toast.LENGTH_LONG).show();
                    
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (isAdded()) logoutAndRedirect();
                    }, 2000);
                } else {
                    pbProfile.setVisibility(View.GONE);
                    String error = task.getException() != null ? task.getException().getMessage() : "Gagal";
                    
                    if (error.contains("verify the new email")) {
                        tvProfileStatus.setText("Gagal: Proteksi Firebase Aktif");
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Update Email Diblokir")
                                .setMessage("Firebase melarang update email langsung.\n\n" +
                                        "SOLUSI: Matikan opsi 'User must verify their email before updating it' di Firebase Console > Authentication > Settings > User actions.")
                                .setPositiveButton("Ok", null)
                                .show();
                    } else {
                        tvProfileStatus.setText("Error: " + error);
                        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            pbProfile.setVisibility(View.GONE);
            tvProfileStatus.setText("Profil diperbarui.");
        }
    }
}