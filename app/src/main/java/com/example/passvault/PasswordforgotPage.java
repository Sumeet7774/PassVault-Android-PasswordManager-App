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
    private Button update_password_button;
    private EditText email_reset_edittext, new_password_edittext, confirm_new_password_edittext;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_passwordforgot_page);

        forgot_password_back_button = findViewById(R.id.forgot_password_backButton);
        email_reset_edittext = findViewById(R.id.passwordResetEmail_edittext);
        new_password_edittext = findViewById(R.id.newPassword_edittext);
        confirm_new_password_edittext = findViewById(R.id.newPasswordConfirm_edittext);
        update_password_button = findViewById(R.id.resetPassword_Button);

        auth = FirebaseAuth.getInstance();

        forgot_password_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PasswordforgotPage.this, LoginPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        new_password_edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (new_password_edittext.getRight() - new_password_edittext.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility(new_password_edittext);
                        return true;
                    }
                }
                return false;
            }
        });

        confirm_new_password_edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (confirm_new_password_edittext.getRight() - confirm_new_password_edittext.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility(confirm_new_password_edittext);
                        return true;
                    }
                }
                return false;
            }
        });

        update_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_txt = email_reset_edittext.getText().toString();
                String newPassword_txt = new_password_edittext.getText().toString();
                String confirmNewPassword_txt = confirm_new_password_edittext.getText().toString();

                if(TextUtils.isEmpty(email_txt))
                {
                    Toast.makeText(PasswordforgotPage.this, "Please provide your credentials", Toast.LENGTH_SHORT).show();
                }
                else if (!isValidEmail(email_txt))
                {
                    Toast.makeText(PasswordforgotPage.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                }
                else if( newPassword_txt.length() < 5 || confirmNewPassword_txt.length() < 5)
                {
                    Toast.makeText(PasswordforgotPage.this, "Please ensure that your password consists of at least 5 characters.", Toast.LENGTH_LONG).show();
                }
                else if(!newPassword_txt.equals(confirmNewPassword_txt))
                {
                    Toast.makeText(PasswordforgotPage.this, "Password don't match", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    updatePassword(email_txt, newPassword_txt);
                }
            }
        });
    }

    private boolean isValidEmail(CharSequence target)
    {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    public void updatePassword(final String emailid, final String password)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.updatePassword_url, response -> {

            if (response.contains("success")) {
                showPasswordUpdatedSuccessDialog();
            }
            else if(response.contains("email doesnt exist"))
            {
                Toast.makeText(PasswordforgotPage.this, "User Email Doesn't Exist", Toast.LENGTH_SHORT).show();
            }
            else if(response.contains("enter new password"))
            {
                Toast.makeText(PasswordforgotPage.this, "New password is same as old password", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(PasswordforgotPage.this, "Failed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PasswordforgotPage.this, PasswordforgotPage.class);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(PasswordforgotPage.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                Log.d("VOLLEY", volleyError.getMessage());
                Intent intent = new Intent(PasswordforgotPage.this, PasswordforgotPage.class);
                startActivity(intent);
            }
        }) {
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("email_id", emailid);
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

    private void showPasswordUpdatedSuccessDialog()
    {
        Dialog successful_password_updated_dialogBox = new Dialog(PasswordforgotPage.this);
        successful_password_updated_dialogBox.setContentView(R.layout.custom_password_updated_successfull_dialog_box);
        Button dialogBox_ok_button = successful_password_updated_dialogBox.findViewById(R.id.okButtonPasswordUpdatedSuccesful_dialogBox);
        dialogBox_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                successful_password_updated_dialogBox.dismiss();
                Intent intent = new Intent(PasswordforgotPage.this, IndexPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });
        successful_password_updated_dialogBox.show();
    }
}