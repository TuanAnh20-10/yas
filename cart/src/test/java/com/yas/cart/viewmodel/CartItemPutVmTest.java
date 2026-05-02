package com.yas.cart.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CartItemPutVmTest {

    @Nested
    class CartItemPutVmConstructorTest {

        @Test
        void testConstructor_shouldCreateCartItemPutVm() {
            CartItemPutVm cartItemPutVm = new CartItemPutVm(5);

            assertThat(cartItemPutVm).isNotNull();
            assertThat(cartItemPutVm.quantity()).isEqualTo(5);
        }

        @Test
        void testConstructor_withDifferentQuantity_shouldCreateCorrectly() {
            CartItemPutVm cartItemPutVm = new CartItemPutVm(100);

            assertThat(cartItemPutVm.quantity()).isEqualTo(100);
        }

        @Test
        void testConstructor_withQuantityOne_shouldCreateCorrectly() {
            CartItemPutVm cartItemPutVm = new CartItemPutVm(1);

            assertThat(cartItemPutVm.quantity()).isEqualTo(1);
        }

        @Test
        void testConstructor_withLargeQuantity_shouldCreateCorrectly() {
            CartItemPutVm cartItemPutVm = new CartItemPutVm(10000);

            assertThat(cartItemPutVm.quantity()).isEqualTo(10000);
        }
    }

    @Nested
    class CartItemPutVmRecordTest {

        @Test
        void testRecord_shouldAccessQuantityCorrectly() {
            CartItemPutVm cartItemPutVm = new CartItemPutVm(5);

            assertThat(cartItemPutVm.quantity()).isEqualTo(5);
        }

        @Test
        void testRecord_withMultipleInstances_shouldMaintainIndependence() {
            CartItemPutVm vm1 = new CartItemPutVm(5);
            CartItemPutVm vm2 = new CartItemPutVm(10);

            assertThat(vm1.quantity()).isEqualTo(5);
            assertThat(vm2.quantity()).isEqualTo(10);
        }
    }
}
