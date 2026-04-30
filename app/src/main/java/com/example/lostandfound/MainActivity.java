package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ItemAdapter adapter;
    private RecyclerView recyclerView;
    private EditText etSearch;
    private Spinner spinnerCategory;
    private TextView tvEmpty;

    private String selectedCategory = "All";

    private final String[] CATEGORIES = {
        "All", "Electronics", "Pets", "Wallets", "Keys",
        "Clothing", "Bags", "Jewellery", "Documents", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper      = new DatabaseHelper(this);
        recyclerView  = findViewById(R.id.recyclerView);
        etSearch      = findViewById(R.id.etSearch);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        tvEmpty       = findViewById(R.id.tvEmpty);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v ->
            startActivity(new Intent(this, AddItemActivity.class)));


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, CATEGORIES);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedCategory = CATEGORIES[pos];
                refreshList();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                refreshList();
            }
            @Override public void afterTextChanged(Editable s) {}
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Item> items = dbHelper.getAllItems();
        adapter = new ItemAdapter(this, items);
        recyclerView.setAdapter(adapter);

        updateEmptyView(items.isEmpty());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        String query = etSearch.getText().toString().trim();
        List<Item> items;

        if (query.isEmpty()) {
            if (selectedCategory.equals("All")) {
                items = dbHelper.getAllItems();
            } else {
                items = dbHelper.getItemsByCategory(selectedCategory);
            }
        } else {
            items = dbHelper.searchItems(query, selectedCategory);
        }

        adapter.updateList(items);
        updateEmptyView(items.isEmpty());
    }

    private void updateEmptyView(boolean isEmpty) {
        tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}
