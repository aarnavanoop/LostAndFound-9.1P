package com.example.lostandfound;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddItemActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etLocation, etContactName, etContactPhone;
    private Spinner spinnerCategory, spinnerType;
    private ImageView ivPreview;
    private String imageUriString = "";

    private DatabaseHelper dbHelper;

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
                    // Persist permission so we can read it later
                    getContentResolver().takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    imageUriString = uri.toString();
                    ivPreview.setImageURI(uri);
                }
            }
        });


    private final ActivityResultLauncher<String> permissionLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) openImagePicker();
            else Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        dbHelper = new DatabaseHelper(this);

        etTitle        = findViewById(R.id.etTitle);
        etDescription  = findViewById(R.id.etDescription);
        etLocation     = findViewById(R.id.etLocation);
        etContactName  = findViewById(R.id.etContactName);
        etContactPhone = findViewById(R.id.etContactPhone);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerType    = findViewById(R.id.spinnerType);
        ivPreview      = findViewById(R.id.ivPreview);
        Button btnPickImage = findViewById(R.id.btnPickImage);
        Button btnSubmit    = findViewById(R.id.btnSubmit);


        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, CATEGORIES);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);


        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, TYPES);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        btnPickImage.setOnClickListener(v -> requestImagePermission());
        btnSubmit.setOnClickListener(v -> submitItem());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Post Item");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void requestImagePermission() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ? Manifest.permission.READ_MEDIA_IMAGES
            : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission)
            == PackageManager.PERMISSION_GRANTED) {
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
        String title   = etTitle.getText().toString().trim();
        String desc    = etDescription.getText().toString().trim();
        String loc     = etLocation.getText().toString().trim();
        String cName   = etContactName.getText().toString().trim();
        String cPhone  = etContactPhone.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String type    = spinnerType.getSelectedItem().toString();

        if (title.isEmpty()) {
            etTitle.setError("Title is required");
            etTitle.requestFocus();
            return;
        }
        if (loc.isEmpty()) {
            etLocation.setError("Location is required");
            etLocation.requestFocus();
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
            loc, cName, cPhone, imageUriString, dateTime);

        long result = dbHelper.insertItem(item);
        if (result != -1) {
            Toast.makeText(this, "Item posted successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error posting item", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
