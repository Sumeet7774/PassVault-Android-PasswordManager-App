package com.example.passvault;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SessionManagement {
        // SharedPreferences file name
        private SharedPreferences prefs;

    public SessionManagement(Context context) {
        prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    public void setEmailid(String emailid) {
        prefs.edit().putString("email_id", emailid).apply();
    }

    public String getEmailid() {
        return prefs.getString("email_id", "");
    }

    public void logout()
    {
        prefs.edit().remove("email_id").apply();
    }
}
