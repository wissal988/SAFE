package com.example.safe;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionAdminsActivity extends AppCompatActivity {

    private Button btnAddAdmin;
    private RecyclerView recyclerView;

    private List<Admin> adminList;
    private AdminAdapter adapter;

    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_admins);

        btnAddAdmin = findViewById(R.id.btnAddAdmin);
        recyclerView = findViewById(R.id.recyclerAdmins);

        adminList = new ArrayList<>();
        adapter = new AdminAdapter(this, adminList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference("Users");

        loadAdmins();

        btnAddAdmin.setOnClickListener(v -> showAddAdminDialog());
    }

    // Charger les admins
    private void loadAdmins() {
        dbRef.orderByChild("role").equalTo("admin")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        adminList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Admin admin = ds.getValue(Admin.class);
                            admin.uid = ds.getKey();
                            adminList.add(admin);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    // Ajouter admin
    private void showAddAdminDialog() {

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_admin, null);

        EditText etNom = dialogView.findViewById(R.id.etNom);
        EditText etPrenom = dialogView.findViewById(R.id.etPrenom);
        EditText etTelephone = dialogView.findViewById(R.id.etTelephone);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etPassword = dialogView.findViewById(R.id.etPassword);

        new AlertDialog.Builder(this)
                .setTitle("Ajouter Admin")
                .setView(dialogView)
                .setPositiveButton("Ajouter", (dialog, which) -> {

                    String nom = etNom.getText().toString();
                    String prenom = etPrenom.getText().toString();
                    String tel = etTelephone.getText().toString();
                    String email = etEmail.getText().toString();
                    String pass = etPassword.getText().toString();

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                            .addOnSuccessListener(auth -> {

                                String uid = auth.getUser().getUid();

                                Map<String, Object> map = new HashMap<>();
                                map.put("nom", nom);
                                map.put("prenom", prenom);
                                map.put("telephone", tel);
                                map.put("email", email);
                                map.put("role", "admin");

                                dbRef.child(uid).setValue(map);

                                Toast.makeText(this, "Admin ajouté", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}
