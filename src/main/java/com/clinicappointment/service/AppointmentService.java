package com.clinicappointment.service;

import com.clinicappointment.dto.AppointmentCreateForm;
import com.clinicappointment.dto.AdminAppointmentForm;
import com.clinicappointment.dto.MyAppointmentDto;
import com.clinicappointment.model.Appointment;
import com.clinicappointment.model.AppointmentStatus;
import com.clinicappointment.model.Doctor;
import com.clinicappointment.model.Patient;
import com.clinicappointment.repository.AppointmentRepository;
import com.clinicappointment.repository.DoctorRepository;
import com.clinicappointment.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(
        AppointmentRepository appointmentRepository,
        DoctorRepository doctorRepository,
        PatientRepository patientRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public void createAppointment(String username, Long doctorId, AppointmentCreateForm form) {
        Patient patient = patientRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy patient: " + username));
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bác sĩ + id " + doctorId));

        var appointmentTime = form.getAppointmentTime();
        if (appointmentRepository.existsByDoctorIdAndAppointmentTime(doctorId, appointmentTime)) {
            throw new IllegalArgumentException("Khung giờ đã có lịch, vui lòng chọn giờ khác");
        }

        Appointment appointment = Appointment.builder()
            .patient(patient)
            .doctor(doctor)
            .appointmentTime(appointmentTime)
            .status(AppointmentStatus.PENDING)
            .build();
        appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    public List<MyAppointmentDto> getMyAppointments(String username) {
        return appointmentRepository.findByPatientUsernameOrderByAppointmentTimeDesc(username).stream()
            .map(a -> new MyAppointmentDto(
                a.getId(),
                a.getDoctor().getName(),
                a.getAppointmentTime(),
                a.getDoctor().getDepartment() != null ? a.getDoctor().getDepartment().getName() : ""
            ))
            .toList();
    }

    @Transactional(readOnly = true)
    public Page<Appointment> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAllBy(pageable);
    }

    @Transactional(readOnly = true)
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lịch khám với id " + id));
    }

    @Transactional(readOnly = true)
    public Appointment getMyAppointmentForPatient(String username, Long appointmentId) {
        return appointmentRepository.findByIdAndPatientUsername(appointmentId, username)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lịch khám"));
    }

    @Transactional
    public void createByAdmin(AdminAppointmentForm form) {
        Patient patient = patientRepository.findById(form.getPatientId())
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bệnh nhân"));
        Doctor doctor = doctorRepository.findById(form.getDoctorId())
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bác sĩ"));

        Appointment appointment = Appointment.builder()
            .patient(patient)
            .doctor(doctor)
            .appointmentTime(form.getAppointmentTime())
            .status(form.getStatus())
            .note(form.getNote())
            .build();
        appointmentRepository.save(appointment);
    }

    @Transactional
    public void updateByAdmin(Long id, AdminAppointmentForm form) {
        Appointment appointment = getAppointmentById(id);
        Patient patient = patientRepository.findById(form.getPatientId())
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bệnh nhân"));
        Doctor doctor = doctorRepository.findById(form.getDoctorId())
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bác sĩ"));

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentTime(form.getAppointmentTime());
        appointment.setStatus(form.getStatus());
        appointment.setNote(form.getNote());
        appointmentRepository.save(appointment);
    }

    @Transactional
    public void deleteByAdmin(Long id) {
        appointmentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public AdminAppointmentForm buildAdminForm(Appointment appointment) {
        AdminAppointmentForm form = new AdminAppointmentForm();
        form.setPatientId(appointment.getPatient() != null ? appointment.getPatient().getId() : null);
        form.setDoctorId(appointment.getDoctor() != null ? appointment.getDoctor().getId() : null);
        form.setAppointmentTime(
            appointment.getAppointmentTime() != null
                ? appointment.getAppointmentTime().withSecond(0).withNano(0)
                : null
        );
        form.setStatus(appointment.getStatus());
        form.setNote(appointment.getNote());
        return form;
    }

    @Transactional
    public void cancelMyAppointment(String username, Long appointmentId) {
        Appointment appointment = appointmentRepository.findByIdAndPatientUsername(appointmentId, username)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lịch khám để hủy"));
        appointmentRepository.delete(appointment);
    }
}
