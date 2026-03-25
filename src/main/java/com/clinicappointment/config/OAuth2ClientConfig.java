package com.clinicappointment.config;

import com.clinicappointment.service.CustomOAuth2UserService;
import com.clinicappointment.service.CustomOidcUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Configuration
public class OAuth2ClientConfig {

    @Bean
    OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(CustomOAuth2UserService customOAuth2UserService) {
        return customOAuth2UserService::loadUser;
    }

    @Bean
    OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService(CustomOidcUserService customOidcUserService) {
        return customOidcUserService::loadUser;
    }
}
