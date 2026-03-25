package com.clinicappointment.repository;
import com.clinicappointment.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor,Long>{
    Page<Doctor> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
