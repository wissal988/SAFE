package com.example.safe;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AmeliorationActivity extends AppCompatActivity {

    private RecyclerView rvAmeliorations;
    private AmeliorationAdapter adapter;
    private List<Amelioration> ameliorationList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amelioration);

        rvAmeliorations = findViewById(R.id.rvAmelioration);
        rvAmeliorations.setLayoutManager(new LinearLayoutManager(this));

        ameliorationList = new ArrayList<>();
        adapter = new AmeliorationAdapter(this, ameliorationList);
        rvAmeliorations.setAdapter(adapter);

        loadAmeliorations();
    }

    private void loadAmeliorations() {
        FirebaseDatabase.getInstance().getReference("ameliorations")
                .get()
                .addOnSuccessListener(snapshot -> {
                    ameliorationList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Amelioration a = ds.getValue(Amelioration.class);
                        if (a != null) {
                            a.setAmeliorationId(ds.getKey());
                            ameliorationList.add(a);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Erreur")
                            .setMessage("Impossible de charger les améliorations : " + e.getMessage())
                            .setPositiveButton("OK", null)
                            .show();
                });
    }
}
