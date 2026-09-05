package com.example.safe;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.safe.R;

public class signalement extends AppCompatActivity {

    private LinearLayout navBtnProfil;
    private LinearLayout navBtnAccueil;
    private LinearLayout navBtnDeconnexion;
    private LinearLayout btnListePropositions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signalement);

        // Boutons du centre
        LinearLayout btnVoirSignalements = findViewById(R.id.btnVoirSignalements);
        LinearLayout btnListeReclamations = findViewById(R.id.btnListeReclamations);
        LinearLayout btnListePropositions = findViewById(R.id.btnListePropositions);

        // Boutons de la barre de navigation du bas
        navBtnProfil = findViewById(R.id.navBtnProfil);
        navBtnAccueil = findViewById(R.id.navBtnAccueil);
        navBtnDeconnexion = findViewById(R.id.navBtnDeconnexion);

        // 1. Liste des signalements
        btnVoirSignalements.setOnClickListener(v -> {
            Toast.makeText(this, "Ouverture Liste des signalements", Toast.LENGTH_SHORT).show();
            // Si vous restez sur cette page pour l'instant, c'est ok. Sinon :
            startActivity(new Intent(signalement.this, listeSignalUser.class));
        });

        // 2. Liste des réclamations
        btnListeReclamations.setOnClickListener(v -> {
            Toast.makeText(this, "Ouverture Liste des réclamations", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(signalement.this, listReclamationUser.class));
        });

        // 3. Liste des propositions
        btnListePropositions.setOnClickListener(v -> {
            Toast.makeText(this, "Ouverture Liste des propositions", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(signalement.this, listAmeliorationUser.class));
        });

        // 4. NAVIGATION : Mon Compte (Profil)
        navBtnProfil.setOnClickListener(v -> {
            Toast.makeText(this, "Ouverture Mon Compte (Profil)", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(signalement.this, compte.class));
        });

        // 5. NAVIGATION : Accueil (HOME) - CODE MODIFIÉ
        navBtnAccueil.setOnClickListener(v -> {
            Toast.makeText(this, "Redirection vers la page d'Accueil (Home)", Toast.LENGTH_SHORT).show();
            // Démarrer la nouvelle activité HomeActivity
            startActivity(new Intent(signalement.this, UserDashboard.class));
            // Optionnel : finish() si vous ne voulez pas garder cette activité dans la pile
        });

        // 6. NAVIGATION : Déconnexion
        navBtnDeconnexion.setOnClickListener(v -> {
            showDeconnexionDialog();
        });
    }

    // Méthode de dialogue de déconnexion (inchangée)
    private void showDeconnexionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_deconnexion, null);
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        Button btnAnnuler = dialogView.findViewById(R.id.btnAnnulerDeconnexion);
        Button btnConfirmer = dialogView.findViewById(R.id.btnConfirmerDeconnexion);

        btnAnnuler.setOnClickListener(v -> {
            alertDialog.dismiss();
            Toast.makeText(this, "Déconnexion annulée.", Toast.LENGTH_SHORT).show();
        });

        btnConfirmer.setOnClickListener(v -> {
            alertDialog.dismiss();

            Toast.makeText(this, "Déconnexion réussie.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(signalement.this, login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        alertDialog.show();
    }
}