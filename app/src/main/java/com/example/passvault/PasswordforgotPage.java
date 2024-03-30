package com.example.passvault;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;


public class PasswordforgotPage extends AppCompatActivity {

    private ImageButton forgot_password_back_button;
    private Button send_email_button;
    private EditText email_reset_edittext;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_passwordforgot_page);

        forgot_password_back_button = findViewById(R.id.forgot_password_backButton);
        email_reset_edittext = findViewById(R.id.sendEmail_edittext);
        send_email_button = findViewById(R.id.resetPassword_email_Button);

        auth = FirebaseAuth.getInstance();

        forgot_password_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PasswordforgotPage.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });

        send_email_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_txt = email_reset_edittext.getText().toString();

                if(TextUtils.isEmpty(email_txt))
                {
                    Toast.makeText(PasswordforgotPage.this, "Please provide your credentials", Toast.LENGTH_SHORT).show();
                }
                else if (!isValidEmail(email_txt))
                {
                    Toast.makeText(PasswordforgotPage.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //sendResetEmail(email_txt);
                }
            }
        });
    }

    private boolean isValidEmail(CharSequence target)
    {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private void updatePassword(String email, String newPassword) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.updatePassword_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")) {
                            Toast.makeText(PasswordforgotPage.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PasswordforgotPage.this, IndexPage.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(PasswordforgotPage.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PasswordforgotPage.this, PasswordforgotPage.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PasswordforgotPage.this, "Failed to update password: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email_id", email);
                params.put("new_password", newPassword);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}