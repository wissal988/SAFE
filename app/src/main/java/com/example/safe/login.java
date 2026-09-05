package com.example.safe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class login extends AppCompatActivity {

    private EditText email, password;
    private Button loginBtn, registerRedirectBtn;
    private TextView tvForgotPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("Users");

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        registerRedirectBtn = findViewById(R.id.registerRedirectBtn);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progressBar = findViewById(R.id.progressBar);

        loginBtn.setOnClickListener(v -> loginUser());
        registerRedirectBtn.setOnClickListener(v -> {
            startActivity(new Intent(login.this, register.class));
            finish();
        });

        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(login.this, ForgotPasswordActivity.class));
        });

        // Vérifier si un utilisateur est déjà connecté
        checkCurrentUser();
    }

    private void checkCurrentUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // L'utilisateur est déjà connecté, récupérer son rôle
            navigateToDashboard(currentUser.getUid());
        }
    }

    private void loginUser() {
        String mail = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (TextUtils.isEmpty(mail)) { email.setError("Email requis"); return; }
        if (TextUtils.isEmpty(pass)) { password.setError("Mot de passe requis"); return; }

        progressBar.setVisibility(ProgressBar.VISIBLE);

        mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(task -> {
            progressBar.setVisibility(ProgressBar.GONE);

            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    navigateToDashboard(user.getUid());
                }
            } else {
                Toast.makeText(login.this, "Erreur Auth: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToDashboard(String uid) {
        dbRef.child(uid).get().addOnCompleteListener(snapshotTask -> {
            if (snapshotTask.isSuccessful()) {
                DataSnapshot data = snapshotTask.getResult();
                if (data != null && data.exists()) {
                    String role = data.child("role").getValue(String.class);
                    if (role == null || role.isEmpty()) role = "user";

                    Intent intent;
                    switch (role) {
                        case "superadmin":
                            intent = new Intent(login.this, SuperAdminDashboard.class);
                            break;
                        case "admin":
                            intent = new Intent(login.this, AdminDashboard.class);
                            break;
                        case "user":
                        default:
                            intent = new Intent(login.this, UserDashboard.class);
                            break;
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(login.this, "Utilisateur non trouvé dans la DB", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(login.this, "Erreur DB: " + snapshotTask.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
