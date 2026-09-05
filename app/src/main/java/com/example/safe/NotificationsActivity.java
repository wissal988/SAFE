package com.example.safe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationsActivity extends AppCompatActivity {

    private Button btnSignales, btnReclamations, btnAmeliorations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        btnSignales = findViewById(R.id.btnSignales);
        btnReclamations = findViewById(R.id.btnReclamations);
        btnAmeliorations = findViewById(R.id.btnAmeliorations);

        btnSignales.setOnClickListener(v ->
                startActivity(new Intent(NotificationsActivity.this, SignalesActivity.class))
        );

        btnReclamations.setOnClickListener(v ->
                startActivity(new Intent(NotificationsActivity.this, ReclamationsActivity.class))
        );

        btnAmeliorations.setOnClickListener(v ->
                startActivity(new Intent(NotificationsActivity.this, AmeliorationActivity.class))
        );
    }
}
