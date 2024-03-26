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

public class PasswordforgotPage extends AppCompatActivity {

    private ImageButton forgot_password_back_button;
    private Button send_email;
    private EditText email_reset_edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_passwordforgot_page);

        forgot_password_back_button = findViewById(R.id.forgot_password_backButton);
        email_reset_edittext = findViewById(R.id.sendEmail_edittext);
        send_email = findViewById(R.id.resetPassword_email_Button);

        forgot_password_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PasswordforgotPage.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });

    }
}