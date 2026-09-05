package com.example.safe;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class listeSignalUser extends AppCompatActivity {

    private LinearLayout conteneurSignalements;
    private DatabaseReference signalementsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_signal_user);

        conteneurSignalements = findViewById(R.id.conteneurSignalements);
        signalementsRef = FirebaseDatabase.getInstance().getReference("signals");

        // Charger tous les signalements
        chargerSignalements();

        // Bouton retour
        ImageView btnRetour = findViewById(R.id.btnRetour);
        btnRetour.setOnClickListener(v -> finish());
    }

    // Convertir le code de l'état en texte
    private String convertirEtat(Long code) {
        if (code == null) return "En attente";

        switch (code.intValue()) {
            case 1: return "Traité";
            case 2: return "Refusé";
            default: return "En attente"; // 0
        }
    }

    // Couleur selon l'état
    private int getCouleurEtat(String etat) {
        switch (etat) {
            case "Traité": return Color.parseColor("#27AE60");   // vert
            case "Refusé": return Color.parseColor("#E74C3C");   // rouge
            default: return Color.parseColor("#F1C40F");         // jaune
        }
    }

    // Charger les signalements depuis Firebase
    private void chargerSignalements() {
        signalementsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                conteneurSignalements.removeAllViews();

                if (!snapshot.exists()) {
                    TextView tvVide = new TextView(listeSignalUser.this);
                    tvVide.setText("Aucun signalement trouvé.");
                    tvVide.setTextColor(Color.GRAY);
                    tvVide.setGravity(Gravity.CENTER);
                    tvVide.setPadding(0, 50, 0, 0);
                    conteneurSignalements.addView(tvVide);
                    return;
                }

                for (DataSnapshot data : snapshot.getChildren()) {
                    String numero = data.getKey();
                    String titre = data.child("title").getValue(String.class);
                    String date = data.child("date").getValue(String.class);
                    String description = data.child("description").getValue(String.class);
                    Long etatLong = data.child("etat").getValue(Long.class);
                    String etat = convertirEtat(etatLong);

                    ajouterSignalement(numero, titre, date, description, etat);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(listeSignalUser.this, "Erreur lors du chargement", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Ajouter un signalement dans la vue
    private void ajouterSignalement(String numero, String titre, String date, String description, String etat) {

        // Création de la carte
        CardView carte = new CardView(this);
        LinearLayout.LayoutParams paramsCarte =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsCarte.setMargins(0, 12, 0, 12);
        carte.setLayoutParams(paramsCarte);
        carte.setRadius(20f);
        carte.setCardElevation(8f);
        carte.setUseCompatPadding(true);

        // Layout interne
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 20, 24, 20);

        // Ligne numéro + date
        TextView tvNumero = creerTexte("N° " + numero, "#2C3E50", 16, true);
        TextView tvDate = creerTexte(date, "#7F8C8D", 14, false);

        LinearLayout ligne1 = new LinearLayout(this);
        ligne1.setOrientation(LinearLayout.HORIZONTAL);
        ligne1.addView(tvNumero);
        tvDate.setPadding(16, 0, 0, 0);
        ligne1.addView(tvDate);

        // Titre et description
        TextView tvTitre = creerTexte("🛑 " + titre, "#E74C3C", 18, true);
        TextView tvDesc = creerTexte(description, "#2C3E50", 15, false);

        // Etat
        TextView tvEtat = new TextView(this);
        tvEtat.setText(etat.toUpperCase());
        tvEtat.setTextColor(Color.WHITE);
        tvEtat.setPadding(28, 10, 28, 10);
        tvEtat.setGravity(Gravity.CENTER);

        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(40f);
        bg.setColor(getCouleurEtat(etat));
        if (etat.equals("Refusé")) {
            bg.setStroke(4, Color.BLACK); // bordure noire pour refusé
        }
        tvEtat.setBackground(bg);

        LinearLayout layoutEtat = new LinearLayout(this);
        layoutEtat.setGravity(Gravity.END);
        layoutEtat.setPadding(0, 16, 0, 0);
        layoutEtat.addView(tvEtat);

        // Ajouter tous les éléments
        layout.addView(ligne1);
        layout.addView(tvTitre);
        layout.addView(tvDesc);
        layout.addView(layoutEtat);

        carte.addView(layout);
        conteneurSignalements.addView(carte);
    }

    // Méthode utilitaire pour créer un TextView
    private TextView creerTexte(String texte, String couleurHex, int taille, boolean gras) {
        TextView tv = new TextView(this);
        tv.setText(texte);
        tv.setTextColor(Color.parseColor(couleurHex));
        tv.setTextSize(taille);
        if (gras) tv.setTypeface(null, Typeface.BOLD);
        return tv;
    }
}
