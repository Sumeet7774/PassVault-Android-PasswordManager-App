package com.example.passvault;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ForgotPasswordPage extends AppCompatActivity {

    private ImageButton forgotpassword_back_button;
    private Button send_email;
    private EditText email_reset_edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_page);

        forgotpassword_back_button = findViewById(R.id.forgotPasswordPage_backButton);
        send_email = findViewById(R.id.emailSendLink_button);
        email_reset_edittext = findViewById(R.id.emailReset_edittext);

        forgotpassword_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordPage.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });


    }
}