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

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    SessionManagement sessionManagement;
    private Button sign_up;
    private ImageButton signup_back_button;
    private EditText username_edit_text, email_edit_text, password_edit_text, confirm_password_edit_text;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sign_up = findViewById(R.id.signupButton);
        signup_back_button = findViewById(R.id.signupPage_backButton);
        username_edit_text = findViewById(R.id.username_signup_edittext);
        email_edit_text = findViewById(R.id.email_signup_edittext);
        password_edit_text = findViewById(R.id.password_signup_edittext);
        confirm_password_edit_text = findViewById(R.id.confirmpassword_signup_edittext);


        sessionManagement = new SessionManagement(getApplication());

        password_edit_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (password_edit_text.getRight() - password_edit_text.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility(password_edit_text);
                        return true;
                    }
                }
                return false;
            }
        });

        confirm_password_edit_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (confirm_password_edit_text.getRight() - confirm_password_edit_text.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility(confirm_password_edit_text);
                        return true;
                    }
                }
                return false;
            }
        });

        signup_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, IndexPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_username = username_edit_text.getText().toString();
                String txt_email = email_edit_text.getText().toString();
                String txt_password = password_edit_text.getText().toString();
                String txt_confirm_password = confirm_password_edit_text.getText().toString();

                if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_confirm_password))
                {
                    Toast.makeText(SignUp.this, "Please provide your credentials", Toast.LENGTH_SHORT).show();
                }
                else if (!isValidEmail(txt_email))
                {
                    Toast.makeText(SignUp.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                }
                else if( txt_password.length() < 5 || txt_confirm_password.length() < 5)
                {
                    Toast.makeText(SignUp.this, "Please ensure that your password consists of at least 5 characters.", Toast.LENGTH_LONG).show();
                }
                else if(!txt_password.equals(txt_confirm_password))
                {
                    Toast.makeText(SignUp.this, "Password don't match", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    registerUser(txt_username, txt_email, txt_password);
                }
            }
        });
    }

    private boolean isValidEmail(CharSequence target)
    {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void registerUser(final String username,final String email,final String password)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.register_url, response -> {

            if (response.contains("success"))
            {
                showRegistrationSuccessDialog();
            }
            else if(response.contains("User Email Already Exists! Login Here"))
            {
                Toast.makeText(SignUp.this, "User Already Exists", Toast.LENGTH_SHORT).show();
            }
            else
            {
                showUnsuccesfulRegistrationDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(SignUp.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                Log.d("VOLLEY", volleyError.getMessage());
                Intent intent = new Intent(SignUp.this, SignUp.class);
                startActivity(intent);
            }
        }) {
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("username", username);
                params.put("email_id", email);
                params.put("password", password);

                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
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


    private void showRegistrationSuccessDialog()
    {
        Dialog successful_registration_dialogBox = new Dialog(SignUp.this);
        successful_registration_dialogBox.setContentView(R.layout.custom_registration_successfull_dialog_box);
        Button dialogBox_ok_button = successful_registration_dialogBox.findViewById(R.id.okButtonSuccesfull_dialogBox);
        dialogBox_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                successful_registration_dialogBox.dismiss();
                Intent intent = new Intent(SignUp.this, IndexPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
        successful_registration_dialogBox.show();
    }

    private void showUnsuccesfulRegistrationDialog()
    {
        Dialog unsuccessful_registration_dialogBox = new Dialog(SignUp.this);
        unsuccessful_registration_dialogBox.setContentView(R.layout.custom_registration_unsuccessfull_dialog_box);
        Button dialogBox_ok_unsuccessful_button = unsuccessful_registration_dialogBox.findViewById(R.id.okButton_Unsuccessfull_dialogBox);
        dialogBox_ok_unsuccessful_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                unsuccessful_registration_dialogBox.dismiss();
            }
        });
        unsuccessful_registration_dialogBox.show();
    }
}