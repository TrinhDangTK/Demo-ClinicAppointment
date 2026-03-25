package com.clinicappointment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DoctorForm {

    @NotBlank(message = "Ten bac si khong duoc de trong")
    @Size(max = 100, message = "Ten bac si toi da 100 ky tu")
    private String name;

    @Size(max = 100, message = "Chuyen khoa toi da 100 ky tu")
    private String specialty;

    @Size(max = 255, message = "Link anh toi da 255 ky tu")
    private String imageUrl;

    @NotNull(message = "Vui long chon khoa")
    private Long departmentId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
}
