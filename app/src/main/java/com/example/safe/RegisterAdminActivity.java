package com.example.safe;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterAdminActivity extends AppCompatActivity {

    EditText etNom, etPrenom, etTelephone, etEmail, etPassword;
    Button btnSave;
    ProgressBar progressBar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        etTelephone = findViewById(R.id.etTelephone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etMotDePasse);
        btnSave = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        btnSave.setOnClickListener(v -> saveAdmin());
    }

    private void saveAdmin() {
        String nom = etNom.getText().toString();
        String prenom = etPrenom.getText().toString();
        String tel = etTelephone.getText().toString();
        String email = etEmail.getText().toString();
        String pass = etPassword.getText().toString();

        if (TextUtils.isEmpty(nom)) { etNom.setError("Requis"); return; }
        if (TextUtils.isEmpty(prenom)) { etPrenom.setError("Requis"); return; }
        if (TextUtils.isEmpty(tel)) { etTelephone.setError("Requis"); return; }
        if (TextUtils.isEmpty(email)) { etEmail.setError("Requis"); return; }
        if (TextUtils.isEmpty(pass)) { etPassword.setError("Requis"); return; }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);

            if (task.isSuccessful()) {

                String uid = mAuth.getCurrentUser().getUid();

                HashMap<String, Object> map = new HashMap<>();
                map.put("nom", nom);
                map.put("prenom", prenom);
                map.put("telephone", tel);
                map.put("email", email);
                map.put("role", "admin");

                FirebaseDatabase.getInstance().getReference("Users")
                        .child(uid)
                        .setValue(map);

                Toast.makeText(this, "Admin ajouté", Toast.LENGTH_SHORT).show();
                finish();

            } else {
                Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
