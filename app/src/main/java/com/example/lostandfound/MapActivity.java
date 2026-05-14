package com.example.lostandfound;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap               googleMap;
    private DatabaseHelper          dbHelper;
    private FusedLocationProviderClient fusedLocationClient;

    private SeekBar  seekBarRadius;
    private TextView tvRadius;
    private Button   btnApplyRadius;
    private Button   btnShowAll;

    private Location currentLocation;

    private static final int LOCATION_PERMISSION_REQUEST = 300;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        dbHelper            = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        seekBarRadius  = findViewById(R.id.seekBarRadius);
        tvRadius       = findViewById(R.id.tvRadius);
        btnApplyRadius = findViewById(R.id.btnApplyRadius);
        btnShowAll     = findViewById(R.id.btnShowAll);


        seekBarRadius.setMax(49);          
        seekBarRadius.setProgress(9);      
        tvRadius.setText("Radius: 10 km");

        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                tvRadius.setText("Radius: " + (progress + 1) + " km");
            }
            @Override public void onStartTrackingTouch(SeekBar bar) {}
            @Override public void onStopTrackingTouch(SeekBar bar) {}
        });

        btnApplyRadius.setOnClickListener(v -> requestLocationAndFilter());
        btnShowAll.setOnClickListener(v -> { if (googleMap != null) showAllItems(); });

        SupportMapFragment mapFragment = (SupportMapFragment)
            getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Items on Map");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            cacheCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST);
        }

        showAllItems();
    }

    private void showAllItems() {
        googleMap.clear();
        List<Item> items = dbHelper.getItemsWithLocation();

        if (items.isEmpty()) {
            Toast.makeText(this,
                "No items with location data yet. Add items using the location field.",
                Toast.LENGTH_LONG).show();

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(-25.2744, 133.7751), 4));
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Item item : items) {
            addMarker(item);
            builder.include(new LatLng(item.getLatitude(), item.getLongitude()));
        }

        try {
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(builder.build(), 150));
        } catch (Exception ignored) {

            Item first = items.get(0);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(first.getLatitude(), first.getLongitude()), 13));
        }
    }


    private void requestLocationAndFilter() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                int radiusKm = seekBarRadius.getProgress() + 1;   // 1-based
                filterByRadius(radiusKm);
            } else {
                Toast.makeText(this,
                    "Cannot determine your location. Ensure GPS is enabled.",
                    Toast.LENGTH_LONG).show();
            }
        });
    }


    private void filterByRadius(int radiusKm) {
        googleMap.clear();

        List<Item> all    = dbHelper.getItemsWithLocation();
        List<Item> nearby = new ArrayList<>();

        for (Item item : all) {
            float[] dist = new float[1];
            Location.distanceBetween(
                currentLocation.getLatitude(), currentLocation.getLongitude(),
                item.getLatitude(), item.getLongitude(), dist);
            if (dist[0] / 1000f <= radiusKm) nearby.add(item);
        }

        LatLng userPos = new LatLng(
            currentLocation.getLatitude(), currentLocation.getLongitude());


        googleMap.addMarker(new MarkerOptions()
            .position(userPos)
            .title("Your Location")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));


        googleMap.addCircle(new CircleOptions()
            .center(userPos)
            .radius(radiusKm * 1000.0)
            .strokeColor(0x880000FF)
            .fillColor(0x110000FF)
            .strokeWidth(3));

        if (nearby.isEmpty()) {
            Toast.makeText(this,
                "No items found within " + radiusKm + " km of your location.",
                Toast.LENGTH_LONG).show();
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(userPos, zoomForRadius(radiusKm)));
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(userPos);
        for (Item item : nearby) {
            addMarker(item);
            builder.include(new LatLng(item.getLatitude(), item.getLongitude()));
        }

        Toast.makeText(this,
            nearby.size() + " item(s) within " + radiusKm + " km", Toast.LENGTH_SHORT).show();
        try {
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(builder.build(), 150));
        } catch (Exception ignored) {}
    }

    private void addMarker(Item item) {
        LatLng pos = new LatLng(item.getLatitude(), item.getLongitude());
        float colour = item.getType().equals("Lost")
            ? BitmapDescriptorFactory.HUE_RED
            : BitmapDescriptorFactory.HUE_GREEN;
        googleMap.addMarker(new MarkerOptions()
            .position(pos)
            .title(item.getTitle())
            .snippet(item.getType() + "  •  " + item.getCategory()
                     + "\n📍 " + item.getLocation()
                     + "\n👤 " + item.getContactName())
            .icon(BitmapDescriptorFactory.defaultMarker(colour)));
    }


    private float zoomForRadius(int radiusKm) {
        if (radiusKm <= 1)  return 14;
        if (radiusKm <= 5)  return 12;
        if (radiusKm <= 10) return 11;
        if (radiusKm <= 25) return 9;
        return 7;
    }

    private void cacheCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;
        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(loc -> { if (loc != null) currentLocation = loc; });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST
            && grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (googleMap != null) {
                if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }
                cacheCurrentLocation();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
