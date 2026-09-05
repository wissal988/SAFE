package com.example.safe;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class listReclamationUser extends AppCompatActivity {

    private LinearLayout conteneurReclamations;
    private ImageView btnRetour;
    private DatabaseReference reclamationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_reclamation_user);

        // ==== RÉFÉRENCES XML ====
        conteneurReclamations = findViewById(R.id.conteneurReclamations);
        btnRetour = findViewById(R.id.btnRetour);
        reclamationRef = FirebaseDatabase.getInstance().getReference("reclamations");

        // ==== BOUTON RETOUR ====
        btnRetour.setOnClickListener(v -> finish());

        // ==== CHARGER RÉCLAMATIONS ====
        chargerReclamations();
    }

    // Charger toutes les réclamations depuis Firebase
    private void chargerReclamations() {
        reclamationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                conteneurReclamations.removeAllViews();

                if (!snapshot.exists()) {
                    TextView tvVide = new TextView(listReclamationUser.this);
                    tvVide.setText("Aucune réclamation trouvée.");
                    tvVide.setTextColor(Color.GRAY);
                    tvVide.setGravity(Gravity.CENTER);
                    tvVide.setPadding(0, 50, 0, 0);
                    conteneurReclamations.addView(tvVide);
                    return;
                }

                for (DataSnapshot data : snapshot.getChildren()) {
                    String numero = data.getKey();
                    String title = data.child("title").getValue(String.class);
                    String date = data.child("date").getValue(String.class);
                    String description = data.child("description").getValue(String.class);
                    Long etatLong = data.child("etat").getValue(Long.class);
                    String statut = convertirEtat(etatLong);

                    if (title == null) title = "Sans type";
                    if (date == null) date = "-";
                    if (description == null) description = "-";

                    ajouterReclamation(numero, title, date, description, statut);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(listReclamationUser.this, "Erreur lors du chargement", Toast.LENGTH_SHORT).show();
            }
        });
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
    private int getCouleurStatut(String etat) {
        switch (etat.toLowerCase()) {
            case "traité": return Color.parseColor("#27AE60"); // vert
            case "refusé": return Color.parseColor("#E74C3C"); // rouge
            default: return Color.parseColor("#F1C40F");       // jaune
        }
    }

    // Créer badge de statut
    private TextView creerBadgeStatut(String etat) {
        TextView tv = new TextView(this);
        tv.setText(etat.toUpperCase());
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(14);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setPadding(32, 12, 32, 12);

        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(60f);
        bg.setColor(getCouleurStatut(etat));

        if (etat.equalsIgnoreCase("refusé")) {
            bg.setStroke(4, Color.BLACK); // bordure noire pour refusé
        }

        tv.setBackground(bg);
        return tv;
    }

    // Ajouter une réclamation dans la vue
    private void ajouterReclamation(String numero, String type, String date, String details, String statut) {

        // Carte
        CardView carte = new CardView(this);
        LinearLayout.LayoutParams paramsCarte =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsCarte.setMargins(0, 12, 0, 12);
        carte.setLayoutParams(paramsCarte);
        carte.setRadius(24f);
        carte.setCardElevation(10f);
        carte.setUseCompatPadding(true);
        carte.setCardBackgroundColor(Color.WHITE);

        // Layout interne
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 28, 32, 28);

        // Ligne 1 : Numéro + Date
        LinearLayout ligne1 = new LinearLayout(this);
        ligne1.setOrientation(LinearLayout.HORIZONTAL);
        ligne1.setGravity(Gravity.CENTER_VERTICAL);
        TextView tvNumero = creerTexte("N° " + numero, "#2C3E50", 17, true);
        TextView tvDate = creerTexte(date, "#7F8C8D", 14, false);
        tvDate.setPadding(18, 0, 0, 0);
        ligne1.addView(tvNumero);
        ligne1.addView(tvDate);

        // Ligne 2 : Type
        TextView tvType = creerTexte("💡 " + type, "#FF9800", 19, true);
        tvType.setPadding(0, 12, 0, 8);

        // Ligne 3 : Détails
        TextView tvDetails = creerTexte(details, "#2C3E50", 15, false);

        // Badge Statut
        TextView tvStatut = creerBadgeStatut(statut);
        LinearLayout layoutStatut = new LinearLayout(this);
        layoutStatut.setGravity(Gravity.END);
        layoutStatut.setPadding(0, 20, 0, 0);
        layoutStatut.addView(tvStatut);

        // Assemblage
        layout.addView(ligne1);
        layout.addView(tvType);
        layout.addView(tvDetails);
        layout.addView(layoutStatut);
        carte.addView(layout);
        conteneurReclamations.addView(carte);

        // Click sur la carte
        carte.setOnClickListener(v -> {
            Toast.makeText(this, "Réclamation N°" + numero, Toast.LENGTH_SHORT).show();
        });
    }

    // Méthode utilitaire pour créer TextView
    private TextView creerTexte(String txt, String couleur, int taille, boolean gras) {
        TextView t = new TextView(this);
        t.setText(txt);
        t.setTextColor(Color.parseColor(couleur));
        t.setTextSize(taille);
        t.setPadding(4, 4, 4, 4);
        t.setMaxLines(6);
        if (gras) t.setTypeface(null, Typeface.BOLD);
        return t;
    }
}
