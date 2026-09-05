package com.example.safe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

public class ameliorationUser extends AppCompatActivity {

    private EditText titleEdit, descriptionEdit, locationEdit;
    private Button sendBtn, cancelBtn;
    private LinearLayout selectImgLayout;
    private ImageView ameliorationImg;

    private Uri imageUri;
    private LatLng selectedLatLng;

    private static final int PICK_IMAGE = 300;
    private static final int REQUEST_LOCATION = 110;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amelioration_user);

        titleEdit = findViewById(R.id.titleamelioration);
        descriptionEdit = findViewById(R.id.descriptionAmelioration);
        locationEdit = findViewById(R.id.locationAmelioration);

        sendBtn = findViewById(R.id.sendAmeliorationlBtn);
        cancelBtn = findViewById(R.id.cancelAmeliorationBtn);

        selectImgLayout = findViewById(R.id.selectImageLayout);
        ameliorationImg = findViewById(R.id.amelioration_img);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        selectImgLayout.setOnClickListener(v -> openGallery());

        locationEdit.setFocusable(false);
        locationEdit.setOnClickListener(v -> showLocationDialog());

        sendBtn.setOnClickListener(v -> saveAmelioration());
        cancelBtn.setOnClickListener(v -> finish());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void showLocationDialog() {
        String[] options = {"Saisir manuellement (ouvrir Google Maps)", "Utiliser la localisation automatique"};

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Choisir la méthode de localisation")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) openGoogleMaps();
                    else getCurrentLocation();
                }).show();
    }

    private void openGoogleMaps() {
        String geoUri = "geo:36.7538,3.0588?q=36.7538,3.0588(Emplacement)";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        intent.setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            Toast.makeText(this, "Copiez le lien depuis Google Maps et collez-le dans Localisation.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Google Maps non installé.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ameliorationImg.setImageURI(imageUri);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }

        fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null
        ).addOnSuccessListener(location -> {
            if (location != null) {
                selectedLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                locationEdit.setText(location.getLatitude() + "," + location.getLongitude());
            } else {
                Toast.makeText(this, "Localisation indisponible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            getCurrentLocation();
        }
    }

    private void saveAmelioration() {
        String title = titleEdit.getText().toString().trim();
        String description = descriptionEdit.getText().toString().trim();
        String location = locationEdit.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ameliorations");
        String ameliorationId = ref.push().getKey();

        HashMap<String, Object> data = new HashMap<>();
        data.put("ameliorationId", ameliorationId);
        data.put("userId", userId);
        data.put("title", title);
        data.put("description", description);
        data.put("location", location);
        data.put("etat", 0);  // 0 = en attente

        data.put("timestamp", System.currentTimeMillis());

        if (imageUri != null) {
            uploadImageBase64(ameliorationId, data);
        } else {
            ref.child(ameliorationId).setValue(data).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Amélioration envoyée", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    private void uploadImageBase64(String ameliorationId, HashMap<String, Object> data) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            String imageBase64 = android.util.Base64.encodeToString(
                    baos.toByteArray(),
                    android.util.Base64.DEFAULT
            );

            data.put("imageBase64", imageBase64);

            FirebaseDatabase.getInstance().getReference("ameliorations")
                    .child(ameliorationId)
                    .setValue(data)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Amélioration envoyée avec image", Toast.LENGTH_SHORT).show();
                        finish();
                    });

        } catch (Exception e) {
            Toast.makeText(this, "Erreur image : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
