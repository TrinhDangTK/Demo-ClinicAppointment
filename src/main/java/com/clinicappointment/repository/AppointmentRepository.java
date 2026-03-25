package com.clinicappointment.repository;

import com.clinicappointment.model.Appointment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndAppointmentTime(Long doctorId, LocalDateTime appointmentTime);

    @EntityGraph(attributePaths = {"doctor", "doctor.department"})
    List<Appointment> findByPatientUsernameOrderByAppointmentTimeDesc(String username);

    Optional<Appointment> findByIdAndPatientUsername(Long id, String username);

    @EntityGraph(attributePaths = {"patient", "doctor", "doctor.department"})
    Page<Appointment> findAllBy(Pageable pageable);
}
