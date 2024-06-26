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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginPage extends AppCompatActivity {

    SessionManagement sessionManagement;
    private TextView forgot_password;
    private Button loginButton;
    private ImageButton login_back_button;
    private EditText email_edittext, password_edittext, confirm_password_edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        forgot_password = findViewById(R.id.forgotPassword_textview);
        loginButton = findViewById(R.id.login_Button);
        login_back_button = findViewById(R.id.loginPage_backButton);
        email_edittext = findViewById(R.id.email_login_edittext);
        password_edittext = findViewById(R.id.password_login_edittext);
        confirm_password_edittext = findViewById(R.id.confirmpassword_login_edittext);

        sessionManagement = new SessionManagement(getApplication());

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, PasswordforgotPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });

        password_edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (password_edittext.getRight() - password_edittext.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility(password_edittext);
                        return true;
                    }
                }
                return false;
            }
        });

        confirm_password_edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (confirm_password_edittext.getRight() - confirm_password_edittext.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility(confirm_password_edittext);
                        return true;
                    }
                }
                return false;
            }
        });

        login_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, IndexPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String txt_emailid = email_edittext.getText().toString();
               String txt_pass_word = password_edittext.getText().toString();
               String txt_confirmPassword = confirm_password_edittext.getText().toString();

                if(TextUtils.isEmpty(txt_emailid) || TextUtils.isEmpty(txt_pass_word) || TextUtils.isEmpty(txt_confirmPassword))
                {
                    Toast.makeText(LoginPage.this, "Please provide your credentials", Toast.LENGTH_SHORT).show();
                }
                else if (!isValidEmail(txt_emailid))
                {
                    Toast.makeText(LoginPage.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                }
                else if( txt_pass_word.length() < 5 || txt_confirmPassword.length() < 5)
                {
                    Toast.makeText(LoginPage.this, "Please ensure that your password consists of at least 5 characters.", Toast.LENGTH_LONG).show();
                }
                else if(!txt_pass_word.equals(txt_confirmPassword))
                {
                    Toast.makeText(LoginPage.this, "Password don't match", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loginUser(txt_emailid,txt_pass_word);
                }
            }
        });
    }

    private boolean isValidEmail(CharSequence target)
    {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void loginUser(final String emailid, final String password)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.login_url, response -> {

            if (response.contains("success"))
            {
                //sessionManagement.setUserId(userid);
                sessionManagement.setEmailid(emailid);
                showLoginSuccessDialog(emailid);
            }
            else if(response.contains("Invalid Password"))
            {
                Toast.makeText(LoginPage.this, "Invalid Password", Toast.LENGTH_SHORT).show();
            }
            else if(response.contains("User Doesnt Exist"))
            {
                Toast.makeText(LoginPage.this, "User Doesn't Exist", Toast.LENGTH_SHORT).show();
            }
            else {
                showUnsuccesfulLoginDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(LoginPage.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                Log.d("VOLLEY", volleyError.getMessage());
                Intent intent = new Intent(LoginPage.this, LoginPage.class);
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


    private void showLoginSuccessDialog(final String emailid)
    {
        Dialog successful_login_dialogBox = new Dialog(LoginPage.this);
        successful_login_dialogBox.setContentView(R.layout.custom_login_successfull_dialog_box);
        Button dialogBox_ok_button = successful_login_dialogBox.findViewById(R.id.okButtonLoginSuccesfull_dialogBox);
        dialogBox_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                successful_login_dialogBox.dismiss();
                Intent intent = new Intent(LoginPage.this, LoadingSplashScreen.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
        successful_login_dialogBox.show();
    }

    private void showUnsuccesfulLoginDialog()
    {
        Dialog unsuccessful_login_dialogBox = new Dialog(LoginPage.this);
        unsuccessful_login_dialogBox.setContentView(R.layout.custom_login_unsuccessfull_dialog_box);
        Button dialogBox_ok_unsuccessful_button = unsuccessful_login_dialogBox.findViewById(R.id.okButtonLoginUnuccesfull_dialogBox);
        dialogBox_ok_unsuccessful_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                unsuccessful_login_dialogBox.dismiss();
            }
        });
        unsuccessful_login_dialogBox.show();
    }
}