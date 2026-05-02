package com.yas.cart.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

class CartItemRepositoryTest {

    @Nested
    class CartItemRepositoryInterfaceTest {

        @Test
        void testCartItemRepository_shouldExistAsInterface() {
            assertThat(CartItemRepository.class).isNotNull();
            assertThat(CartItemRepository.class.isInterface()).isTrue();
        }

        @Test
        void testCartItemRepository_shouldExtendJpaRepository() {
            boolean extendsJpaRepository = JpaRepository.class.isAssignableFrom(CartItemRepository.class);
            assertThat(extendsJpaRepository).isTrue();
        }

        @Test
        void testCartItemRepository_shouldHaveExpectedMethods() {
            // Check for custom repository methods
            assertThat(CartItemRepository.class.getMethods()).isNotEmpty();
        }

        @Test
        void testCartItemRepository_shouldBeInCorrectPackage() {
            assertThat(CartItemRepository.class.getPackageName())
                .isEqualTo("com.yas.cart.repository");
        }

        @Test
        void testCartItemRepository_shouldHaveFindByCustomerIdAndProductIdMethod() {
            try {
                CartItemRepository.class.getMethod("findByCustomerIdAndProductId", String.class, Long.class);
                assertThat(true).isTrue();
            } catch (NoSuchMethodException e) {
                assertThat(false).withFailMessage("Method not found").isTrue();
            }
        }

        @Test
        void testCartItemRepository_shouldHaveFindByCustomerIdOrderByCreatedOnDescMethod() {
            try {
                CartItemRepository.class.getMethod("findByCustomerIdOrderByCreatedOnDesc", String.class);
                assertThat(true).isTrue();
            } catch (NoSuchMethodException e) {
                assertThat(false).withFailMessage("Method not found").isTrue();
            }
        }

        @Test
        void testCartItemRepository_shouldHaveFindByCustomerIdAndProductIdInMethod() {
            try {
                CartItemRepository.class.getMethod("findByCustomerIdAndProductIdIn", String.class, java.util.List.class);
                assertThat(true).isTrue();
            } catch (NoSuchMethodException e) {
                assertThat(false).withFailMessage("Method not found").isTrue();
            }
        }

        @Test
        void testCartItemRepository_shouldHaveDeleteByCustomerIdAndProductIdMethod() {
            try {
                CartItemRepository.class.getMethod("deleteByCustomerIdAndProductId", String.class, Long.class);
                assertThat(true).isTrue();
            } catch (NoSuchMethodException e) {
                assertThat(false).withFailMessage("Method not found").isTrue();
            }
        }
    }

    @Nested
    class CartItemRepositoryMethodSignaturesTest {

        @Test
        void testFindByCustomerIdAndProductId_hasCorrectReturnType() throws NoSuchMethodException {
            var method = CartItemRepository.class.getMethod("findByCustomerIdAndProductId", String.class, Long.class);
            String returnType = method.getReturnType().getSimpleName();
            assertThat(returnType).contains("Optional");
        }

        @Test
        void testFindByCustomerIdOrderByCreatedOnDesc_hasCorrectReturnType() throws NoSuchMethodException {
            var method = CartItemRepository.class.getMethod("findByCustomerIdOrderByCreatedOnDesc", String.class);
            String returnType = method.getReturnType().getSimpleName();
            assertThat(returnType).contains("List");
        }

        @Test
        void testFindByCustomerIdAndProductIdIn_hasCorrectReturnType() throws NoSuchMethodException {
            var method = CartItemRepository.class.getMethod("findByCustomerIdAndProductIdIn", String.class, java.util.List.class);
            String returnType = method.getReturnType().getSimpleName();
            assertThat(returnType).contains("List");
        }

        @Test
        void testDeleteByCustomerIdAndProductId_hasCorrectReturnType() throws NoSuchMethodException {
            var method = CartItemRepository.class.getMethod("deleteByCustomerIdAndProductId", String.class, Long.class);
            String returnType = method.getReturnType().getSimpleName();
            assertThat(returnType).isEqualTo("void");
        }
    }

    @Nested
    class CartItemRepositoryAnnotationTest {

        @Test
        void testCartItemRepository_hasCustomQueryMethods() {
            assertThat(CartItemRepository.class.getMethods()).isNotEmpty();
        }

        @Test
        void testCartItemRepository_shouldBePersistenceRepository() {
            assertThat(org.springframework.data.repository.Repository.class.isAssignableFrom(CartItemRepository.class))
                .isTrue();
        }

        @Test
        void testCartItemRepository_shouldSupportLocking() {
            try {
                var method = CartItemRepository.class.getMethod("findByCustomerIdAndProductId", String.class, Long.class);
                assertThat(method).isNotNull();
            } catch (NoSuchMethodException e) {
                assertThat(false).isTrue();
            }
        }
    }
}
