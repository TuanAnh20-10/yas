package com.yas.cart.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CartItemIdTest {

    private static final String CUSTOMER_ID_SAMPLE = "customerId";
    private static final Long PRODUCT_ID_SAMPLE = 1L;

    @Nested
    class CartItemIdConstructorTest {

        @Test
        void testNoArgsConstructor_shouldCreateEmptyCartItemId() {
            CartItemId cartItemId = new CartItemId();

            assertThat(cartItemId).isNotNull();
        }

        @Test
        void testAllArgsConstructor_shouldCreateCartItemIdWithAllFields() {
            CartItemId cartItemId = new CartItemId(CUSTOMER_ID_SAMPLE, PRODUCT_ID_SAMPLE);

            assertThat(cartItemId.getCustomerId()).isEqualTo(CUSTOMER_ID_SAMPLE);
            assertThat(cartItemId.getProductId()).isEqualTo(PRODUCT_ID_SAMPLE);
        }

        @Test
        void testAllArgsConstructor_withDifferentValues_shouldCreateCorrectly() {
            CartItemId cartItemId = new CartItemId("anotherUser", 999L);

            assertThat(cartItemId.getCustomerId()).isEqualTo("anotherUser");
            assertThat(cartItemId.getProductId()).isEqualTo(999L);
        }
    }

    @Nested
    class CartItemIdGettersSettersTest {

        @Test
        void testGettersAndSetters_shouldWorkCorrectly() {
            CartItemId cartItemId = new CartItemId();

            cartItemId.setCustomerId(CUSTOMER_ID_SAMPLE);
            cartItemId.setProductId(PRODUCT_ID_SAMPLE);

            assertThat(cartItemId.getCustomerId()).isEqualTo(CUSTOMER_ID_SAMPLE);
            assertThat(cartItemId.getProductId()).isEqualTo(PRODUCT_ID_SAMPLE);
        }

        @Test
        void testSetCustomerId_shouldUpdate() {
            CartItemId cartItemId = new CartItemId();
            cartItemId.setCustomerId("newUserId");

            assertThat(cartItemId.getCustomerId()).isEqualTo("newUserId");
        }

        @Test
        void testSetProductId_shouldUpdate() {
            CartItemId cartItemId = new CartItemId();
            cartItemId.setProductId(123L);

            assertThat(cartItemId.getProductId()).isEqualTo(123L);
        }
    }

    @Nested
    class CartItemIdEqualsTest {

        @Test
        void testEquals_withSameValues_shouldReturnTrue() {
            CartItemId cartItemId1 = new CartItemId(CUSTOMER_ID_SAMPLE, PRODUCT_ID_SAMPLE);
            CartItemId cartItemId2 = new CartItemId(CUSTOMER_ID_SAMPLE, PRODUCT_ID_SAMPLE);

            assertThat(cartItemId1).isEqualTo(cartItemId2);
        }

        @Test
        void testEquals_withDifferentCustomerId_shouldReturnFalse() {
            CartItemId cartItemId1 = new CartItemId("user1", PRODUCT_ID_SAMPLE);
            CartItemId cartItemId2 = new CartItemId("user2", PRODUCT_ID_SAMPLE);

            assertThat(cartItemId1).isNotEqualTo(cartItemId2);
        }

        @Test
        void testEquals_withDifferentProductId_shouldReturnFalse() {
            CartItemId cartItemId1 = new CartItemId(CUSTOMER_ID_SAMPLE, 1L);
            CartItemId cartItemId2 = new CartItemId(CUSTOMER_ID_SAMPLE, 2L);

            assertThat(cartItemId1).isNotEqualTo(cartItemId2);
        }

        @Test
        void testEquals_withBothDifferent_shouldReturnFalse() {
            CartItemId cartItemId1 = new CartItemId("user1", 1L);
            CartItemId cartItemId2 = new CartItemId("user2", 2L);

            assertThat(cartItemId1).isNotEqualTo(cartItemId2);
        }

        @Test
        void testEquals_withSelf_shouldReturnTrue() {
            CartItemId cartItemId = new CartItemId(CUSTOMER_ID_SAMPLE, PRODUCT_ID_SAMPLE);

            assertThat(cartItemId).isEqualTo(cartItemId);
        }

        @Test
        void testEquals_withNull_shouldReturnFalse() {
            CartItemId cartItemId = new CartItemId(CUSTOMER_ID_SAMPLE, PRODUCT_ID_SAMPLE);

            assertThat(cartItemId).isNotEqualTo(null);
        }

        @Test
        void testEquals_withDifferentType_shouldReturnFalse() {
            CartItemId cartItemId = new CartItemId(CUSTOMER_ID_SAMPLE, PRODUCT_ID_SAMPLE);
            String differentType = "notACartItemId";

            assertThat(cartItemId).isNotEqualTo(differentType);
        }
    }

    @Nested
    class CartItemIdHashCodeTest {

        @Test
        void testHashCode_withSameValues_shouldReturnSameHashCode() {
            CartItemId cartItemId1 = new CartItemId(CUSTOMER_ID_SAMPLE, PRODUCT_ID_SAMPLE);
            CartItemId cartItemId2 = new CartItemId(CUSTOMER_ID_SAMPLE, PRODUCT_ID_SAMPLE);

            assertThat(cartItemId1.hashCode()).isEqualTo(cartItemId2.hashCode());
        }

        @Test
        void testHashCode_withDifferentCustomerId_shouldReturnDifferentHashCode() {
            CartItemId cartItemId1 = new CartItemId("user1", PRODUCT_ID_SAMPLE);
            CartItemId cartItemId2 = new CartItemId("user2", PRODUCT_ID_SAMPLE);

            assertThat(cartItemId1.hashCode()).isNotEqualTo(cartItemId2.hashCode());
        }

        @Test
        void testHashCode_withDifferentProductId_shouldReturnDifferentHashCode() {
            CartItemId cartItemId1 = new CartItemId(CUSTOMER_ID_SAMPLE, 1L);
            CartItemId cartItemId2 = new CartItemId(CUSTOMER_ID_SAMPLE, 2L);

            assertThat(cartItemId1.hashCode()).isNotEqualTo(cartItemId2.hashCode());
        }

        @Test
        void testHashCode_consistency_shouldReturnSameValueOnMultipleCalls() {
            CartItemId cartItemId = new CartItemId(CUSTOMER_ID_SAMPLE, PRODUCT_ID_SAMPLE);

            int hashCode1 = cartItemId.hashCode();
            int hashCode2 = cartItemId.hashCode();

            assertThat(hashCode1).isEqualTo(hashCode2);
        }
    }

    @Nested
    class CartItemIdComplexTest {

        @Test
        void testCanBeUsedInMap_withSameIds() {
            CartItemId key1 = new CartItemId(CUSTOMER_ID_SAMPLE, PRODUCT_ID_SAMPLE);
            CartItemId key2 = new CartItemId(CUSTOMER_ID_SAMPLE, PRODUCT_ID_SAMPLE);

            var map = new java.util.HashMap<CartItemId, String>();
            map.put(key1, "value1");
            map.put(key2, "value2");

            assertThat(map).hasSize(1);
            assertThat(map.get(key1)).isEqualTo("value2");
        }

        @Test
        void testCanBeUsedInSet_withDuplicates() {
            CartItemId id1 = new CartItemId(CUSTOMER_ID_SAMPLE, PRODUCT_ID_SAMPLE);
            CartItemId id2 = new CartItemId(CUSTOMER_ID_SAMPLE, PRODUCT_ID_SAMPLE);
            CartItemId id3 = new CartItemId("differentUser", PRODUCT_ID_SAMPLE);

            var set = new java.util.HashSet<CartItemId>();
            set.add(id1);
            set.add(id2);
            set.add(id3);

            assertThat(set).hasSize(2);
        }
    }
}
