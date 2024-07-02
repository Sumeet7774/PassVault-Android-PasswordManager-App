package com.example.passvault;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class UserDetails extends Fragment {

    private TextView usernameTextview;
    private TextView emailTextview;
    private TextView passwordTextview;
    private TextView serviceTextview;
    private ImageButton gobackButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);

        usernameTextview = view.findViewById(R.id.usernameShow_textview);
        emailTextview = view.findViewById(R.id.emailidShow_textview);
        passwordTextview = view.findViewById(R.id.passwordShow_textview);
        serviceTextview = view.findViewById(R.id.servicetypeShow_textview);
        gobackButton = view.findViewById(R.id.userDetailPage_backButton);



        gobackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.user_details_container, new HomeFragment()).addToBackStack(null).commit();
            }
        });

        loadUserData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        if (getArguments() != null) {
            String username = getArguments().getString("username");
            String emailId = getArguments().getString("emailId");
            String password = getArguments().getString("password");
            String serviceType = getArguments().getString("serviceType");

            usernameTextview.setText(username);
            emailTextview.setText(emailId);
            passwordTextview.setText(password);
            serviceTextview.setText(serviceType);
        } else {
            Log.e("UserDetails", "No arguments found.");
        }
    }
}