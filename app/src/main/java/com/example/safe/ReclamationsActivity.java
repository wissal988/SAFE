package com.example.safe;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ReclamationsActivity extends AppCompatActivity {

    private RecyclerView rvReclamations;
    private ReclamationAdapter adapter;
    private List<Reclamation> reclamationList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reclamations);

        rvReclamations = findViewById(R.id.rvReclamations);
        rvReclamations.setLayoutManager(new LinearLayoutManager(this));
        reclamationList = new ArrayList<>();
        adapter = new ReclamationAdapter(this, reclamationList);
        rvReclamations.setAdapter(adapter);

        fetchReclamations();
    }

    private void fetchReclamations() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("reclamations");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                reclamationList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Reclamation r = data.getValue(Reclamation.class);
                    if (r != null) {
                        reclamationList.add(r);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}
