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
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class ReclamationAdapter extends RecyclerView.Adapter<ReclamationAdapter.ReclamationViewHolder> {

    private Context context;
    private List<Reclamation> reclamationList;

    public ReclamationAdapter(Context context, List<Reclamation> list) {
        this.context = context;
        this.reclamationList = list;
    }

    @NonNull
    @Override
    public ReclamationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reclamation, parent, false);
        return new ReclamationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReclamationViewHolder holder, int position) {
        Reclamation r = reclamationList.get(position);
        holder.tvTitle.setText(r.getTitle());
        holder.tvEtat.setText(getEtatText(r.getEtat()));

        // Récupérer nom et prénom utilisateur
        FirebaseDatabase.getInstance().getReference("Users")
                .child(r.getUserId())
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        UserModel user = snapshot.getValue(UserModel.class);
                        if (user != null) {
                            holder.tvUser.setText(user.nom + " " + user.prenom);
                            holder.tvUser.setOnClickListener(v -> showUserDetails(user));
                        }
                    } else {
                        holder.tvUser.setText("Utilisateur inconnu");
                    }
                });

        // Valider
        holder.btnValider.setOnClickListener(v -> {
            r.setEtat(1);
            FirebaseDatabase.getInstance().getReference("reclamations")
                    .child(r.getReclamationId())
                    .child("etat")
                    .setValue(1);
            notifyDataSetChanged();
        });

        // Refuser
        holder.btnRefuser.setOnClickListener(v -> {
            r.setEtat(2);
            FirebaseDatabase.getInstance().getReference("reclamations")
                    .child(r.getReclamationId())
                    .child("etat")
                    .setValue(2);
            notifyDataSetChanged();
        });

        // Voir détails
        holder.btnDetails.setOnClickListener(v -> showDetailsDialog(r));

        // Localisation → Google Maps
        holder.tvLocation.setOnClickListener(v -> {
            String geoUri = "geo:" + r.getLocation() + "?q=" + r.getLocation();
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri)));
        });
    }

    private void showDetailsDialog(Reclamation r) {
        String details = "Titre: " + r.getTitle() + "\n" +
                "Description: " + r.getDescription() + "\n" +
                "Localisation: " + r.getLocation() + "\n" +
                "Etat: " + getEtatText(r.getEtat());
        new AlertDialog.Builder(context)
                .setTitle("Détails")
                .setMessage(details)
                .setPositiveButton("Fermer", null)
                .show();
    }

    private void showUserDetails(UserModel user) {
        String info = "Nom: " + user.nom + "\n" +
                "Prénom: " + user.prenom + "\n" +
                "Email: " + user.email + "\n" +
                "Téléphone: " + user.telephone + "\n" +
                "Rôle: " + user.role;
        new AlertDialog.Builder(context)
                .setTitle("Utilisateur")
                .setMessage(info)
                .setPositiveButton("Fermer", null)
                .show();
    }

    private String getEtatText(int etat) {
        switch (etat) {
            case 1: return "Validé";
            case 2: return "Refusé";
            default: return "Non traité";
        }
    }

    @Override
    public int getItemCount() {
        return reclamationList.size();
    }

    static class ReclamationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvUser, tvLocation, tvEtat;
        Button btnValider, btnRefuser, btnDetails;

        public ReclamationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvEtat = itemView.findViewById(R.id.tvEtat);
            btnValider = itemView.findViewById(R.id.btnValider);
            btnRefuser = itemView.findViewById(R.id.btnRefuser);
            btnDetails = itemView.findViewById(R.id.btnDetails);
        }
    }
}
