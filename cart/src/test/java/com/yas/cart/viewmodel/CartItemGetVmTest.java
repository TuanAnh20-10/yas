package com.yas.cart.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CartItemGetVmTest {

    private static final String CUSTOMER_ID_SAMPLE = "customerId";
    private static final Long PRODUCT_ID_SAMPLE = 1L;
    private static final int QUANTITY_SAMPLE = 5;

    @Nested
    class CartItemGetVmBuilderTest {

        @Test
        void testBuilder_shouldCreateCartItemGetVm() {
            CartItemGetVm cartItemGetVm = CartItemGetVm.builder()
                .customerId(CUSTOMER_ID_SAMPLE)
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(QUANTITY_SAMPLE)
                .build();

            assertThat(cartItemGetVm).isNotNull();
            assertThat(cartItemGetVm.customerId()).isEqualTo(CUSTOMER_ID_SAMPLE);
            assertThat(cartItemGetVm.productId()).isEqualTo(PRODUCT_ID_SAMPLE);
            assertThat(cartItemGetVm.quantity()).isEqualTo(QUANTITY_SAMPLE);
        }

        @Test
        void testBuilder_withDifferentValues_shouldCreateCorrectly() {
            CartItemGetVm cartItemGetVm = CartItemGetVm.builder()
                .customerId("anotherUser")
                .productId(999L)
                .quantity(100)
                .build();

            assertThat(cartItemGetVm.customerId()).isEqualTo("anotherUser");
            assertThat(cartItemGetVm.productId()).isEqualTo(999L);
            assertThat(cartItemGetVm.quantity()).isEqualTo(100);
        }

        @Test
        void testBuilder_withQuantityOne_shouldCreateCorrectly() {
            CartItemGetVm cartItemGetVm = CartItemGetVm.builder()
                .customerId(CUSTOMER_ID_SAMPLE)
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(1)
                .build();

            assertThat(cartItemGetVm.quantity()).isEqualTo(1);
        }
    }

    @Nested
    class CartItemGetVmRecordTest {

        @Test
        void testRecord_shouldAccessPropertiesCorrectly() {
            CartItemGetVm cartItemGetVm = CartItemGetVm.builder()
                .customerId(CUSTOMER_ID_SAMPLE)
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(QUANTITY_SAMPLE)
                .build();

            assertThat(cartItemGetVm.customerId()).isEqualTo(CUSTOMER_ID_SAMPLE);
            assertThat(cartItemGetVm.productId()).isEqualTo(PRODUCT_ID_SAMPLE);
            assertThat(cartItemGetVm.quantity()).isEqualTo(QUANTITY_SAMPLE);
        }

        @Test
        void testRecord_withMultipleInstances_shouldMaintainIndependence() {
            CartItemGetVm vm1 = CartItemGetVm.builder()
                .customerId("user1")
                .productId(1L)
                .quantity(5)
                .build();
            CartItemGetVm vm2 = CartItemGetVm.builder()
                .customerId("user2")
                .productId(2L)
                .quantity(10)
                .build();

            assertThat(vm1.customerId()).isEqualTo("user1");
            assertThat(vm2.customerId()).isEqualTo("user2");
        }
    }
}
