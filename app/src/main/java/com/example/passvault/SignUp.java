package com.example.passvault;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    private Button sign_up;
    private ImageButton signup_back_button;
    private EditText email_edit_text, password_edit_text, confirm_password_edit_text;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sign_up = findViewById(R.id.signupButton);
        signup_back_button = findViewById(R.id.signupPage_backButton);
        email_edit_text = findViewById(R.id.email_signup_edittext);
        password_edit_text = findViewById(R.id.password_signup_edittext);
        confirm_password_edit_text = findViewById(R.id.confirmpassword_signup_edittext);

        auth = FirebaseAuth.getInstance();

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = email_edit_text.getText().toString();
                String txt_password = password_edit_text.getText().toString();
                String txt_confirm_password = confirm_password_edit_text.getText().toString();

                if( TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_confirm_password))
                {
                    Toast.makeText(SignUp.this, "Please provide your credentials", Toast.LENGTH_SHORT).show();
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
                    registerUser(txt_email, txt_password);
                }
            }
        });

    }

    private void registerUser(String email, String password)
    {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(SignUp.this, "Registration Successfull", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUp.this, IndexPage.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(SignUp.this, "Registration Unsuccessfull", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}