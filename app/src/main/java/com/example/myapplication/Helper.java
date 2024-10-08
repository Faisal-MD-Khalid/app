package com.example.myapplication;



public class Helper {

    String name, email, username, password,signup_religion_spinner;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void signup_religion_spinner(String signup_religion_spinner) {
        this.signup_religion_spinner = signup_religion_spinner;
    }

    public Helper(String name, String email, String username, String password,String signup_religion_spinner) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.signup_religion_spinner = signup_religion_spinner;
    }

    public Helper() {
    }
}
