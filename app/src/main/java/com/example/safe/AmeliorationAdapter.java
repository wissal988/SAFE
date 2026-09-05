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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import java.util.List;

public class AmeliorationAdapter extends RecyclerView.Adapter<AmeliorationAdapter.AmeliorationViewHolder> {

    private Context context;
    private List<Amelioration> ameliorationList;

    public AmeliorationAdapter(Context context, List<Amelioration> list) {
        this.context = context;
        this.ameliorationList = list;
    }

    @NonNull
    @Override
    public AmeliorationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_amelioration, parent, false);
        return new AmeliorationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmeliorationViewHolder holder, int position) {
        Amelioration a = ameliorationList.get(position);
        holder.tvTitle.setText(a.getTitle());
        holder.tvEtat.setText(getEtatText(a.getEtat()));

        // Récupérer nom et prénom utilisateur
        FirebaseDatabase.getInstance().getReference("Users")
                .child(a.getUserId())
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
            a.setEtat(1);
            FirebaseDatabase.getInstance().getReference("ameliorations")
                    .child(a.getAmeliorationId())
                    .child("etat")
                    .setValue(1);
            notifyDataSetChanged();
        });

        // Refuser
        holder.btnRefuser.setOnClickListener(v -> {
            a.setEtat(2);
            FirebaseDatabase.getInstance().getReference("ameliorations")
                    .child(a.getAmeliorationId())
                    .child("etat")
                    .setValue(2);
            notifyDataSetChanged();
        });

        // Voir détails
        holder.btnDetails.setOnClickListener(v -> showDetailsDialog(a));

        // Cliquer sur localisation → Google Maps
        holder.tvLocation.setOnClickListener(v -> {
            String geoUri = "geo:" + a.getLocation() + "?q=" + a.getLocation();
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri)));
        });
    }

    private void showDetailsDialog(Amelioration a) {
        String details = "Titre: " + a.getTitle() + "\n" +
                "Description: " + a.getDescription() + "\n" +
                "Localisation: " + a.getLocation() + "\n" +
                "Etat: " + getEtatText(a.getEtat());
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
        return ameliorationList.size();
    }

    static class AmeliorationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvUser, tvLocation, tvEtat;
        Button btnValider, btnRefuser, btnDetails;

        public AmeliorationViewHolder(@NonNull View itemView) {
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
