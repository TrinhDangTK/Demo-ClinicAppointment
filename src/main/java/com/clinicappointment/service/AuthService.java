package com.clinicappointment.service;

import com.clinicappointment.dto.RegisterForm;
import com.clinicappointment.model.Patient;
import com.clinicappointment.model.PatientRole;
import com.clinicappointment.model.PatientRoleId;
import com.clinicappointment.model.Role;
import com.clinicappointment.repository.PatientRepository;
import com.clinicappointment.repository.PatientRoleRepository;
import com.clinicappointment.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final PatientRepository patientRepository;
    private final RoleRepository roleRepository;
    private final PatientRoleRepository patientRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
        PatientRepository patientRepository,
        RoleRepository roleRepository,
        PatientRoleRepository patientRoleRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.patientRepository = patientRepository;
        this.roleRepository = roleRepository;
        this.patientRoleRepository = patientRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerPatient(RegisterForm form) {
        if (patientRepository.existsByUsername(form.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        if (patientRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        Patient patient = Patient.builder()
            .username(form.getUsername())
            .passwordHash(passwordEncoder.encode(form.getPassword()))
            .email(form.getEmail())
            .build();
        Patient savedPatient = patientRepository.save(patient);

        Role patientRole = roleRepository.findByName("PATIENT")
            .orElseThrow(() -> new EntityNotFoundException("Chưa có role PATIENT trong bảng roles"));

        PatientRole relation = PatientRole.builder()
            .id(new PatientRoleId(savedPatient.getId(), patientRole.getId()))
            .patient(savedPatient)
            .role(patientRole)
            .build();
        patientRoleRepository.save(relation);
    }

    @Transactional
    public Patient loadOrRegisterOAuthPatient(String email, String oidcSub) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Google không trả về email");
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        return patientRepository.findByEmailWithRoles(normalized)
            .orElseGet(() -> registerOAuthPatientInternal(normalized, oidcSub != null ? oidcSub : ""));
    }

    @Transactional
    public Set<GrantedAuthority> buildAuthoritiesForPatient(Long patientId) {
        ensurePatientRoleIfMissing(patientId);
        return patientRoleRepository.findByPatient_Id(patientId).stream()
            .map(pr -> pr.getRole().getName())
            .map(roleName -> roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
    }

    private void ensurePatientRoleIfMissing(Long patientId) {
        if (!patientRoleRepository.findByPatient_Id(patientId).isEmpty()) {
            return;
        }
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy id bệnh nhân " + patientId));
        Role patientRole = roleRepository.findByName("PATIENT")
            .orElseThrow(() -> new EntityNotFoundException("Chưa có role PATIENT trong bảng roles"));
        PatientRole relation = PatientRole.builder()
            .id(new PatientRoleId(patientId, patientRole.getId()))
            .patient(patient)
            .role(patientRole)
            .build();
        patientRoleRepository.save(relation);
    }

    private Patient registerOAuthPatientInternal(String email, String oidcSub) {
        String username = generateUniqueOAuthUsername(email, oidcSub);
        Patient patient = Patient.builder()
            .username(username)
            .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
            .email(email)
            .build();
        Patient saved = patientRepository.save(patient);

        Role patientRole = roleRepository.findByName("PATIENT")
            .orElseThrow(() -> new EntityNotFoundException("Chưa có role PATIENT trong bảng roles"));

        PatientRole relation = PatientRole.builder()
            .id(new PatientRoleId(saved.getId(), patientRole.getId()))
            .patient(saved)
            .role(patientRole)
            .build();
        patientRoleRepository.save(relation);

        return patientRepository.findByEmailWithRoles(email)
            .orElseThrow(() -> new EntityNotFoundException("Không tải lại được tài khoản bệnh nhân"));
    }

    private String generateUniqueOAuthUsername(String email, String sub) {
        if (email.length() <= 50 && !patientRepository.existsByUsername(email)) {
            return email;
        }
        String base = "g_" + (!sub.isEmpty() ? sub : "oauth");
        if (base.length() > 50) {
            base = base.substring(0, 50);
        }
        String candidate = base;
        int n = 0;
        while (patientRepository.existsByUsername(candidate)) {
            String suffix = "_" + n++;
            candidate = base.length() + suffix.length() <= 50
                ? base + suffix
                : base.substring(0, Math.max(1, 50 - suffix.length())) + suffix;
        }
        return candidate;
    }
}
