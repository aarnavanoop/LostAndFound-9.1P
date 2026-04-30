package com.example.lostandfound;

import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ItemDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        dbHelper = new DatabaseHelper(this);
        itemId = getIntent().getIntExtra("ITEM_ID", -1);

        if (itemId == -1) { finish(); return; }

        Item item = dbHelper.getItemById(itemId);
        if (item == null) { finish(); return; }

        ImageView ivImage       = findViewById(R.id.ivImage);
        TextView tvTitle        = findViewById(R.id.tvTitle);
        TextView tvType         = findViewById(R.id.tvType);
        TextView tvCategory     = findViewById(R.id.tvCategory);
        TextView tvDescription  = findViewById(R.id.tvDescription);
        TextView tvLocation     = findViewById(R.id.tvLocation);
        TextView tvContactName  = findViewById(R.id.tvContactName);
        TextView tvContactPhone = findViewById(R.id.tvContactPhone);
        TextView tvDateTime     = findViewById(R.id.tvDateTime);
        Button btnDelete        = findViewById(R.id.btnDelete);

        tvTitle.setText(item.getTitle());
        tvType.setText(item.getType());
        tvCategory.setText("Category: " + item.getCategory());
        tvDescription.setText(item.getDescription());
        tvLocation.setText("📍 " + item.getLocation());
        tvContactName.setText("👤 " + item.getContactName());
        tvContactPhone.setText("📞 " + item.getContactPhone());
        tvDateTime.setText("🕐 Posted: " + item.getDateTime());

        if (item.getType().equals("Lost")) {
            tvType.setBackgroundResource(R.drawable.badge_lost);
        } else {
            tvType.setBackgroundResource(R.drawable.badge_found);
        }

        if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
            try {
                ivImage.setImageURI(Uri.parse(item.getImageUri()));
            } catch (Exception e) {
                ivImage.setImageResource(R.drawable.ic_image_placeholder);
            }
        }

        btnDelete.setOnClickListener(v -> confirmDelete());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Item Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
            .setTitle("Remove Listing")
            .setMessage("Has this item been found/returned? Remove it from the listings?")
            .setPositiveButton("Yes, Remove", (dialog, which) -> {
                dbHelper.deleteItem(itemId);
                Toast.makeText(this, "Listing removed", Toast.LENGTH_SHORT).show();
                finish();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
