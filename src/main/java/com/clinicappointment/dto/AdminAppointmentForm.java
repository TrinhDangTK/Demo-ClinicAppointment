package com.clinicappointment.dto;

import com.clinicappointment.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class AdminAppointmentForm {

    @NotNull(message = "Vui long chon benh nhan")
    private Long patientId;

    @NotNull(message = "Vui long chon bac si")
    private Long doctorId;

    @NotNull(message = "Vui long chon ngay gio kham")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime appointmentTime;

    @NotNull(message = "Vui long chon trang thai")
    private AppointmentStatus status;

    private String note;

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
