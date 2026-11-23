package com.foodorder.model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_CUSTOMER = "customer";

    private int id;
    private String name;
    private String email;
    private String password; // hash BCrypt; tidak pernah disimpan di session
    private String role = ROLE_CUSTOMER;
    private String phone;
    private boolean active = true;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isAdmin() { return ROLE_ADMIN.equals(role); }
}
