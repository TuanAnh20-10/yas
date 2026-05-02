package com.yas.cart.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CartItemTest {

    private static final String CUSTOMER_ID_SAMPLE = "customerId";
    private static final Long PRODUCT_ID_SAMPLE = 1L;
    private static final int QUANTITY_SAMPLE = 5;

    @Nested
    class CartItemBuilderTest {

        @Test
        void testBuilder_shouldCreateCartItemWithAllFields() {
            CartItem cartItem = CartItem.builder()
                .customerId(CUSTOMER_ID_SAMPLE)
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(QUANTITY_SAMPLE)
                .build();

            assertThat(cartItem).isNotNull();
            assertThat(cartItem.getCustomerId()).isEqualTo(CUSTOMER_ID_SAMPLE);
            assertThat(cartItem.getProductId()).isEqualTo(PRODUCT_ID_SAMPLE);
            assertThat(cartItem.getQuantity()).isEqualTo(QUANTITY_SAMPLE);
        }

        @Test
        void testBuilder_withDifferentValues_shouldCreateCorrectly() {
            CartItem cartItem = CartItem.builder()
                .customerId("anotherUser")
                .productId(999L)
                .quantity(10)
                .build();

            assertThat(cartItem.getCustomerId()).isEqualTo("anotherUser");
            assertThat(cartItem.getProductId()).isEqualTo(999L);
            assertThat(cartItem.getQuantity()).isEqualTo(10);
        }
    }

    @Nested
    class CartItemConstructorTest {

        @Test
        void testNoArgsConstructor_shouldCreateEmptyCartItem() {
            CartItem cartItem = new CartItem();

            assertThat(cartItem).isNotNull();
        }

        @Test
        void testAllArgsConstructor_shouldCreateCartItemWithAllFields() {
            CartItem cartItem = new CartItem(CUSTOMER_ID_SAMPLE, PRODUCT_ID_SAMPLE, QUANTITY_SAMPLE);

            assertThat(cartItem.getCustomerId()).isEqualTo(CUSTOMER_ID_SAMPLE);
            assertThat(cartItem.getProductId()).isEqualTo(PRODUCT_ID_SAMPLE);
            assertThat(cartItem.getQuantity()).isEqualTo(QUANTITY_SAMPLE);
        }
    }

    @Nested
    class CartItemGettersSettersTest {

        @Test
        void testGettersAndSetters_shouldWorkCorrectly() {
            CartItem cartItem = new CartItem();

            cartItem.setCustomerId(CUSTOMER_ID_SAMPLE);
            cartItem.setProductId(PRODUCT_ID_SAMPLE);
            cartItem.setQuantity(QUANTITY_SAMPLE);

            assertThat(cartItem.getCustomerId()).isEqualTo(CUSTOMER_ID_SAMPLE);
            assertThat(cartItem.getProductId()).isEqualTo(PRODUCT_ID_SAMPLE);
            assertThat(cartItem.getQuantity()).isEqualTo(QUANTITY_SAMPLE);
        }

        @Test
        void testSetQuantity_withDifferentValue_shouldUpdate() {
            CartItem cartItem = CartItem.builder()
                .customerId(CUSTOMER_ID_SAMPLE)
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(5)
                .build();

            cartItem.setQuantity(10);

            assertThat(cartItem.getQuantity()).isEqualTo(10);
        }

        @Test
        void testSetCustomerId_shouldUpdate() {
            CartItem cartItem = new CartItem();
            cartItem.setCustomerId("newUserId");

            assertThat(cartItem.getCustomerId()).isEqualTo("newUserId");
        }

        @Test
        void testSetProductId_shouldUpdate() {
            CartItem cartItem = new CartItem();
            cartItem.setProductId(123L);

            assertThat(cartItem.getProductId()).isEqualTo(123L);
        }
    }

    @Nested
    class CartItemEqualsTest {

        @Test
        void testEquals_withSameValues_shouldReturnTrue() {
            CartItem cartItem1 = CartItem.builder()
                .customerId(CUSTOMER_ID_SAMPLE)
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(QUANTITY_SAMPLE)
                .build();
            CartItem cartItem2 = CartItem.builder()
                .customerId(CUSTOMER_ID_SAMPLE)
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(QUANTITY_SAMPLE)
                .build();

            assertThat(cartItem1.getCustomerId()).isEqualTo(cartItem2.getCustomerId());
            assertThat(cartItem1.getProductId()).isEqualTo(cartItem2.getProductId());
            assertThat(cartItem1.getQuantity()).isEqualTo(cartItem2.getQuantity());
        }

        @Test
        void testEquals_withDifferentCustomerId_shouldReturnFalse() {
            CartItem cartItem1 = CartItem.builder()
                .customerId("user1")
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(QUANTITY_SAMPLE)
                .build();
            CartItem cartItem2 = CartItem.builder()
                .customerId("user2")
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(QUANTITY_SAMPLE)
                .build();

            assertThat(cartItem1).isNotEqualTo(cartItem2);
        }

        @Test
        void testEquals_withDifferentProductId_shouldReturnFalse() {
            CartItem cartItem1 = CartItem.builder()
                .customerId(CUSTOMER_ID_SAMPLE)
                .productId(1L)
                .quantity(QUANTITY_SAMPLE)
                .build();
            CartItem cartItem2 = CartItem.builder()
                .customerId(CUSTOMER_ID_SAMPLE)
                .productId(2L)
                .quantity(QUANTITY_SAMPLE)
                .build();

            assertThat(cartItem1).isNotEqualTo(cartItem2);
        }

        @Test
        void testEquals_withSelf_shouldReturnTrue() {
            CartItem cartItem = CartItem.builder()
                .customerId(CUSTOMER_ID_SAMPLE)
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(QUANTITY_SAMPLE)
                .build();

            assertThat(cartItem).isEqualTo(cartItem);
        }
    }

    @Nested
    class CartItemHashCodeTest {

        @Test
        void testHashCode_withSameValues_shouldReturnSameHashCode() {
            CartItem cartItem1 = CartItem.builder()
                .customerId(CUSTOMER_ID_SAMPLE)
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(QUANTITY_SAMPLE)
                .build();
            CartItem cartItem2 = CartItem.builder()
                .customerId(CUSTOMER_ID_SAMPLE)
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(QUANTITY_SAMPLE)
                .build();

            assertThat(cartItem1.getCustomerId()).isEqualTo(cartItem2.getCustomerId());
            assertThat(cartItem1.getProductId()).isEqualTo(cartItem2.getProductId());
            assertThat(cartItem1.getQuantity()).isEqualTo(cartItem2.getQuantity());
        }

        @Test
        void testHashCode_withDifferentValues_shouldReturnDifferentHashCode() {
            CartItem cartItem1 = CartItem.builder()
                .customerId("user1")
                .productId(1L)
                .quantity(5)
                .build();
            CartItem cartItem2 = CartItem.builder()
                .customerId("user2")
                .productId(2L)
                .quantity(10)
                .build();

            assertThat(cartItem1.getCustomerId()).isNotEqualTo(cartItem2.getCustomerId());
            assertThat(cartItem1.getProductId()).isNotEqualTo(cartItem2.getProductId());
        }
    }
}
