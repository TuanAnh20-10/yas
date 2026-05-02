package com.yas.cart.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CartItemDeleteVmTest {

    private static final Long PRODUCT_ID_SAMPLE = 1L;
    private static final int QUANTITY_SAMPLE = 5;

    @Nested
    class CartItemDeleteVmConstructorTest {

        @Test
        void testConstructor_shouldCreateCartItemDeleteVm() {
            CartItemDeleteVm cartItemDeleteVm = new CartItemDeleteVm(PRODUCT_ID_SAMPLE, QUANTITY_SAMPLE);

            assertThat(cartItemDeleteVm).isNotNull();
            assertThat(cartItemDeleteVm.productId()).isEqualTo(PRODUCT_ID_SAMPLE);
            assertThat(cartItemDeleteVm.quantity()).isEqualTo(QUANTITY_SAMPLE);
        }

        @Test
        void testConstructor_withDifferentValues_shouldCreateCorrectly() {
            CartItemDeleteVm cartItemDeleteVm = new CartItemDeleteVm(999L, 100);

            assertThat(cartItemDeleteVm.productId()).isEqualTo(999L);
            assertThat(cartItemDeleteVm.quantity()).isEqualTo(100);
        }

        @Test
        void testConstructor_withQuantityOne_shouldCreateCorrectly() {
            CartItemDeleteVm cartItemDeleteVm = new CartItemDeleteVm(PRODUCT_ID_SAMPLE, 1);

            assertThat(cartItemDeleteVm.quantity()).isEqualTo(1);
        }

        @Test
        void testConstructor_withDifferentProductId_shouldCreateCorrectly() {
            CartItemDeleteVm cartItemDeleteVm = new CartItemDeleteVm(777L, QUANTITY_SAMPLE);

            assertThat(cartItemDeleteVm.productId()).isEqualTo(777L);
        }
    }

    @Nested
    class CartItemDeleteVmRecordTest {

        @Test
        void testRecord_shouldAccessPropertiesCorrectly() {
            CartItemDeleteVm cartItemDeleteVm = new CartItemDeleteVm(PRODUCT_ID_SAMPLE, QUANTITY_SAMPLE);

            assertThat(cartItemDeleteVm.productId()).isEqualTo(PRODUCT_ID_SAMPLE);
            assertThat(cartItemDeleteVm.quantity()).isEqualTo(QUANTITY_SAMPLE);
        }

        @Test
        void testRecord_withMultipleInstances_shouldMaintainIndependence() {
            CartItemDeleteVm vm1 = new CartItemDeleteVm(1L, 5);
            CartItemDeleteVm vm2 = new CartItemDeleteVm(2L, 10);

            assertThat(vm1.productId()).isEqualTo(1L);
            assertThat(vm2.productId()).isEqualTo(2L);
            assertThat(vm1.quantity()).isEqualTo(5);
            assertThat(vm2.quantity()).isEqualTo(10);
        }

        @Test
        void testRecord_shouldPreserveValues() {
            CartItemDeleteVm cartItemDeleteVm = new CartItemDeleteVm(123L, 456);

            assertThat(cartItemDeleteVm.productId()).isEqualTo(123L);
            assertThat(cartItemDeleteVm.quantity()).isEqualTo(456);
        }
    }

    @Nested
    class CartItemDeleteVmEqualsAndHashTest {

        @Test
        void testEquals_withSameValues_shouldReturnTrue() {
            CartItemDeleteVm vm1 = new CartItemDeleteVm(PRODUCT_ID_SAMPLE, QUANTITY_SAMPLE);
            CartItemDeleteVm vm2 = new CartItemDeleteVm(PRODUCT_ID_SAMPLE, QUANTITY_SAMPLE);

            assertThat(vm1).isEqualTo(vm2);
        }

        @Test
        void testEquals_withDifferentProductId_shouldReturnFalse() {
            CartItemDeleteVm vm1 = new CartItemDeleteVm(1L, QUANTITY_SAMPLE);
            CartItemDeleteVm vm2 = new CartItemDeleteVm(2L, QUANTITY_SAMPLE);

            assertThat(vm1).isNotEqualTo(vm2);
        }

        @Test
        void testEquals_withDifferentQuantity_shouldReturnFalse() {
            CartItemDeleteVm vm1 = new CartItemDeleteVm(PRODUCT_ID_SAMPLE, 5);
            CartItemDeleteVm vm2 = new CartItemDeleteVm(PRODUCT_ID_SAMPLE, 10);

            assertThat(vm1).isNotEqualTo(vm2);
        }

        @Test
        void testHashCode_withSameValues_shouldReturnSameHashCode() {
            CartItemDeleteVm vm1 = new CartItemDeleteVm(PRODUCT_ID_SAMPLE, QUANTITY_SAMPLE);
            CartItemDeleteVm vm2 = new CartItemDeleteVm(PRODUCT_ID_SAMPLE, QUANTITY_SAMPLE);

            assertThat(vm1.hashCode()).isEqualTo(vm2.hashCode());
        }
    }
}
