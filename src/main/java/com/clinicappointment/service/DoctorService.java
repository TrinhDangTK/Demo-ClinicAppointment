package com.clinicappointment.service;

import com.clinicappointment.dto.DoctorCardDto;
import com.clinicappointment.dto.DepartmentForm;
import com.clinicappointment.dto.DoctorForm;
import com.clinicappointment.model.Department;
import com.clinicappointment.model.Doctor;
import com.clinicappointment.repository.DepartmentRepository;
import com.clinicappointment.repository.DoctorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;

    public DoctorService(DoctorRepository doctorRepository, DepartmentRepository departmentRepository) {
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional(readOnly = true)
    public Page<DoctorCardDto> getDoctorCards(Pageable pageable) {
        return doctorRepository.findAll(pageable).map(this::toCardDto);
    }

    @Transactional(readOnly = true)
    public Page<DoctorCardDto> getDoctorCards(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return getDoctorCards(pageable);
        }
        return doctorRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable).map(this::toCardDto);
    }

    @Transactional(readOnly = true)
    public Page<Doctor> getDoctors(Pageable pageable) {
        return doctorRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bác sĩ với id " + id));
    }

    @Transactional
    public Doctor createDoctor(DoctorForm form) {
        Department department = getDepartmentById(form.getDepartmentId());
        Doctor doctor = Doctor.builder()
            .name(form.getName())
            .specialty(form.getSpecialty())
            .imageUrl(form.getImageUrl())
            .department(department)
            .build();
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor updateDoctor(Long id, DoctorForm form) {
        Doctor doctor = getDoctorById(id);
        Department department = getDepartmentById(form.getDepartmentId());
        doctor.setName(form.getName());
        doctor.setSpecialty(form.getSpecialty());
        doctor.setImageUrl(form.getImageUrl());
        doctor.setDepartment(department);
        return doctorRepository.save(doctor);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public DoctorForm buildDoctorForm(Doctor doctor) {
        DoctorForm form = new DoctorForm();
        form.setName(doctor.getName());
        form.setSpecialty(doctor.getSpecialty());
        form.setImageUrl(doctor.getImageUrl());
        form.setDepartmentId(doctor.getDepartment() != null ? doctor.getDepartment().getId() : null);
        return form;
    }

    @Transactional(readOnly = true)
    public java.util.List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Transactional
    public Department createDepartment(DepartmentForm form) {
        String normalizedName = form.getName() != null ? form.getName().trim() : "";
        if (departmentRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("Khoa da ton tai");
        }
        Department department = Department.builder()
            .name(normalizedName)
            .build();
        return departmentRepository.save(department);
    }

    private DoctorCardDto toCardDto(Doctor doctor) {
        return new DoctorCardDto(
            doctor.getId(),
            doctor.getName(),
            doctor.getSpecialty(),
            doctor.getDepartment() != null ? doctor.getDepartment().getName() : "",
            doctor.getImageUrl()
        );
    }

    private Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khoa với id " + id));
    }
}
