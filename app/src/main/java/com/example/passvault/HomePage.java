package com.example.passvault;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomePage extends AppCompatActivity {

    SessionManagement sessionManagement;
    private BottomNavigationView bottom_navigation_view;
    private FrameLayout frame_layout;
    private SessionManagement sessionManaagement;
    private FloatingActionButton add_data_button;
    private TextView home_fragment_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        bottom_navigation_view = findViewById(R.id.bottomNavView);
        frame_layout = findViewById(R.id.frameLayout);
        add_data_button = findViewById(R.id.floatingAddButton);
        //home_fragment_textview = findViewById(R.id.home_textview);

        sessionManagement = new SessionManagement(getApplicationContext());

        //String emailid = sessionMangement.getSessionDetails("key_session_emailid");
        //home_fragment_textview.setText(emailid);

        if (!sessionManagement.getEmailid().isEmpty())
        {
            String emailid = sessionManagement.getEmailid();
            //home_fragment_textview.setText(emailid);
        }
        else
        {
            // User is not logged in, redirect to index page
            Intent intent = new Intent(HomePage.this, IndexPage.class);
            startActivity(intent);
            finish(); // Finish the current activity to prevent the user from coming back to it after login

            return; // Exit the method
        }

        // Load HomeFragment by default when activity is initialized
        loadFragment(new HomeFragment(), true);
        // Load HomeFragment by default when activity is initialized

        add_data_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, AddDataPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

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
