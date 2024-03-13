package com.example.passvault;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUp extends AppCompatActivity {

    private Button sign_up;
    private ImageButton signup_back_button;
    private EditText user_name_edit_text;
    private EditText email_edit_text;
    private EditText password_edit_text;
    private EditText confirm_password_edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sign_up = findViewById(R.id.signupButton);
        signup_back_button = findViewById(R.id.signupPage_backButton);
        user_name_edit_text = findViewById(R.id.username_edittext);
        email_edit_text = findViewById(R.id.email_edittext);
        password_edit_text = findViewById(R.id.password_edittext);
        confirm_password_edit_text = findViewById(R.id.confirmpassword_edittext);

        signup_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, IndexPage.class);
                startActivity(intent);
                finish();
            }
        });

    }
}