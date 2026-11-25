package com.example.stylemate;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WardrobeAdapter extends RecyclerView.Adapter<WardrobeAdapter.WardrobeViewHolder> {

    public interface OnItemActionListener {
        void onEdit(ClothingItem item);
        void onDelete(ClothingItem item);
    }

    private final ArrayList<ClothingItem> clothingList;
    private final OnItemActionListener listener;

    public WardrobeAdapter(
            ArrayList<ClothingItem> clothingList,
            OnItemActionListener listener) {

        this.clothingList = clothingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WardrobeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wardrobe, parent, false);

        return new WardrobeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull WardrobeViewHolder holder, int position) {

        ClothingItem item = clothingList.get(position);

        holder.name.setText(item.getName());
        holder.category.setText(item.getCategory());

        if (item.getImageUri() != null) {
            holder.image.setImageURI(item.getImageUri());
        } else {
            holder.image.setImageDrawable(null);
        }

        holder.itemView.setOnClickListener(v -> {

            ImageView largeImage = new ImageView(
                    holder.itemView.getContext()
            );

            int imageHeight = (int) (
                    400 * holder.itemView.getResources()
                            .getDisplayMetrics().density
            );

            largeImage.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            imageHeight
                    )
            );

            largeImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

            if (item.getImageUri() != null) {
                largeImage.setImageURI(item.getImageUri());
            }

            LinearLayout layout = new LinearLayout(
                    holder.itemView.getContext()
            );

            layout.setOrientation(LinearLayout.VERTICAL);

            int padding = (int) (
                    12 * holder.itemView.getResources()
                            .getDisplayMetrics().density
            );

            layout.setPadding(padding, padding, padding, 0);
            layout.addView(largeImage);

            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle(item.getName())
                    .setMessage(item.getCategory())
                    .setView(layout)
                    .setPositiveButton("Edit", (dialog, which) ->
                            listener.onEdit(item))
                    .setNegativeButton("Delete", (dialog, which) ->
                            listener.onDelete(item))
                    .setNeutralButton("Close", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return clothingList.size();
    }

    static class WardrobeViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, category;

        public WardrobeViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.iv_clothing);
            name = itemView.findViewById(R.id.tv_clothing_name);
            category = itemView.findViewById(R.id.tv_clothing_category);
        }
    }
}