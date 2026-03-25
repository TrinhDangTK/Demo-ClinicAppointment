package com.clinicappointment.dto;

public record DoctorCardDto(
    Long id,
    String name,
    String specialty,
    String departmentName,
    String imageUrl
) {
}
