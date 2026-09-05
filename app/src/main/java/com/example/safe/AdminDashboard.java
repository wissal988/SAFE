package com.example.safe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboard extends AppCompatActivity {

    private Button btnNotifications, btnStats;
    private LinearLayout btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        btnStats = findViewById(R.id.btnStats);
        btnNotifications = findViewById(R.id.btnNotifications);
        btnLogout = findViewById(R.id.navBtnDeconnexion);

        // Ouvrir Stats (temporaire)
        btnStats.setOnClickListener(v ->
                btnStats.setText("Statistiques cliqué !")
        );

        // Notifications
        btnNotifications.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboard.this, NotificationsActivity.class))
        );

        // Déconnexion
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminDashboard.this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
