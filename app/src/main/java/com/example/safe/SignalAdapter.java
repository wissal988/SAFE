package com.example.safe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SignalAdapter extends RecyclerView.Adapter<SignalAdapter.SignalViewHolder> {

    private Context context;
    private List<Signal> signalList;

    public SignalAdapter(Context context, List<Signal> signalList) {
        this.context = context;
        this.signalList = signalList;
    }

    @NonNull
    @Override
    public SignalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_signal, parent, false);
        return new SignalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SignalViewHolder holder, int position) {
        Signal signal = signalList.get(position);
        holder.tvTitle.setText(signal.getTitle());
        holder.tvEtat.setText(getEtatText(signal.getEtat()));

        // Afficher le nom et prénom de l'utilisateur
        getUserName(signal.getUserId(), holder.tvUser);

        // Valider (vert)
        holder.btnValider.setOnClickListener(v -> {
            signal.setEtat(1);
            FirebaseDatabase.getInstance().getReference("signals")
                    .child(signal.getId())
                    .child("etat")
                    .setValue(1);
            notifyDataSetChanged();
        });

        // Refuser (rouge)
        holder.btnRefuser.setOnClickListener(v -> {
            signal.setEtat(2);
            FirebaseDatabase.getInstance().getReference("signals")
                    .child(signal.getId())
                    .child("etat")
                    .setValue(2);
            notifyDataSetChanged();
        });

        // Voir détails (bleu)
        holder.btnDetails.setOnClickListener(v -> showDetailsDialog(signal));

        // Ouvrir localisation sur Google Maps
        holder.tvLocation.setOnClickListener(v -> {
            String loc = signal.getLocation(); // "latitude,longitude"
            String geoUri = "geo:" + loc + "?q=" + loc;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
            intent.setPackage("com.google.android.apps.maps"); // ouvre Google Maps
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Google Maps non disponible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getEtatText(int etat) {
        switch (etat) {
            case 1: return "Validé";
            case 2: return "Refusé";
            default: return "Non traité";
        }
    }

    private void showDetailsDialog(Signal signal) {
        String details = "Titre: " + signal.getTitle() + "\n"
                + "Description: " + signal.getDescription() + "\n"
                + "Type: " + signal.getType() + "\n"
                + "Degré: " + signal.getDegree() + "\n"
                + "Localisation: " + signal.getLocation() + "\n"
                + "Utilisateur: " + signal.getUserId() + "\n"
                + "Etat: " + getEtatText(signal.getEtat());

        new AlertDialog.Builder(context)
                .setTitle("Détails du signal")
                .setMessage(details)
                .setPositiveButton("Fermer", null)
                .show();
    }

    private void getUserName(String userId, TextView textView) {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        UserModel user = dataSnapshot.getValue(UserModel.class);
                        if (user != null) {
                            textView.setText(user.nom + " " + user.prenom);
                            // Afficher toutes les infos si on clique sur le nom
                            textView.setOnClickListener(v -> showUserDetails(user));
                        }
                    } else {
                        textView.setText("Utilisateur inconnu");
                    }
                })
                .addOnFailureListener(e -> textView.setText("Erreur utilisateur"));
    }

    private void showUserDetails(UserModel user) {
        String details = "Nom: " + user.nom + "\n"
                + "Prénom: " + user.prenom + "\n"
                + "Email: " + user.email + "\n"
                + "Téléphone: " + user.telephone + "\n"
                + "Rôle: " + user.role;

        new AlertDialog.Builder(context)
                .setTitle("Infos Utilisateur")
                .setMessage(details)
                .setPositiveButton("Fermer", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return signalList.size();
    }

    public static class SignalViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvEtat, tvUser, tvLocation;
        Button btnValider, btnRefuser, btnDetails;

        public SignalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvEtat = itemView.findViewById(R.id.tvEtat);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            btnValider = itemView.findViewById(R.id.btnValider);
            btnRefuser = itemView.findViewById(R.id.btnRefuser);
            btnDetails = itemView.findViewById(R.id.btnDetails);
        }
    }
}
