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

        bottom_navigation_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int itemId = menuItem.getItemId();

                if(itemId == R.id.navHome)
                {
                    loadFragment(new HomeFragment(), false);
                }
                else if(itemId == R.id.navSearch)
                {
                    loadFragment(new SearchFragment(), false);
                }
                else // navProfile
                {
                    loadFragment(new ProfileFragment(), false);
                }

                loadFragment(new HomeFragment(), true);

                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(isAppInitialized)
        {
            fragmentTransaction.add(R.id.frameLayout, fragment);
        }
        else
        {
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }

        fragmentTransaction.commit();
    }
}