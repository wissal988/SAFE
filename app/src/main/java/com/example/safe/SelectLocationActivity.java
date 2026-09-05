package com.example.safe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class SelectLocationActivity extends AppCompatActivity {

    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_select_location);
        map = findViewById(R.id.map);

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(12.0);
        GeoPoint startPoint = new GeoPoint(36.7538, 3.0588);
        mapController.setCenter(startPoint);

        map.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                GeoPoint geoPoint = (GeoPoint) map.getProjection().fromPixels((int) event.getX(), (int) event.getY());
                map.getOverlays().clear();
                Marker marker = new Marker(map);
                marker.setPosition(geoPoint);
                marker.setTitle("Emplacement sélectionné");
                map.getOverlays().add(marker);
                map.invalidate();

                // Retourne latitude et longitude à l'activité appelante
                Intent resultIntent = new Intent();
                resultIntent.putExtra("latitude", geoPoint.getLatitude());
                resultIntent.putExtra("longitude", geoPoint.getLongitude());
                setResult(RESULT_OK, resultIntent);
                finish();

                Toast.makeText(this, "Lat: " + geoPoint.getLatitude() + " Lng: " + geoPoint.getLongitude(), Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }
}
