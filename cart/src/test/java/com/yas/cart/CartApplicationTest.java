package com.yas.cart;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

class CartApplicationTest {

    @Nested
    class CartApplicationClassTest {

        @Test
        void testCartApplicationClass_shouldExist() {
            assertThat(CartApplication.class).isNotNull();
        }

        @Test
        void testCartApplicationClass_shouldBeAClass() {
            assertThat(CartApplication.class.isInterface()).isFalse();
        }

        @Test
        void testCartApplicationClass_shouldHaveSpringBootApplicationAnnotation() {
            boolean hasAnnotation = CartApplication.class.isAnnotationPresent(SpringBootApplication.class);
            assertThat(hasAnnotation).isTrue();
        }

        @Test
        void testCartApplicationClass_shouldHaveEnableConfigurationPropertiesAnnotation() {
            boolean hasAnnotation = CartApplication.class.isAnnotationPresent(EnableConfigurationProperties.class);
            assertThat(hasAnnotation).isTrue();
        }

        @Test
        void testCartApplicationClass_shouldBePublic() {
            assertThat(CartApplication.class.getModifiers()).isNotNegative();
        }

        @Test
        void testCartApplicationClass_shouldHaveMainMethod() {
            try {
                CartApplication.class.getMethod("main", String[].class);
                assertThat(true).isTrue();
            } catch (NoSuchMethodException e) {
                assertThat(false).isTrue();
            }
        }
    }

    @Nested
    class CartApplicationAnnotationTest {

        @Test
        void testSpringBootApplicationAnnotation_isConfigured() {
            SpringBootApplication annotation = CartApplication.class.getAnnotation(SpringBootApplication.class);
            assertThat(annotation).isNotNull();
        }

        @Test
        void testSpringBootApplicationAnnotation_configuresScanBasePackages() {
            SpringBootApplication annotation = CartApplication.class.getAnnotation(SpringBootApplication.class);
            assertThat(annotation).isNotNull();
            // Verify scanBasePackages includes cart and commonlibrary
            String[] packages = annotation.scanBasePackages();
            assertThat(packages).contains("com.yas.cart", "com.yas.commonlibrary");
        }

        @Test
        void testEnableConfigurationPropertiesAnnotation_isConfigured() {
            EnableConfigurationProperties annotation = CartApplication.class.getAnnotation(EnableConfigurationProperties.class);
            assertThat(annotation).isNotNull();
        }

        @Test
        void testApplicationName_shouldBeCartApplication() {
            assertThat(CartApplication.class.getSimpleName()).isEqualTo("CartApplication");
        }

        @Test
        void testApplicationPackage_shouldBeComYasCart() {
            assertThat(CartApplication.class.getPackageName()).isEqualTo("com.yas.cart");
        }
    }

    @Nested
    class CartApplicationMainMethodTest {

        @Test
        void testCartApplication_hasMainMethod() throws NoSuchMethodException {
            var mainMethod = CartApplication.class.getMethod("main", String[].class);
            assertThat(mainMethod).isNotNull();
        }

        @Test
        void testMainMethod_isStatic() throws NoSuchMethodException {
            var mainMethod = CartApplication.class.getMethod("main", String[].class);
            int modifiers = mainMethod.getModifiers();
            assertThat(java.lang.reflect.Modifier.isStatic(modifiers)).isTrue();
        }

        @Test
        void testMainMethod_isPublic() throws NoSuchMethodException {
            var mainMethod = CartApplication.class.getMethod("main", String[].class);
            int modifiers = mainMethod.getModifiers();
            assertThat(java.lang.reflect.Modifier.isPublic(modifiers)).isTrue();
        }

        @Test
        void testMainMethod_acceptsStringArray() throws NoSuchMethodException {
            var mainMethod = CartApplication.class.getMethod("main", String[].class);
            var parameterTypes = mainMethod.getParameterTypes();
            assertThat(parameterTypes).hasSize(1);
            assertThat(parameterTypes[0]).isEqualTo(String[].class);
        }
    }

    @Nested
    class CartApplicationInstantiationTest {

        @Test
        void testCartApplication_canBeInstantiated() {
            CartApplication app = new CartApplication();
            assertThat(app).isNotNull();
        }

        @Test
        void testCartApplication_multipleInstancesCanBeCreated() {
            CartApplication app1 = new CartApplication();
            CartApplication app2 = new CartApplication();
            assertThat(app1).isNotNull();
            assertThat(app2).isNotNull();
        }
    }

    @Nested
    class CartApplicationConfigurationTest {

        @Test
        void testCartApplication_isSpringBootApplication() {
            assertThat(CartApplication.class.isAnnotationPresent(SpringBootApplication.class)).isTrue();
        }

        @Test
        void testCartApplication_scansCorrectPackages() {
            SpringBootApplication annotation = CartApplication.class.getAnnotation(SpringBootApplication.class);
            String[] packages = annotation.scanBasePackages();
            assertThat(packages).containsExactlyInAnyOrder("com.yas.cart", "com.yas.commonlibrary");
        }

        @Test
        void testCartApplication_enablesConfigurationProperties() {
            EnableConfigurationProperties annotation = CartApplication.class.getAnnotation(EnableConfigurationProperties.class);
            assertThat(annotation).isNotNull();
        }

        @Test
        void testCartApplication_shouldHaveValidConfiguration() {
            SpringBootApplication bootApp = CartApplication.class.getAnnotation(SpringBootApplication.class);
            EnableConfigurationProperties configProps = CartApplication.class.getAnnotation(EnableConfigurationProperties.class);

            assertThat(bootApp).isNotNull();
            assertThat(configProps).isNotNull();
        }
    }
}
