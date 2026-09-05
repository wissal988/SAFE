package com.example.safe;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> {

    private Context context;
    private List<Admin> adminList;

    public AdminAdapter(Context context, List<Admin> adminList) {
        this.context = context;
        this.adminList = adminList;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdminViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_admin, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        Admin admin = adminList.get(position);

        holder.tvNom.setText(admin.nom);
        holder.tvEmail.setText(admin.email);

        // ---------- SUPPRIMER ----------
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Supprimer admin")
                    .setMessage("Voulez-vous vraiment supprimer cet admin ?")
                    .setPositiveButton("Oui", (d, w) -> {
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(admin.uid)
                                .removeValue();

                        adminList.remove(position);
                        notifyItemRemoved(position);

                        Toast.makeText(context, "Admin supprimé", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });

        // ---------- MODIFIER ----------
        holder.btnEdit.setOnClickListener(v -> showEditDialog(admin, position));
    }

    private void showEditDialog(Admin admin, int index) {

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_admin, null);
        EditText etNom = dialogView.findViewById(R.id.etNom);
        EditText etPrenom = dialogView.findViewById(R.id.etPrenom);
        EditText etTelephone = dialogView.findViewById(R.id.etTelephone);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);

        etNom.setText(admin.nom);
        etPrenom.setText(admin.prenom);
        etTelephone.setText(admin.telephone);
        etEmail.setText(admin.email);

        new AlertDialog.Builder(context)
                .setTitle("Modifier Admin")
                .setView(dialogView)
                .setPositiveButton("Modifier", (dialog, which) -> {

                    admin.nom = etNom.getText().toString();
                    admin.prenom = etPrenom.getText().toString();
                    admin.telephone = etTelephone.getText().toString();
                    admin.email = etEmail.getText().toString();

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(admin.uid)
                            .child("nom").setValue(admin.nom);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(admin.uid)
                            .child("prenom").setValue(admin.prenom);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(admin.uid)
                            .child("telephone").setValue(admin.telephone);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(admin.uid)
                            .child("email").setValue(admin.email);

                    notifyItemChanged(index);

                    Toast.makeText(context, "Admin modifié", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return adminList.size();
    }

    public static class AdminViewHolder extends RecyclerView.ViewHolder {

        TextView tvNom, tvEmail;
        ImageButton btnDelete, btnEdit;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNom = itemView.findViewById(R.id.tvNom);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
