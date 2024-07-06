package com.example.passvault;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateData extends AppCompatActivity {

    SessionManagement sessionManagement;
    EditText username_update_edittext;
    EditText emailid_update_edittext;
    EditText password_update_edittext;
    EditText servicetype_update_edittext;
    ImageButton updateDataPage_backButton;
    Button update_button;
    String userId;
    String passwordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);

        username_update_edittext = findViewById(R.id.updateUsername_edittext);
        emailid_update_edittext = findViewById(R.id.updateEmail_edittext);
        password_update_edittext = findViewById(R.id.updatePassword_edittext);
        servicetype_update_edittext = findViewById(R.id.updateServiceType_edittext);
        updateDataPage_backButton = findViewById(R.id.updateData_backButton);
        update_button = findViewById(R.id.updateDetails_Button);

        sessionManagement = new SessionManagement(this);
        userId = sessionManagement.getUserId();

        String username = getIntent().getStringExtra("username");
        String emailId = getIntent().getStringExtra("emailId");
        String password = getIntent().getStringExtra("password");
        String serviceType = getIntent().getStringExtra("serviceType");

        username_update_edittext.setText(username);
        emailid_update_edittext.setText(emailId);
        password_update_edittext.setText(password);
        servicetype_update_edittext.setText(serviceType);

        retrievePasswordId(userId, username, emailId, password, serviceType);

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordId != null)
                {
                    updateData();
                } else {
                    Toast.makeText(UpdateData.this, "Password ID not retrieved yet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateDataPage_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Finish the current activity
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        password_update_edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (password_update_edittext.getRight() - password_update_edittext.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility(password_update_edittext);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void retrievePasswordId(String userId, String username, String emailId, String password, String serviceType) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.retrievePasswordId_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RetrievePasswordId", "Response: " + response);

                        passwordId = extractPasswordIdFromResponse(response);

                        Log.d("PasswordId", "Password Id: " + passwordId);

                        if (passwordId != null)
                        {
                            Log.d("PasswordIdRetrieval", "Successfull");
                        }
                        else
                        {
                            Log.e("RetrievePasswordId", "Failed to retrieve password ID");
                            Toast.makeText(UpdateData.this, "Failed to retrieve password ID", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("RetrievePasswordId", "Error: " + error.toString());
                Toast.makeText(UpdateData.this, "Error retrieving password ID", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("username", username);
                params.put("email_id", emailId);
                params.put("password", password);
                params.put("service_type", serviceType);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void updateData() {
        final String updatedUsername = username_update_edittext.getText().toString().trim();
        final String updatedEmailId = emailid_update_edittext.getText().toString().trim();
        final String updatedPassword = password_update_edittext.getText().toString().trim();
        final String updatedServiceType = servicetype_update_edittext.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.updateData_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("UpdateData", "Response: " + response);
                showDataUpdatedSuccessDialogBox();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("UpdateData", "Error: " + error.toString());
                Toast.makeText(UpdateData.this, "Error updating data", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("password_id", passwordId);
                params.put("user_id", userId);
                params.put("username", updatedUsername);
                params.put("email_id", updatedEmailId);
                params.put("password", updatedPassword);
                params.put("service_type", updatedServiceType);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String extractPasswordIdFromResponse(String response) {
        String pattern = "Connection Successfull\\s*(\\d+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(response);

        if (m.find()) {
            return m.group(1);
        }

        Log.d("extractPasswordIdFromResponse", "Failed to extract password ID from response: " + response);
        return null;
    }

    private void showDataUpdatedSuccessDialogBox() {
        Dialog successful_dataUpdated_dialogBox = new Dialog(UpdateData.this);
        successful_dataUpdated_dialogBox.setContentView(R.layout.custom_data_updated_successfully_dialogbox);
        Button dialogBox_ok_button = successful_dataUpdated_dialogBox.findViewById(R.id.okButtonDataUpdatedSuccesfull_dialogBox);
        dialogBox_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successful_dataUpdated_dialogBox.dismiss();
                Intent intent = new Intent(UpdateData.this, HomePage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
        successful_dataUpdated_dialogBox.show();
    }

    private void togglePasswordVisibility(EditText editText) {
        if (editText.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
            // Show Password
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            setCompoundDrawablesWithIntrinsicBounds(editText, R.drawable.baseline_lock_outline_24, R.drawable.eye_invisible);
        } else {
            // Hide Password
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            setCompoundDrawablesWithIntrinsicBounds(editText, R.drawable.baseline_lock_outline_24, R.drawable.eye_visible);
        }
        editText.setSelection(editText.getText().length());
    }

    private void setCompoundDrawablesWithIntrinsicBounds(EditText editText, int start, int end) {
        editText.setCompoundDrawablesWithIntrinsicBounds(start, 0, end, 0);
        editText.setCompoundDrawablePadding(16); // Adjust padding as needed
    }
}