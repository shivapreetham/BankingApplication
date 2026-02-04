package com.banking.model;

import java.util.Date;


public class User {

    private int userId;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;
    private Date createdDate;

    // Default Constructor
    public User() {
        this.createdDate = new Date();
        this.userId = 0;
        this.username = "";
        this.password = "";
        this.email = "";
        this.phone = "";
        this.address = "";
    }

    // Full Constructor
    public User(int userId, String username, String password, String email, String phone, String address, Date createdDate) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.createdDate = createdDate;
    }

    // Constructor for registration
    public User(String username, String password, String email, String phone, String address) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.createdDate = new Date();
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "User{"
                + "userId=" + userId
                + ", username='" + username + '\''
                + ", email='" + email + '\''
                + ", phone='" + phone + '\''
                + ", address='" + address + '\''
                + ", createdDate=" + createdDate
                + '}';
    }
}
