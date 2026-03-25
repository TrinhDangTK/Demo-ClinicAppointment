package com.clinicappointment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterForm {

    @NotBlank(message = "Username khong duoc de trong")
    @Size(min = 4, max = 50, message = "Username tu 4 den 50 ky tu")
    private String username;

    @NotBlank(message = "Password khong duoc de trong")
    @Size(min = 6, max = 100, message = "Password tu 6 den 100 ky tu")
    private String password;

    @NotBlank(message = "Email khong duoc de trong")
    @Email(message = "Email khong dung dinh dang")
    @Size(max = 100, message = "Email toi da 100 ky tu")
    private String email;

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
}
