package com.example.passvault;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private SessionManagement sessionManagement;
    private TextView profile_name_textview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        CardView card_AboutUs = view.findViewById(R.id.cardAboutUs);
        CardView card_contactUs = view.findViewById(R.id.cardContactUs);
        MaterialButton logout_button = view.findViewById(R.id.logout_btn);
        profile_name_textview = view.findViewById(R.id.profileNameText);

        sessionManagement = new SessionManagement(requireContext());

        String userId = sessionManagement.getUserId();
        String emailId = sessionManagement.getEmailid();

        retrieveUsername(userId,emailId);

        card_AboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aboutUsIntent = new Intent(requireContext(), AboutUsPage.class);
                startActivity(aboutUsIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        card_contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent helpIntent = new Intent(requireContext(), ContactUsPage.class);
                startActivity(helpIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmLogoutDialogBox(); // Show confirmation dialog for logout
            }
        });

        return view;
    }

    private void retrieveUsername(String userId, String emailId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.retrieveUsername,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RetrieveUsername", "Response: " + response);

                        String username = extractUsernameFromResponse(response);

                        Log.d("RetrievedUsername","Username: " + username);

                        if (username != null)
                        {
                            Log.d("UsernameRetrieval", "Successful");
                            profile_name_textview.setText("Hello, " + username);
                        }
                        else
                        {
                            Log.e("RetrieveUsername", "Failed to retrieve username");
                            Toast.makeText(getContext(), "Failed to retrieve username", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("RetrieveUsername", "Error: " + error.toString());
                        Toast.makeText(getContext(), "Error retrieving username", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("email_id", emailId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    private String extractUsernameFromResponse(String response) {
        // Check if the response contains "Connection Success"
        if (response.contains("Connection Successfull")) {
            // Extract the username part
            String[] parts = response.split("Connection Successfull");
            if (parts.length > 1) {
                // Trim any extra spaces and return the username
                return parts[1].trim();
            }
        }

        Log.e("ExtractUsername", "Failed to extract username from response: " + response);
        return null;
    }


    private void showConfirmLogoutDialogBox() {

        Dialog confirmLogoutDialog = new Dialog(requireContext());
        confirmLogoutDialog.setContentView(R.layout.custom_confirmlogout_dialog_box);

        MaterialButton logoutButton = confirmLogoutDialog.findViewById(R.id.logoutButton_dialogBox);
        MaterialButton cancelButton = confirmLogoutDialog.findViewById(R.id.cancelButton_dialogBox);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManagement.logout();
                Intent intent = new Intent(requireContext(), LogoutSplashScreen.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                requireActivity().finish();
                confirmLogoutDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLogoutDialog.dismiss(); // Dismiss the dialog
            }
        });

        confirmLogoutDialog.show();
    }
}
