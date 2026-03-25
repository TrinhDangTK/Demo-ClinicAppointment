package com.clinicappointment.dto;

import java.time.LocalDateTime;

public record MyAppointmentDto(
    Long id,
    String doctorName,
    LocalDateTime appointmentTime,
    String departmentName
) {
}
