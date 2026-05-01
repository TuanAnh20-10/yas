package com.yas.media.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SecurityConfigTest {
    @Test
    void jwtAuthenticationConverterForKeycloak_shouldMapRolesToAuthorities() {
        SecurityConfig config = new SecurityConfig();
        var converter = config.jwtAuthenticationConverterForKeycloak();


        var claims = Map.<String, Object>of("realm_access", Map.of("roles", List.of("ADMIN", "USER")));
        var headers = Map.<String, Object>of("alg", "none");
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), headers, claims);

        var auth = converter.convert(jwt);
        assertNotNull(auth);
        var authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        assertTrue(authorities.contains("ROLE_ADMIN"));
        assertTrue(authorities.contains("ROLE_USER"));
    }

    @Test
    void jwtAuthenticationConverterForKeycloak_shouldFailWhenRealmAccessMissing() {
        SecurityConfig config = new SecurityConfig();
        var converter = config.jwtAuthenticationConverterForKeycloak();

        var claims = Map.<String, Object>of("sub", "user");
        var headers = Map.<String, Object>of("alg", "none");
        Jwt jwt = new Jwt("token", Instant.now(), Instant.now().plusSeconds(3600), headers, claims);

        assertThrows(NullPointerException.class, () -> converter.convert(jwt));
    }

    @Test
    void filterChain_shouldConfigureExpectedSecurityRules() throws Exception {
        SecurityConfig config = new SecurityConfig();
        org.springframework.security.config.annotation.web.builders.HttpSecurity httpSecurity =
            mock(org.springframework.security.config.annotation.web.builders.HttpSecurity.class);
        DefaultSecurityFilterChain expectedChain = mock(DefaultSecurityFilterChain.class);

        AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry registry =
            mock(AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry.class,
                org.mockito.Mockito.RETURNS_DEEP_STUBS);
        OAuth2ResourceServerConfigurer<org.springframework.security.config.annotation.web.builders.HttpSecurity> oauth2Configurer =
            mock(OAuth2ResourceServerConfigurer.class, org.mockito.Mockito.RETURNS_DEEP_STUBS);

        when(httpSecurity.authorizeHttpRequests(any())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Customizer<AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry> customizer =
                invocation.getArgument(0);
            customizer.customize(registry);
            return httpSecurity;
        });
        when(httpSecurity.oauth2ResourceServer(any())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Customizer<OAuth2ResourceServerConfigurer<org.springframework.security.config.annotation.web.builders.HttpSecurity>> customizer =
                invocation.getArgument(0);
            customizer.customize(oauth2Configurer);
            return httpSecurity;
        });
        when(httpSecurity.build()).thenReturn(expectedChain);

        SecurityFilterChain actualChain = config.filterChain(httpSecurity);

        assertSame(expectedChain, actualChain);
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).oauth2ResourceServer(any());
        verify(httpSecurity).build();
    }
}
