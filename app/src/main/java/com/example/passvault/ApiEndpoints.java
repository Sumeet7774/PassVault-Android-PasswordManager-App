package com.example.passvault;

public class ApiEndpoints {
    private static final String base_url = "https://pass-vault-project.000webhostapp.com/";
    public static final String register_url = base_url + "register.php";
    public static final String login_url = base_url + "login.php";
    public static final String updatePassword_url = base_url + "update_password.php";
    public static final String insertData_url = base_url + "insert_data.php";
    public static final String retrieveUserId_url = base_url + "get_user_id.php";
    public static final String displaySavedData_url = base_url + "display_data.php";
    public static final String retrievePasswordId_url = base_url + "get_password_id.php";
    public static final String updateData_url = base_url + "update_data.php";
    public static final String retrieveUsername = base_url + "get_username.php";
    public static final String deleteData_url = base_url + "delete_data.php";
}