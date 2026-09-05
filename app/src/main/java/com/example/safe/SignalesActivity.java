package com.example.safe;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SignalesActivity extends AppCompatActivity {

    private RecyclerView rvSignals;
    private SignalAdapter adapter;
    private List<Signal> signalList;
    private DatabaseReference signalsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signales);

        rvSignals = findViewById(R.id.rvSignals);
        rvSignals.setLayoutManager(new LinearLayoutManager(this));

        signalList = new ArrayList<>();
        adapter = new SignalAdapter(this, signalList);
        rvSignals.setAdapter(adapter);

        signalsRef = FirebaseDatabase.getInstance().getReference("signals");
        loadSignals();
    }

    private void loadSignals() {
        signalsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                signalList.clear();
                for (DataSnapshot snap : task.getResult().getChildren()) {
                    Signal signal = snap.getValue(Signal.class);
                    if (signal != null) {
                        signal.setId(snap.getKey());
                        signalList.add(signal);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
