package com.example.passvault;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateData extends AppCompatActivity {

    private EditText password_update_edittext;
    private ImageButton updateDataPage_backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);

        updateDataPage_backButton = findViewById(R.id.updateData_backButton);
        password_update_edittext = findViewById(R.id.updatePassword_edittext);

        updateDataPage_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Finish the current activity
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        password_update_edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (password_update_edittext.getRight() - password_update_edittext.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility(password_update_edittext);
                        return true;
                    }
                }
                return false;
            }
        });
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
}