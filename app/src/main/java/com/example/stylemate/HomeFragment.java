
package com.example.stylemate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.activity_home,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // My Digital Wardrobe
        view.findViewById(R.id.wardrobe_card)
                .setOnClickListener(v -> openNavigationTab(R.id.nav_wardrobe));

        // Outfit Planner
        view.findViewById(R.id.planner_card)
                .setOnClickListener(v -> openNavigationTab(R.id.nav_planner));

        // AI Fashion Stylist
        view.findViewById(R.id.ai_stylist_card)
                .setOnClickListener(v ->
                        openFeatureScreen("AI Fashion Assistant"));

        // AI Clothing Scanner
        view.findViewById(R.id.scanner_card)
                .setOnClickListener(v ->
                        openFeatureScreen("AI Clothing Scanner"));
    }

    private void openNavigationTab(int menuItemId) {

        BottomNavigationView navigation =
                requireActivity().findViewById(R.id.bottom_navigation);

        if (navigation != null) {
            navigation.setSelectedItemId(menuItemId);
        }
    }

    private void openFeatureScreen(String title) {

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.fragment_container,
                        SectionFragment.newInstance(title)
                )
                .addToBackStack(null)
                .commit();
    }
}
