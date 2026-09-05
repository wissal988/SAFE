package com.example.safe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MyInfoActivity extends AppCompatActivity {

    private TextView tvNom, tvPrenom, tvTelephone, tvEmail;
    private Button btnUpdate;
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        tvNom = findViewById(R.id.tvNom);
        tvPrenom = findViewById(R.id.tvPrenom);
        tvTelephone = findViewById(R.id.tvTelephone);
        tvEmail = findViewById(R.id.tvEmail);
        btnUpdate = findViewById(R.id.btnUpdateInfo);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getCurrentUser().getUid());

        // Charger les infos actuelles
        loadUserInfo();

        btnUpdate.setOnClickListener(v -> openUpdateDialog());
    }

    private void loadUserInfo() {
        dbRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult().exists()) {
                tvNom.setText("Nom: " + task.getResult().child("nom").getValue(String.class));
                tvPrenom.setText("Prénom: " + task.getResult().child("prenom").getValue(String.class));
                tvTelephone.setText("Téléphone: " + task.getResult().child("telephone").getValue(String.class));
                tvEmail.setText("Email: " + task.getResult().child("email").getValue(String.class));
            }
        });
    }

    private void openUpdateDialog() {
        // Inflater le layout custom
        View view = LayoutInflater.from(this).inflate(R.layout.update_info_dialog, null);

        EditText etNom = view.findViewById(R.id.etUpdateNom);
        EditText etPrenom = view.findViewById(R.id.etUpdatePrenom);
        EditText etTelephone = view.findViewById(R.id.etUpdateTelephone);
        EditText etEmail = view.findViewById(R.id.etUpdateEmail);
        Button btnUpdateDialog = view.findViewById(R.id.btnUpdateDialog); // bouton ajouté dans layout

        // Pré-remplir les infos
        etNom.setText(tvNom.getText().toString().replace("Nom: ", ""));
        etPrenom.setText(tvPrenom.getText().toString().replace("Prénom: ", ""));
        etTelephone.setText(tvTelephone.getText().toString().replace("Téléphone: ", ""));
        etEmail.setText(tvEmail.getText().toString().replace("Email: ", ""));

        // Créer le dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.show();

        // Listener du bouton update
        btnUpdateDialog.setOnClickListener(v -> {
            String newNom = etNom.getText().toString().trim();
            String newPrenom = etPrenom.getText().toString().trim();
            String newPhone = etTelephone.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();

            Map<String, Object> updates = new HashMap<>();
            updates.put("nom", newNom);
            updates.put("prenom", newPrenom);
            updates.put("telephone", newPhone);
            updates.put("email", newEmail);

            dbRef.updateChildren(updates).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Toast.makeText(this, "Informations mises à jour ✔️", Toast.LENGTH_SHORT).show();
                    tvNom.setText("Nom: " + newNom);
                    tvPrenom.setText("Prénom: " + newPrenom);
                    tvTelephone.setText("Téléphone: " + newPhone);
                    tvEmail.setText("Email: " + newEmail);
                    dialog.dismiss(); // fermer le dialog
                } else {
                    Toast.makeText(this, "Erreur: " + task.getException(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
