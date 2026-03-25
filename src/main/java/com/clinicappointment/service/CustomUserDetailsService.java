package com.clinicappointment.service;

import com.clinicappointment.model.Patient;
import com.clinicappointment.repository.PatientRepository;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PatientRepository patientRepository;

    public CustomUserDetailsService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Patient patient = patientRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + username));

        return User.withUsername(patient.getUsername())
            .password(patient.getPasswordHash())
            .authorities(
                patient.getPatientRoles().stream()
                    .map(patientRole -> patientRole.getRole().getName())
                    .map(roleName -> roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet())
            )
            .build();
    }
}
