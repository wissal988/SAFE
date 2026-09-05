package com.example.safe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {

    private EditText etNom, etPrenom, etTelephone, etEmail, etMotDePasse;
    private Button btnRegister;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        etTelephone = findViewById(R.id.etTelephone);
        etEmail = findViewById(R.id.etEmail);
        etMotDePasse = findViewById(R.id.etMotDePasse);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    // ===========================================================
    //   VALIDATION AVANCÉE DU MOT DE PASSE (message dynamique)
    // ===========================================================
    private String getPasswordErrors(String password) {
        StringBuilder errors = new StringBuilder();

        if (password.length() < 8)
            errors.append("• Minimum 8 caractères\n");

        if (!password.matches(".*[A-Z].*"))
            errors.append("• Au moins une lettre majuscule\n");

        if (!password.matches(".*[0-9].*"))
            errors.append("• Au moins un chiffre\n");

        if (!password.matches(".*[@#$%^&+=!?.*].*"))
            errors.append("• Au moins un caractère spécial (@, #, $, ?, !, ...)\n");

        return errors.toString();
    }

    // ===========================================================
    //   INSCRIPTION UTILISATEUR
    // ===========================================================
    private void registerUser() {
        String sNom = etNom.getText().toString().trim();
        String sPrenom = etPrenom.getText().toString().trim();
        String sTelephone = etTelephone.getText().toString().trim();
        String sEmail = etEmail.getText().toString().trim();
        String sMotDePasse = etMotDePasse.getText().toString().trim();

        // Vérification des champs obligatoires
        if (TextUtils.isEmpty(sNom)) { etNom.setError("Nom requis"); return; }
        if (TextUtils.isEmpty(sPrenom)) { etPrenom.setError("Prénom requis"); return; }
        if (TextUtils.isEmpty(sTelephone)) { etTelephone.setError("Téléphone requis"); return; }
        if (TextUtils.isEmpty(sEmail)) { etEmail.setError("Email requis"); return; }

        // ================================
        // Vérification email correct 🔥
        // ================================
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            etEmail.setError("Email incorrect");
            Toast.makeText(this, "Email incorrect", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(sMotDePasse)) {
            etMotDePasse.setError("Mot de passe requis");
            return;
        }

        // ================================
        // Vérification dynamique du mot de passe
        // ================================
        String passwordErrors = getPasswordErrors(sMotDePasse);

        if (!passwordErrors.isEmpty()) {
            etMotDePasse.setError("Mot de passe invalide");

            Toast.makeText(this,
                    "Veuillez corriger :\n" + passwordErrors,
                    Toast.LENGTH_LONG).show();

            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // ================================
        // Création Firebase Auth
        // ================================
        mAuth.createUserWithEmailAndPassword(sEmail, sMotDePasse)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {

                        String uid = mAuth.getCurrentUser().getUid();

                        // Enregistrement dans Realtime DB
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("nom", sNom);
                        userMap.put("prenom", sPrenom);
                        userMap.put("telephone", sTelephone);
                        userMap.put("email", sEmail);
                        userMap.put("role", "user");  // rôle par défaut

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(uid)
                                .setValue(userMap)
                                .addOnCompleteListener(taskDb -> {

                                    if (taskDb.isSuccessful()) {
                                        Toast.makeText(this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(register.this, UserDashboard.class));
                                        finish();
                                    }
                                });

                    } else {
                        Toast.makeText(this, "Erreur : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
