package com.example.passvault;

public class UserSavedData {
    private String username;
    private String emailId;
    private String password;
    private String serviceType;

    public UserSavedData(String username,String emailId ,String password, String serviceType)
    {
        this.username = username;
        this.emailId = emailId;
        this.password = password;
        this.serviceType = serviceType;
    }

    public String getUsername()
    {
        return username;
    }

    public String getEmailId()
    {
        return emailId;
    }

    public String getpassword()
    {
        return password;
    }

    public String getServiceType()
    {
        return serviceType;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setEmailId(String emailId)
    {
        this.emailId = emailId;
    }

    public void setpassword(String password)
    {
        this.password = password;
    }

    public void setServiceType(String serviceType)
    {
        this.serviceType = serviceType;
    }
}
