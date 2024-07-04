package com.example.passvault;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddDataPage extends AppCompatActivity {

    SessionManagement sessionManagement;
    private EditText username_add_edittext;
    private EditText email_add_edittext;
    private EditText password_add_edittext;
    private EditText serviceType_add_edittext;
    private Button saveData_button;
    private ImageButton addDataPage_backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_data_page);

        username_add_edittext = findViewById(R.id.username_addData_edittext);
        email_add_edittext = findViewById(R.id.email_addData_edittext);
        password_add_edittext = findViewById(R.id.password_addData_edittext);
        serviceType_add_edittext = findViewById(R.id.serviceType_addData_edittext);
        addDataPage_backButton = findViewById(R.id.addDataPage_backButton);
        saveData_button = findViewById(R.id.saveDataButton);

        sessionManagement = new SessionManagement(getApplicationContext());

        addDataPage_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddDataPage.this, HomePage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        password_add_edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (password_add_edittext.getRight() - password_add_edittext.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility(password_add_edittext);
                        return true;
                    }
                }
                return false;
            }
        });

        saveData_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_username = username_add_edittext.getText().toString();
                String txt_emailid = email_add_edittext.getText().toString();
                String txt_password = password_add_edittext.getText().toString();
                String txt_servicetype = serviceType_add_edittext.getText().toString();
                String userid = sessionManagement.getUserId();
                String existingEmailId = sessionManagement.getEmailid();

                Log.d("AddDataPage", "User Id: "+ userid);
                Log.d("AddDataPage", "Existing Email id: "+ existingEmailId);
                Log.d("AddDataPage", "Username: " + txt_username);
                Log.d("AddDataPage", "Email ID: " + txt_emailid);
                Log.d("AddDataPage", "Password: " + txt_password);
                Log.d("AddDataPage", "Service Type: " + txt_servicetype);

                if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_emailid) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_servicetype)) {
                    Toast.makeText(AddDataPage.this, "Please provide your credentials", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(txt_emailid)) {
                    Toast.makeText(AddDataPage.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                } else if (txt_password.length() < 5) {
                    Toast.makeText(AddDataPage.this, "Please ensure that your password consists of at least 5 characters.", Toast.LENGTH_LONG).show();
                } else {
                    // Retrieve user_id using email_id
                    retrieveUserId(existingEmailId);
                }
            }
        });
    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void retrieveUserId(final String email_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.retrieveUserId_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("retrieveUserId", "Response: " + response);

                String txt_username = username_add_edittext.getText().toString();
                String txt_emailid = email_add_edittext.getText().toString();
                String txt_password = password_add_edittext.getText().toString();
                String txt_servicetype = serviceType_add_edittext.getText().toString();

                String userId = extractUserIdFromResponse(response);

                if (userId != null)
                {
                    sessionManagement.setUserId(userId);
                    insertData(userId, txt_username, txt_emailid, txt_password, txt_servicetype);
                }
                else
                {
                    Toast.makeText(AddDataPage.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddDataPage.this, "Error retrieving user_id", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email_id", email_id);
                return params;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private String extractUserIdFromResponse(String response) {
        String pattern = "Connection Successfull\\s*(\\d+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(response);

        if (m.find())
        {
            return m.group(1);
        }

        Log.d("extractUserIdFromResponse", "Failed to extract user ID from response: " + response);
        return null;
    }


    public void insertData(final String user_id, final String username, final String email_id, final String password, final String service_type) {

        String existingemailid = sessionManagement.getEmailid();

        Log.d("insertData", "User Id: " + user_id);
        Log.d("insertData", "Existing Email Id: " + existingemailid);
        Log.d("insertData", "Username: " + username);
        Log.d("insertData", "Email ID: " + email_id);
        Log.d("insertData", "Password: " + password);
        Log.d("insertData", "Service Type: " + service_type);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.insertData_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.contains("Data inserted successfully")) {
                    showDataSavedSuccessDialogBox(username, email_id, password, service_type);
                } else if (response.contains("Error inserting data")) {
                    Toast.makeText(AddDataPage.this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddDataPage.this, "Failed to save data.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddDataPage.this, "Error inserting data", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("username", username);
                params.put("email_id", email_id);
                params.put("password", password);
                params.put("service_type", service_type);

                return params;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
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

    private void showDataSavedSuccessDialogBox(final String username, final String email_id, final String password, final String service_type) {
        Dialog successful_dataSaved_dialogBox = new Dialog(AddDataPage.this);
        successful_dataSaved_dialogBox.setContentView(R.layout.custom_data_inserted_successfully_dialog_box);
        Button dialogBox_ok_button = successful_dataSaved_dialogBox.findViewById(R.id.okButtonDataSavedSuccesfull_dialogBox);
        dialogBox_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successful_dataSaved_dialogBox.dismiss();
                Intent intent = new Intent(AddDataPage.this, HomePage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
        successful_dataSaved_dialogBox.show();
    }
}
