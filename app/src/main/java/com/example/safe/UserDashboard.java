package com.example.safe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDashboard extends AppCompatActivity {

    private TextView greetingText;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    private LinearLayout btndanger, btnamelioration, btnreclamation, btnasignalement, btnDeconnexion, btnCompte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        greetingText = findViewById(R.id.greetingText);

        // IMPORTANT : ce sont des LinearLayout, pas des Button !
        btndanger = findViewById(R.id.btnDanger);
        btnamelioration = findViewById(R.id.btnAmelioration);
        btnreclamation = findViewById(R.id.btnReclamation);
        btnasignalement = findViewById(R.id.btnMesSignalements);
        btnDeconnexion = findViewById(R.id.navBtnDeconnexion);
        btnCompte = findViewById(R.id.navBtnProfil);

        loadUserName();

        btndanger.setOnClickListener(v -> startActivity(new Intent(this, signalUser.class)));
        btnamelioration.setOnClickListener(v -> startActivity(new Intent(this, ameliorationUser.class)));
        btnreclamation.setOnClickListener(v -> startActivity(new Intent(this, reclamationUser.class)));
        btnasignalement.setOnClickListener(v -> startActivity(new Intent(this, signalement.class)));
        btnCompte.setOnClickListener(v -> startActivity(new Intent(this, MyInfoActivity.class)));
        btnDeconnexion.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, login.class));
            finish();
        });
    }

    private void loadUserName() {
        String userId = mAuth.getCurrentUser().getUid();

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("prenom").getValue(String.class);
                if (name != null) greetingText.setText("Bonjour " + name + " !");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
