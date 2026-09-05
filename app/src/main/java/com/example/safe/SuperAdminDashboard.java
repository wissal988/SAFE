package com.example.safe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SuperAdminDashboard extends AppCompatActivity {

    private Button btnNotifications, btnStats, btnManageAdmins, btnMyInfo;
    private LinearLayout btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_dashboard);

        // Liens XML
        btnStats = findViewById(R.id.btnStats);
        btnManageAdmins = findViewById(R.id.btnManageAdmins);
        btnMyInfo = findViewById(R.id.btnMyInfo);
        btnLogout = findViewById(R.id.navBtnDeconnexion);
        btnNotifications = findViewById(R.id.btnNotifications);

        // 👉 Ouvrir l'activité des Statistiques
        btnStats.setOnClickListener(v -> {
            Intent intent = new Intent(SuperAdminDashboard.this, StatistiquesActivity.class);
            startActivity(intent);
        });

        // 👉 Notifications
        btnNotifications.setOnClickListener(v ->
                startActivity(new Intent(SuperAdminDashboard.this, NotificationsActivity.class))
        );

        // 👉 Gestion des Admins
        btnManageAdmins.setOnClickListener(v -> {
            Intent intent = new Intent(SuperAdminDashboard.this, GestionAdminsActivity.class);
            startActivity(intent);
        });

        // 👉 Mes informations
        btnMyInfo.setOnClickListener(v ->
                startActivity(new Intent(SuperAdminDashboard.this, MyInfoActivity.class))
        );

        // 👉 Déconnexion
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(SuperAdminDashboard.this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
