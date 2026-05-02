package com.yas.cart.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class DatabaseAutoConfigTest {

    private DatabaseAutoConfig databaseAutoConfig;

    @BeforeEach
    void setUp() {
        databaseAutoConfig = new DatabaseAutoConfig();
    }

    @Nested
    class AuditorAwareBeanTest {

        @Test
        void testAuditorAwareBean_shouldCreateBean() {
            AuditorAware<String> auditorAware = databaseAutoConfig.auditorAware();

            assertThat(auditorAware).isNotNull();
        }

        @Test
        void testAuditorAware_withAuthentication_shouldReturnUsername() {
            // Setup authentication
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "testUser", "password"
            );
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(securityContext);

            AuditorAware<String> auditorAware = databaseAutoConfig.auditorAware();
            Optional<String> currentAuditor = auditorAware.getCurrentAuditor();

            assertThat(currentAuditor).isPresent();
            assertThat(currentAuditor.get()).isEqualTo("testUser");
        }

        @Test
        void testAuditorAware_withDifferentUsername_shouldReturnCorrectUsername() {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "admin_user", "password"
            );
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(securityContext);

            AuditorAware<String> auditorAware = databaseAutoConfig.auditorAware();
            Optional<String> currentAuditor = auditorAware.getCurrentAuditor();

            assertThat(currentAuditor).isPresent();
            assertThat(currentAuditor.get()).isEqualTo("admin_user");
        }

        @Test
        void testAuditorAware_withNullAuthentication_shouldReturnEmptyString() {
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(null);
            SecurityContextHolder.setContext(securityContext);

            AuditorAware<String> auditorAware = databaseAutoConfig.auditorAware();
            Optional<String> currentAuditor = auditorAware.getCurrentAuditor();

            assertThat(currentAuditor).isPresent();
            assertThat(currentAuditor.get()).isEmpty();
        }

        @Test
        void testAuditorAware_withValidAuthentication_shouldReturnOptional() {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "user123", "password"
            );
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(securityContext);

            AuditorAware<String> auditorAware = databaseAutoConfig.auditorAware();
            Optional<String> currentAuditor = auditorAware.getCurrentAuditor();

            assertThat(currentAuditor).isNotEmpty();
            assertThat(currentAuditor).isInstanceOf(Optional.class);
        }

        @Test
        void testAuditorAware_withMultipleAuthentications_shouldReturnCorrectUser() {
            // First user
            UsernamePasswordAuthenticationToken auth1 = new UsernamePasswordAuthenticationToken(
                "user1", "password"
            );
            SecurityContext securityContext1 = mock(SecurityContext.class);
            when(securityContext1.getAuthentication()).thenReturn(auth1);
            SecurityContextHolder.setContext(securityContext1);

            AuditorAware<String> auditorAware = databaseAutoConfig.auditorAware();
            Optional<String> auditor1 = auditorAware.getCurrentAuditor();

            // Second user
            UsernamePasswordAuthenticationToken auth2 = new UsernamePasswordAuthenticationToken(
                "user2", "password"
            );
            SecurityContext securityContext2 = mock(SecurityContext.class);
            when(securityContext2.getAuthentication()).thenReturn(auth2);
            SecurityContextHolder.setContext(securityContext2);

            Optional<String> auditor2 = auditorAware.getCurrentAuditor();

            assertThat(auditor1.get()).isEqualTo("user1");
            assertThat(auditor2.get()).isEqualTo("user2");
        }

        @Test
        void testAuditorAware_withSpecialCharactersInUsername_shouldReturnUsername() {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "user@example.com", "password"
            );
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(securityContext);

            AuditorAware<String> auditorAware = databaseAutoConfig.auditorAware();
            Optional<String> currentAuditor = auditorAware.getCurrentAuditor();

            assertThat(currentAuditor).isPresent();
            assertThat(currentAuditor.get()).isEqualTo("user@example.com");
        }

        @Test
        void testAuditorAware_shouldReturnSameInstanceOnMultipleCalls() {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "testUser", "password"
            );
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(securityContext);

            AuditorAware<String> auditorAware = databaseAutoConfig.auditorAware();
            Optional<String> auditor1 = auditorAware.getCurrentAuditor();
            Optional<String> auditor2 = auditorAware.getCurrentAuditor();

            assertThat(auditor1.get()).isEqualTo(auditor2.get());
        }

        @Test
        void testAuditorAware_isAuditorAwareType() {
            AuditorAware<String> auditorAware = databaseAutoConfig.auditorAware();

            assertThat(auditorAware).isInstanceOf(AuditorAware.class);
        }
    }

    @Nested
    class DatabaseAutoConfigClassTest {

        @Test
        void testConfigurationClass_shouldBeInstantiable() {
            assertThat(databaseAutoConfig).isNotNull();
        }

        @Test
        void testAuditorAwareBeanCreation() {
            AuditorAware<String> auditorAware = databaseAutoConfig.auditorAware();

            assertThat(auditorAware).isNotNull();
        }

        @Test
        void testMultipleAuditorAwareBeans_shouldBeIndependent() {
            AuditorAware<String> bean1 = databaseAutoConfig.auditorAware();
            AuditorAware<String> bean2 = databaseAutoConfig.auditorAware();

            assertThat(bean1).isNotNull();
            assertThat(bean2).isNotNull();
            assertThat(bean1.getClass()).isEqualTo(bean2.getClass());
        }
    }

    @Nested
    class AuditorAwareIntegrationTest {

        @Test
        void testAuditorAwareWithAuthenticationName_shouldUseNameProperty() {
            // Create a custom Authentication implementation
            Authentication auth = mock(Authentication.class);
            when(auth.getName()).thenReturn("customUser");

            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(auth);
            SecurityContextHolder.setContext(securityContext);

            AuditorAware<String> auditorAware = databaseAutoConfig.auditorAware();
            Optional<String> currentAuditor = auditorAware.getCurrentAuditor();

            assertThat(currentAuditor).isPresent();
            assertThat(currentAuditor.get()).isEqualTo("customUser");
        }

        @Test
        void testAuditorAwareFlow_shouldFollowSecurityContext() {
            // Create first authentication
            UsernamePasswordAuthenticationToken auth1 = new UsernamePasswordAuthenticationToken(
                "user_a", "pass"
            );
            SecurityContext context1 = mock(SecurityContext.class);
            when(context1.getAuthentication()).thenReturn(auth1);
            SecurityContextHolder.setContext(context1);

            AuditorAware<String> auditorAware = databaseAutoConfig.auditorAware();

            Optional<String> auditor1 = auditorAware.getCurrentAuditor();
            assertThat(auditor1.get()).isEqualTo("user_a");

            // Switch to second authentication
            UsernamePasswordAuthenticationToken auth2 = new UsernamePasswordAuthenticationToken(
                "user_b", "pass"
            );
            SecurityContext context2 = mock(SecurityContext.class);
            when(context2.getAuthentication()).thenReturn(auth2);
            SecurityContextHolder.setContext(context2);

            Optional<String> auditor2 = auditorAware.getCurrentAuditor();
            assertThat(auditor2.get()).isEqualTo("user_b");
        }
    }
}
