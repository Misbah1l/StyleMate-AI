package com.example.stylemate;

import android.net.Uri;

public class ClothingItem {

    private String name;
    private String category;
    private Uri imageUri;

    public ClothingItem(String name, String category, Uri imageUri) {
        this.name = name;
        this.category = category;
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}