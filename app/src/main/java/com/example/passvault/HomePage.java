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
    private FloatingActionButton floatingAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        bottom_navigation_view = findViewById(R.id.bottomNavView);
        frame_layout = findViewById(R.id.frameLayout);
        add_data_button = findViewById(R.id.floatingAddButton);
        //home_fragment_textview = findViewById(R.id.home_textview);
        floatingAddButton = findViewById(R.id.floatingAddButton);
        BottomNavigationView bottomNavView = findViewById(R.id.bottomNavView);

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

        String targetFragment = getIntent().getStringExtra("TARGET_FRAGMENT");

        if (targetFragment != null && targetFragment.equals("ProfileFragment"))
        {
            loadFragment(new ProfileFragment(), true);
            bottom_navigation_view.setSelectedItemId(R.id.navProfile);
        }
        else
        {
            loadFragment(new HomeFragment(), true);
        }

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
                Fragment currentFragment = getCurrentFragment();

                if (itemId == R.id.navHome) {
                    if (!(currentFragment instanceof HomeFragment)) {
                        loadFragment(new HomeFragment(), currentFragment, false);
                    }
                } else if (itemId == R.id.navSearch) {
                    if (!(currentFragment instanceof SearchFragment)) {
                        loadFragment(new SearchFragment(), currentFragment, false);
                    }
                } else {
                    if (!(currentFragment instanceof ProfileFragment)) {
                        loadFragment(new ProfileFragment(), currentFragment, false);
                    }
                }
                return true;
            }
        });
    }

    private void loadFragment(Fragment newFragment, boolean isAppInitialized) {
        Fragment currentFragment = getCurrentFragment();
        loadFragment(newFragment, currentFragment, isAppInitialized);
    }

    private void loadFragment(Fragment newFragment, Fragment currentFragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (currentFragment != null) {
            // Set animations based on the navigation direction
            if (newFragment instanceof HomeFragment) {
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_left, R.anim.slide_out_right,
                        R.anim.slide_in_right, R.anim.slide_out_left
                );
            } else if (newFragment instanceof SearchFragment) {
                if (currentFragment instanceof HomeFragment) {
                    fragmentTransaction.setCustomAnimations(
                            R.anim.slide_in_right, R.anim.slide_out_left,
                            R.anim.slide_in_left, R.anim.slide_out_right
                    );
                } else if (currentFragment instanceof ProfileFragment) {
                    fragmentTransaction.setCustomAnimations(
                            R.anim.slide_in_left, R.anim.slide_out_right,
                            R.anim.slide_in_right, R.anim.slide_out_left
                    );
                }
            } else if (newFragment instanceof ProfileFragment) {
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_left, R.anim.slide_out_right
                );
            }
        }

        if (isAppInitialized && fragmentManager.findFragmentById(R.id.frameLayout) == null) {
            fragmentTransaction.add(R.id.frameLayout, newFragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, newFragment);
        }

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        manageFloatingButtonVisibility(newFragment);
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.frameLayout);
    }

    private void manageFloatingButtonVisibility(Fragment fragment) {
        if (fragment instanceof HomeFragment) {
            floatingAddButton.show();
        } else {
            floatingAddButton.hide();
        }
    }
}