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

    public String getUserId() {
        return prefs.getString("user_id","");
    }

    public void setUserId(String userid){
        prefs.edit().putString("user_id",userid).apply();
    }

    public String getPasswordId()
    {
        return prefs.getString("password_id","");
    }

    public void setPasswordId(String passwordId){
        prefs.edit().putString("password_id",passwordId).apply();
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
        prefs.edit().remove("user_id").apply();
    }
}
