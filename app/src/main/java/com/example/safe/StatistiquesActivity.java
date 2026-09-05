package com.example.safe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class StatistiquesActivity extends AppCompatActivity {

    private PieChartView pieChart;
    int ameliorations = 0;
    int reclamations = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistiques);

        pieChart = findViewById(R.id.pieChart);

        loadStats();
    }

    private void loadStats() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        // Améliorations
        db.getReference("ameliorations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ameliorations = (int) snapshot.getChildrenCount();
                loadReclamations();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void loadReclamations() {
        FirebaseDatabase.getInstance().getReference("reclamations")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reclamations = (int) snapshot.getChildrenCount();

                        // Mettre à jour le pie chart
                        pieChart.setData(ameliorations, reclamations);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }
}
