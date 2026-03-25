package com.clinicappointment.repository;

import com.clinicappointment.model.Patient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUsername(String username);

    @Query(
        "SELECT DISTINCT p FROM Patient p "
            + "LEFT JOIN FETCH p.patientRoles pr "
            + "LEFT JOIN FETCH pr.role "
            + "WHERE LOWER(p.email) = LOWER(:email)"
    )
    Optional<Patient> findByEmailWithRoles(@Param("email") String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
