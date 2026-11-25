package com.example.stylemate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class WardrobeFragment extends Fragment {

    private final ArrayList<ClothingItem> allItems = new ArrayList<>();
    private final ArrayList<ClothingItem> visibleItems = new ArrayList<>();

    private WardrobeAdapter adapter;
    private ActivityResultLauncher<String[]> imagePicker;

    private TextView totalItems, totalCategories, emptyMessage;
    private RecyclerView recyclerView;
    private EditText searchInput;

    private Uri selectedImageUri;
    private String selectedCategory = "All";

    private static final String PREF_NAME = "StyleMateWardrobe";
    private static final String ITEMS_KEY = "clothing_items";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePicker = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;

                        try {
                            requireContext().getContentResolver()
                                    .takePersistableUriPermission(
                                            uri,
                                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    );
                        } catch (SecurityException ignored) {
                        }

                        showClothingDetailsDialog();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_wardrobe,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_wardrobe);
        totalItems = view.findViewById(R.id.tv_total_items);
        totalCategories = view.findViewById(R.id.tv_categories);
        emptyMessage = view.findViewById(R.id.tv_empty_wardrobe);
        searchInput = view.findViewById(R.id.et_search_wardrobe);

        adapter = new WardrobeAdapter(
                visibleItems,
                new WardrobeAdapter.OnItemActionListener() {

                    @Override
                    public void onEdit(ClothingItem item) {
                        editClothingItem(item);
                    }

                    @Override
                    public void onDelete(ClothingItem item) {
                        deleteClothingItem(item);
                    }
                }
        );

        recyclerView.setLayoutManager(
                new GridLayoutManager(requireContext(), 2)
        );

        recyclerView.setAdapter(adapter);

        loadItems();

        Button addButton = view.findViewById(R.id.btn_add_clothing);

        addButton.setOnClickListener(v ->
                imagePicker.launch(new String[]{"image/*"})
        );

        view.findViewById(R.id.btn_all).setOnClickListener(v ->
                filterItems("All")
        );

        view.findViewById(R.id.btn_tops).setOnClickListener(v ->
                filterItems("Tops")
        );

        view.findViewById(R.id.btn_bottoms).setOnClickListener(v ->
                filterItems("Bottoms")
        );

        view.findViewById(R.id.btn_dresses).setOnClickListener(v ->
                filterItems("Dresses")
        );

        view.findViewById(R.id.btn_shoes).setOnClickListener(v ->
                filterItems("Shoes")
        );

        view.findViewById(R.id.btn_accessories).setOnClickListener(v ->
                filterItems("Accessories")
        );

        searchInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {

                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        filterItems("All");
        updateSummary();
    }

    private void showClothingDetailsDialog() {

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (
                20 * getResources().getDisplayMetrics().density
        );

        layout.setPadding(padding, padding, padding, 0);

        EditText nameInput = new EditText(requireContext());
        nameInput.setHint("Enter clothing name");
        nameInput.setSingleLine(true);

        layout.addView(nameInput);

        Spinner categorySpinner = new Spinner(requireContext());

        String[] categories = {
                "Tops",
                "Bottoms",
                "Dresses",
                "Shoes",
                "Accessories"
        };

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
        );

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        categorySpinner.setAdapter(spinnerAdapter);
        layout.addView(categorySpinner);

        new AlertDialog.Builder(requireContext())
                .setTitle("Add Clothing Item")
                .setMessage("Enter details for your selected photo.")
                .setView(layout)
                .setNegativeButton("Cancel", (dialog, which) -> {
                    selectedImageUri = null;
                })
                .setPositiveButton("Add", (dialog, which) -> {

                    String name = nameInput.getText()
                            .toString().trim();

                    String category = categorySpinner
                            .getSelectedItem().toString();

                    if (name.isEmpty()) {
                        name = category + " Item";
                    }

                    if (selectedImageUri != null) {

                        ClothingItem item = new ClothingItem(
                                name,
                                category,
                                selectedImageUri
                        );

                        allItems.add(item);

                        saveItems();
                        searchInput.setText("");
                        filterItems("All");
                        updateSummary();

                        selectedImageUri = null;
                    }
                })
                .show();
    }

    private void editClothingItem(ClothingItem item) {

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (
                20 * getResources().getDisplayMetrics().density
        );

        layout.setPadding(padding, padding, padding, 0);

        EditText nameInput = new EditText(requireContext());
        nameInput.setText(item.getName());
        nameInput.setSingleLine(true);
        nameInput.setHint("Clothing name");

        layout.addView(nameInput);

        Spinner categorySpinner = new Spinner(requireContext());

        String[] categories = {
                "Tops",
                "Bottoms",
                "Dresses",
                "Shoes",
                "Accessories"
        };

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
        );

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        categorySpinner.setAdapter(spinnerAdapter);

        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equalsIgnoreCase(item.getCategory())) {
                categorySpinner.setSelection(i);
                break;
            }
        }

        layout.addView(categorySpinner);

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Clothing")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (dialog, which) -> {

                    String name = nameInput.getText()
                            .toString().trim();

                    if (name.isEmpty()) {
                        name = "Clothing Item";
                    }

                    item.setName(name);

                    item.setCategory(
                            categorySpinner.getSelectedItem().toString()
                    );

                    saveItems();
                    searchInput.setText("");
                    filterItems("All");
                    updateSummary();
                })
                .show();
    }

    private void deleteClothingItem(ClothingItem item) {

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Clothing?")
                .setMessage(
                        "Are you sure you want to remove " +
                                item.getName() +
                                " from your wardrobe?"
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {

                    allItems.remove(item);

                    saveItems();
                    searchInput.setText("");
                    filterItems("All");
                    updateSummary();
                })
                .show();
    }

    private void saveItems() {

        try {
            JSONArray jsonArray = new JSONArray();

            for (ClothingItem item : allItems) {

                JSONObject object = new JSONObject();

                object.put("name", item.getName());
                object.put("category", item.getCategory());
                object.put("imageUri", item.getImageUri().toString());

                jsonArray.put(object);
            }

            SharedPreferences preferences =
                    requireContext().getSharedPreferences(
                            PREF_NAME,
                            Context.MODE_PRIVATE
                    );

            preferences.edit()
                    .putString(ITEMS_KEY, jsonArray.toString())
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadItems() {

        allItems.clear();

        SharedPreferences preferences =
                requireContext().getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        String savedData = preferences.getString(ITEMS_KEY, "[]");

        try {
            JSONArray jsonArray = new JSONArray(savedData);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);

                String name = object.getString("name");
                String category = object.getString("category");

                Uri imageUri = Uri.parse(
                        object.getString("imageUri")
                );

                allItems.add(
                        new ClothingItem(name, category, imageUri)
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterItems(String category) {

        selectedCategory = category;
        applyFilters();
    }

    private void applyFilters() {

        visibleItems.clear();

        String query = searchInput.getText()
                .toString()
                .trim()
                .toLowerCase(Locale.ROOT);

        for (ClothingItem item : allItems) {

            boolean matchesCategory =
                    selectedCategory.equals("All") ||
                            item.getCategory().equalsIgnoreCase(selectedCategory);

            boolean matchesSearch =
                    item.getName().toLowerCase(Locale.ROOT).contains(query) ||
                            item.getCategory().toLowerCase(Locale.ROOT).contains(query);

            if (matchesCategory && matchesSearch) {
                visibleItems.add(item);
            }
        }

        adapter.notifyDataSetChanged();

        if (visibleItems.isEmpty()) {

            recyclerView.setVisibility(View.GONE);
            emptyMessage.setVisibility(View.VISIBLE);

            if (allItems.isEmpty()) {
                emptyMessage.setText(
                        "Your wardrobe is waiting for its first piece.\n" +
                                "Tap Add Clothing to get started."
                );
            } else if (!query.isEmpty()) {
                emptyMessage.setText(
                        "No matching clothing found."
                );
            } else {
                emptyMessage.setText(
                        "No items found in this category."
                );
            }

        } else {

            recyclerView.setVisibility(View.VISIBLE);
            emptyMessage.setVisibility(View.GONE);
        }
    }

    private void updateSummary() {

        totalItems.setText(String.valueOf(allItems.size()));

        HashSet<String> categories = new HashSet<>();

        for (ClothingItem item : allItems) {
            categories.add(item.getCategory());
        }

        totalCategories.setText(String.valueOf(categories.size()));
    }
}