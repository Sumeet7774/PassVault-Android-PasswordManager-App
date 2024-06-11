package com.example.passvault;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AddDataPage extends AppCompatActivity {

    private EditText username_add_edittext;
    private EditText email_add_edittext;
    private EditText password_add_edittext;
    private EditText serviceType_add_edittext;
    private Button saveData_button;
    private ImageButton addDataPage_backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_data_page);

        username_add_edittext = findViewById(R.id.username_addData_edittext);
        email_add_edittext = findViewById(R.id.email_addData_edittext);
        password_add_edittext = findViewById(R.id.password_addData_edittext);
        serviceType_add_edittext = findViewById(R.id.serviceType_addData_edittext);
        addDataPage_backButton = findViewById(R.id.addDataPage_backButton);
        saveData_button = findViewById(R.id.saveDataButton);

        addDataPage_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddDataPage.this, HomePage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        saveData_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_username = username_add_edittext.getText().toString();
                String txt_emailid = email_add_edittext.getText().toString();
                String txt_password = password_add_edittext.getText().toString();
                String txt_servicetype = serviceType_add_edittext.getText().toString();

                Log.d("AddDataPage", "Username: " + txt_username);
                Log.d("AddDataPage", "Email ID: " + txt_emailid);
                Log.d("AddDataPage", "Password: " + txt_password);
                Log.d("AddDataPage", "Service Type: " + txt_servicetype);

                if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_emailid) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_servicetype))
                {
                    Toast.makeText(AddDataPage.this, "Please provide your credentials", Toast.LENGTH_SHORT).show();
                }
                else if (!isValidEmail(txt_emailid))
                {
                    Toast.makeText(AddDataPage.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                }
                else if( txt_password.length() < 5 )
                {
                    Toast.makeText(AddDataPage.this, "Please ensure that your password consists of at least 5 characters.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    insertData(txt_username,txt_emailid,txt_password,txt_servicetype);
                }
            }
        });
    }

    private boolean isValidEmail(CharSequence target)
    {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void insertData(final String username, final String email_id, final String password, final String service_type)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.insertData_url, response -> {

            if(response.contains("Data inserted successfully"))
            {
                showDataSavedSuccessDialogBox(username,email_id,password,service_type);
            }
            else if(response.contains("Error inserting data: "))
            {
                Toast.makeText(AddDataPage.this,"Error", Toast.LENGTH_SHORT).show();
            }
            else if(response.contains("Invalid Password"))
            {
                Toast.makeText(AddDataPage.this,"Invalid", Toast.LENGTH_SHORT).show();
            }
            else if(response.contains("User Doesn't Exist"))
            {
               Toast.makeText(AddDataPage.this,"User not found", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(AddDataPage.this, "Failed to save data.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(AddDataPage.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                Log.d("VOLLEY", volleyError.getMessage());
                Intent intent = new Intent(AddDataPage.this, AddDataPage.class);
                startActivity(intent);
            }
        }) {
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("username",username);
                params.put("email_id", email_id);
                params.put("password", password);
                params.put("service_type", service_type);

                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void showDataSavedSuccessDialogBox(final String username, final String email_id, final String password, final String service_type)
    {
        Dialog successful_dataSaved_dialogBox = new Dialog(AddDataPage.this);
        successful_dataSaved_dialogBox.setContentView(R.layout.custom_data_inserted_successfully_dialog_box);
        Button dialogBox_ok_button = successful_dataSaved_dialogBox.findViewById(R.id.okButtonDataSavedSuccesfull_dialogBox);
        dialogBox_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                successful_dataSaved_dialogBox.dismiss();
                Intent intent = new Intent(AddDataPage.this, HomePage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
        successful_dataSaved_dialogBox.show();
    }
}