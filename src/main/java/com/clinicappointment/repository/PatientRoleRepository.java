package com.clinicappointment.repository;

import com.clinicappointment.model.PatientRole;
import com.clinicappointment.model.PatientRoleId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRoleRepository extends JpaRepository<PatientRole, PatientRoleId> {

    List<PatientRole> findByPatient_Id(Long patientId);
}
