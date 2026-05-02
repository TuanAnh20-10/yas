package com.yas.cart.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
    }

    @Nested
    class JwtAuthenticationConverterForKeycloakTest {

        @Test
        void testJwtAuthenticationConverterForKeycloak_shouldCreateConverter() {
            JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverterForKeycloak();

            assertThat(converter).isNotNull();
        }

        @Test
        void testJwtAuthenticationConverterForKeycloak_withValidJwt_shouldExtractRoles() {
            Map<String, Object> claims = new HashMap<>();
            Map<String, Collection<String>> realmAccess = new HashMap<>();
            realmAccess.put("roles", List.of("admin", "user", "customer"));
            claims.put("realm_access", realmAccess);

            Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claims(c -> c.putAll(claims))
                .subject("user1")
                .build();

            JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverterForKeycloak();
            var authentication = converter.convert(jwt);

            assertThat(authentication).isNotNull();
            assertThat(authentication.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_admin", "ROLE_user", "ROLE_customer", "FACTOR_BEARER");
        }

        @Test
        void testJwtAuthenticationConverterForKeycloak_withSingleRole_shouldExtractRole() {
            Map<String, Object> claims = new HashMap<>();
            Map<String, Collection<String>> realmAccess = new HashMap<>();
            realmAccess.put("roles", List.of("admin"));
            claims.put("realm_access", realmAccess);

            Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claims(c -> c.putAll(claims))
                .subject("admin_user")
                .build();

            JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverterForKeycloak();
            var authentication = converter.convert(jwt);

            assertThat(authentication.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_admin", "FACTOR_BEARER");
        }

        @Test
        void testJwtAuthenticationConverterForKeycloak_withEmptyRoles_shouldReturnEmptyAuthorities() {
            Map<String, Object> claims = new HashMap<>();
            Map<String, Collection<String>> realmAccess = new HashMap<>();
            realmAccess.put("roles", List.of());
            claims.put("realm_access", realmAccess);

            Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claims(c -> c.putAll(claims))
                .subject("guest")
                .build();

            JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverterForKeycloak();
            var authentication = converter.convert(jwt);

            assertThat(authentication.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .containsExactly("FACTOR_BEARER");
        }

        @Test
        void testJwtAuthenticationConverterForKeycloak_converterIsConfiguredCorrectly() {
            JwtAuthenticationConverter converter1 = securityConfig.jwtAuthenticationConverterForKeycloak();
            JwtAuthenticationConverter converter2 = securityConfig.jwtAuthenticationConverterForKeycloak();

            assertThat(converter1).isNotNull();
            assertThat(converter2).isNotNull();
            assertThat(converter1).isNotSameAs(converter2);
        }
    }

    @Nested
    class SecurityConfigBeanTest {

        @Test
        void testSecurityConfigIsConfigurationClass() {
            assertThat(securityConfig).isNotNull();
            assertThat(securityConfig.getClass().getName()).contains("SecurityConfig");
        }

        @Test
        void testJwtAuthenticationConverterBeanCreation() {
            JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverterForKeycloak();

            assertThat(converter).isNotNull();
            assertThat(converter).isInstanceOf(JwtAuthenticationConverter.class);
        }

        @Test
        void testMultipleRolesExtraction_withCustomerRole() {
            Map<String, Object> claims = new HashMap<>();
            Map<String, Collection<String>> realmAccess = new HashMap<>();
            realmAccess.put("roles", List.of("customer", "user"));
            claims.put("realm_access", realmAccess);

            Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claims(c -> c.putAll(claims))
                .subject("customer_user")
                .build();

            JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverterForKeycloak();
            var authentication = converter.convert(jwt);

            assertThat(authentication.getAuthorities()).extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_customer", "ROLE_user", "FACTOR_BEARER");
        }

        @Test
        void testRolePrefix_shouldBeROLE_() {
            Map<String, Object> claims = new HashMap<>();
            Map<String, Collection<String>> realmAccess = new HashMap<>();
            realmAccess.put("roles", List.of("admin"));
            claims.put("realm_access", realmAccess);

            Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claims(c -> c.putAll(claims))
                .subject("admin")
                .build();

            JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverterForKeycloak();
            var authentication = converter.convert(jwt);

            assertThat(authentication.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_admin", "FACTOR_BEARER");
        }
    }
}
