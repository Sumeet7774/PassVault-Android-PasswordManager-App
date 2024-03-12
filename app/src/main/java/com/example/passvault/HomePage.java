package com.example.passvault;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePage extends AppCompatActivity {

    private BottomNavigationView bottom_navigation_view;
    private FrameLayout frame_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        bottom_navigation_view = findViewById(R.id.bottomNavView);
        frame_layout = findViewById(R.id.frameLayout);

        // Load HomeFragment by default when activity is initialized
        loadFragment(new HomeFragment(), true);

        bottom_navigation_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int itemId = menuItem.getItemId();

                if(itemId == R.id.navHome) {

                    // If HomeFragment is already displayed, do nothing
                    if(!(getCurrentFragment() instanceof HomeFragment))
                    {
                        loadFragment(new HomeFragment(), false);
                    }
                }
                else if(itemId == R.id.navSearch)
                {
                    loadFragment(new SearchFragment(), false);
                }
                else if(itemId == R.id.navSettings)
                {
                    loadFragment(new SettingsFragment(), false);
                }
                else
                { // navProfile
                    loadFragment(new ProfileFragment(), false);
                }

                return true;
            }
        });
    }

    // Function to load fragment
    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(isAppInitialized && fragmentManager.findFragmentById(R.id.frameLayout) == null)
        {
            // Add HomeFragment only if it's not already added
            fragmentTransaction.add(R.id.frameLayout, fragment);
        }
        else
        {
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }

        fragmentTransaction.commit();
    }

    // Function to get the current fragment displayed
    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.frameLayout);
    }
}
