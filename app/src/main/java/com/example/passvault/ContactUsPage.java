package com.example.passvault;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ContactUsPage extends AppCompatActivity {

    TextView sumeetkapadia_emailid_textview;
    TextView sumeetkapadia_linkedin_textview;
    ImageButton contact_us_backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact_us_page);

        sumeetkapadia_emailid_textview = findViewById(R.id.sumeetkapadia_emailid);
        sumeetkapadia_linkedin_textview = findViewById(R.id.sumeetkapadia_linkedin);
        contact_us_backButton = findViewById(R.id.contactus_backButton);

        contact_us_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactUsPage.this, HomePage.class);
                intent.putExtra("TARGET_FRAGMENT", "ProfileFragment");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        sumeetkapadia_emailid_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "sumeetkapadia47@gmail.com", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry and questions");
                startActivity(Intent.createChooser(intent, "Send email..."));
            }
        });

        sumeetkapadia_linkedin_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String linkedinProfileUrl = "https://www.linkedin.com/in/sumeet-kapadia-25b700270/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkedinProfileUrl));
                startActivity(intent);
            }
        });
    }
}