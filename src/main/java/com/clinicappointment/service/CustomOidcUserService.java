package com.clinicappointment.service;

import com.clinicappointment.model.Patient;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomOidcUserService {

    private final OidcUserService delegate = new OidcUserService();
    private final AuthService authService;

    public CustomOidcUserService(AuthService authService) {
        this.authService = authService;
    }

    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = delegate.loadUser(userRequest);
        String email = oidcUser.getEmail();
        String sub = oidcUser.getSubject();
        if (email == null || email.isBlank()) {
            OAuth2Error error = new OAuth2Error("invalid_user", "Can thiet email tu Google", null);
            throw new OAuth2AuthenticationException(error);
        }
        Patient patient = authService.loadOrRegisterOAuthPatient(email.trim(), sub != null ? sub : "");

        var authorities = authService.buildAuthoritiesForPatient(patient.getId());

        Map<String, Object> claims = new HashMap<>(oidcUser.getAttributes());
        claims.put("username", patient.getUsername());
        OidcUserInfo mergedUserInfo = new OidcUserInfo(claims);

        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), mergedUserInfo, "username");
    }
}
