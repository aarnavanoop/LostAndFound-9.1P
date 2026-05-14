package com.example.lostandfound;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddItemActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etLocation, etContactName, etContactPhone;
    private Spinner  spinnerCategory, spinnerType;
    private ImageView ivPreview;

    private String imageUriString = "";
    private double selectedLat    = 0.0;
    private double selectedLng    = 0.0;

    private DatabaseHelper          dbHelper;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST = 200;

    private final String[] CATEGORIES = {
        "Electronics", "Pets", "Wallets", "Keys",
        "Clothing", "Bags", "Jewellery", "Documents", "Other"
    };
    private final String[] TYPES = {"Lost", "Found"};


    private final ActivityResultLauncher<Intent> imagePickerLauncher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                if (uri != null) {
                    getContentResolver().takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    imageUriString = uri.toString();
                    ivPreview.setImageURI(uri);
                }
            }
        });


    private final ActivityResultLauncher<Intent> placesLauncher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Place place = Autocomplete.getPlaceFromIntent(result.getData());

                String display = place.getAddress() != null ? place.getAddress() : place.getName();
                etLocation.setText(display);
                if (place.getLatLng() != null) {
                    selectedLat = place.getLatLng().latitude;
                    selectedLng = place.getLatLng().longitude;
                }
            } else if (result.getResultCode() == AutocompleteActivity.RESULT_ERROR) {
                Toast.makeText(this, "Autocomplete error – try again", Toast.LENGTH_SHORT).show();
            }

        });


    private final ActivityResultLauncher<String> permissionLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) openImagePicker();
            else Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
        });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);


        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }

        dbHelper             = new DatabaseHelper(this);
        fusedLocationClient  = LocationServices.getFusedLocationProviderClient(this);


        etTitle         = findViewById(R.id.etTitle);
        etDescription   = findViewById(R.id.etDescription);
        etLocation      = findViewById(R.id.etLocation);
        etContactName   = findViewById(R.id.etContactName);
        etContactPhone  = findViewById(R.id.etContactPhone);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerType     = findViewById(R.id.spinnerType);
        ivPreview       = findViewById(R.id.ivPreview);
        Button btnPickImage   = findViewById(R.id.btnPickImage);
        Button btnSubmit      = findViewById(R.id.btnSubmit);
        Button btnGetLocation = findViewById(R.id.btnGetLocation);


        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_item, CATEGORIES);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_item, TYPES);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        etLocation.setFocusable(false);
        etLocation.setOnClickListener(v -> launchPlacesAutocomplete());


        btnGetLocation.setOnClickListener(v -> getCurrentLocation());


        btnPickImage.setOnClickListener(v -> requestImagePermission());
        btnSubmit.setOnClickListener(v -> submitItem());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Post Item");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    private void launchPlacesAutocomplete() {
        List<Place.Field> fields = Arrays.asList(
            Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(this);
        placesLauncher.launch(intent);
    }


    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                selectedLat = location.getLatitude();
                selectedLng = location.getLongitude();

                // Reverse-geocode to human-readable address
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(selectedLat, selectedLng, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        etLocation.setText(addresses.get(0).getAddressLine(0));
                    } else {
                        etLocation.setText(selectedLat + ", " + selectedLng);
                    }
                } catch (IOException e) {
                    etLocation.setText(selectedLat + ", " + selectedLng);
                }

                Toast.makeText(this, "Current location captured!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                    "Could not get location – make sure GPS is enabled and try again.",
                    Toast.LENGTH_LONG).show();
            }
        });
    }


    private void requestImagePermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ? Manifest.permission.READ_MEDIA_IMAGES
            : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            permissionLauncher.launch(permission);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
            | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        imagePickerLauncher.launch(intent);
    }


    private void submitItem() {
        String title    = etTitle.getText().toString().trim();
        String desc     = etDescription.getText().toString().trim();
        String loc      = etLocation.getText().toString().trim();
        String cName    = etContactName.getText().toString().trim();
        String cPhone   = etContactPhone.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String type     = spinnerType.getSelectedItem().toString();

        if (title.isEmpty()) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }
        if (loc.isEmpty()) {
            Toast.makeText(this, "Please select a location (tap the field or use GPS button)",
                Toast.LENGTH_LONG).show();
            return;
        }
        if (cName.isEmpty()) {
            etContactName.setError("Contact name is required");
            etContactName.requestFocus();
            return;
        }
        if (imageUriString.isEmpty()) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateTime = new SimpleDateFormat("dd MMM yyyy, hh:mm a",
            Locale.getDefault()).format(new Date());

        Item item = new Item(0, title, desc, category, type,
            loc, cName, cPhone, imageUriString, dateTime,
            selectedLat, selectedLng);          // ← coordinates included

        long result = dbHelper.insertItem(item);
        if (result != -1) {
            Toast.makeText(this, "Item posted successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error posting item", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST
            && grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else if (requestCode == LOCATION_PERMISSION_REQUEST) {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
