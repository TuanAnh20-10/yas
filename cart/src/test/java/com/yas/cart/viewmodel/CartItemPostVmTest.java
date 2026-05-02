package com.yas.cart.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CartItemPostVmTest {

    @Nested
    class CartItemPostVmBuilderTest {

        @Test
        void testBuilder_shouldCreateCartItemPostVm() {
            CartItemPostVm cartItemPostVm = CartItemPostVm.builder()
                .productId(1L)
                .quantity(5)
                .build();

            assertThat(cartItemPostVm).isNotNull();
            assertThat(cartItemPostVm.productId()).isEqualTo(1L);
            assertThat(cartItemPostVm.quantity()).isEqualTo(5);
        }

        @Test
        void testBuilder_withDifferentValues_shouldCreateCorrectly() {
            CartItemPostVm cartItemPostVm = CartItemPostVm.builder()
                .productId(999L)
                .quantity(100)
                .build();

            assertThat(cartItemPostVm.productId()).isEqualTo(999L);
            assertThat(cartItemPostVm.quantity()).isEqualTo(100);
        }

        @Test
        void testBuilder_withQuantityOne_shouldCreateCorrectly() {
            CartItemPostVm cartItemPostVm = CartItemPostVm.builder()
                .productId(1L)
                .quantity(1)
                .build();

            assertThat(cartItemPostVm.quantity()).isEqualTo(1);
        }
    }

    @Nested
    class CartItemPostVmRecordTest {

        @Test
        void testRecord_shouldAccessPropertiesCorrectly() {
            CartItemPostVm cartItemPostVm = CartItemPostVm.builder()
                .productId(1L)
                .quantity(5)
                .build();

            assertThat(cartItemPostVm.productId()).isEqualTo(1L);
            assertThat(cartItemPostVm.quantity()).isEqualTo(5);
        }

        @Test
        void testRecord_withMultipleInstances_shouldMaintainIndependence() {
            CartItemPostVm vm1 = CartItemPostVm.builder()
                .productId(1L)
                .quantity(5)
                .build();
            CartItemPostVm vm2 = CartItemPostVm.builder()
                .productId(2L)
                .quantity(10)
                .build();

            assertThat(vm1.productId()).isEqualTo(1L);
            assertThat(vm2.productId()).isEqualTo(2L);
        }
    }
}
