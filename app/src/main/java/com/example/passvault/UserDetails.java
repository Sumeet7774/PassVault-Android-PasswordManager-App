package com.example.passvault;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class UserDetails extends Fragment {

    private TextView usernameTextview;
    private TextView emailTextview;
    private TextView passwordTextview;
    private TextView serviceTextview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);

        usernameTextview = view.findViewById(R.id.usernameShow_textview);
        emailTextview = view.findViewById(R.id.emailidShow_textview);
        passwordTextview = view.findViewById(R.id.passwordShow_textview);
        serviceTextview = view.findViewById(R.id.servicetypeShow_textview);

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

        return view;
    }



}