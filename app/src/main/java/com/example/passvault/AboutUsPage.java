package com.example.passvault;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutUsPage extends AppCompatActivity {

    TextView member1Linkedin;
    TextView member2Linkedin;
    TextView member3Linkedin;
    TextView member4Linkedin;
    ImageButton about_us_backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about_us_page);

        about_us_backButton = findViewById(R.id.aboutus_backButton);
        member1Linkedin = findViewById(R.id.tarj_linkedin);
        member2Linkedin = findViewById(R.id.rajdeep_linkedin);
        member3Linkedin = findViewById(R.id.sumeet_linkedin);
        member4Linkedin = findViewById(R.id.darsh_linkedin);

        about_us_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutUsPage.this, HomePage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        member1Linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String linkedinProfileUrl = "https://www.linkedin.com/in/tarj-baxi-86621a29b/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedinProfileUrl));
                startActivity(intent);
            }
        });

        member2Linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkedinProfileUrl = "https://www.linkedin.com/in/rajdeep-dharajiya/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedinProfileUrl));
                startActivity(intent);
            }
        });

        member3Linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String linkedinProfileUrl = "https://www.linkedin.com/in/sumeet-kapadia-25b700270/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedinProfileUrl));
                startActivity(intent);
            }
        });

        member4Linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String linkedinProfileUrl = "https://www.linkedin.com/in/darsh-panchal-b81415249/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedinProfileUrl));
                startActivity(intent);
            }
        });
    }
}