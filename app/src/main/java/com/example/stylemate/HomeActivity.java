package com.example.stylemate;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_content);

        // Adjust screen content around system bars
        View root = findViewById(R.id.main_root);

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {

            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            view.setPadding(
                    0,
                    systemBars.top,
                    0,
                    0
            );

            return insets;
        });

        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Load Home Screen
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Bottom Navigation Click Events
        bottomNavigation.setOnItemSelectedListener(item -> {

            Fragment selectedFragment;

            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {

                selectedFragment = new HomeFragment();

            } else if (itemId == R.id.nav_explore) {

                selectedFragment =
                        SectionFragment.newInstance("Explore Fashion");

            } else if (itemId == R.id.nav_wardrobe) {

                selectedFragment =
                        new WardrobeFragment();

            } else if (itemId == R.id.nav_planner) {

                selectedFragment =
                        SectionFragment.newInstance("Outfit Planner");

            } else if (itemId == R.id.nav_profile) {

                selectedFragment =
                        SectionFragment.newInstance("My Profile");

            } else {
                return false;
            }

            loadFragment(selectedFragment);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}