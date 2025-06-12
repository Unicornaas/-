package org.example.filemanagementsystem03.model;

/**
 * 用户实体类
 */
public class User {
    private int id;           // 用户ID
    private String username;  // 用户名
    private String password;  // 密码
    private String email;     // 邮箱
    private int role;         // 角色(0: 管理员, 1: 普通用户)

    // 无参构造
    public User() {
    }

    // 带参构造
    public User(String username, String password, String email, int role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    // Getter和Setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
    
    // 角色判断方法
    public boolean isAdmin() {
        return role == 0;
    }
} 