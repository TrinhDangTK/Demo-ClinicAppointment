package com.clinicappointment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DepartmentForm {

    @NotBlank(message = "Ten khoa khong duoc de trong")
    @Size(max = 100, message = "Ten khoa toi da 100 ky tu")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
