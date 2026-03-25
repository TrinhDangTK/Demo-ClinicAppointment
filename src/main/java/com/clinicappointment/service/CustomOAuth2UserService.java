package com.clinicappointment.service;

import com.clinicappointment.model.Patient;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomOAuth2UserService {

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
    private final AuthService authService;

    public CustomOAuth2UserService(AuthService authService) {
        this.authService = authService;
    }

    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        String email = oauth2User.getAttribute("email");
        String sub = oauth2User.getAttribute("sub");
        if (email == null || email.isBlank()) {
            OAuth2Error error = new OAuth2Error("invalid_user", "email không hợp lệ", null);
            throw new OAuth2AuthenticationException(error);
        }
        Patient patient = authService.loadOrRegisterOAuthPatient(email.trim(), sub != null ? sub : "");

        var authorities = authService.buildAuthoritiesForPatient(patient.getId());

        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
        attributes.put("username", patient.getUsername());

        return new DefaultOAuth2User(authorities, attributes, "username");
    }
}
