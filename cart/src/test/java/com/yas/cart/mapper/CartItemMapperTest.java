package com.yas.cart.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.cart.model.CartItem;
import com.yas.cart.viewmodel.CartItemGetVm;
import com.yas.cart.viewmodel.CartItemPostVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CartItemMapperTest {

    private CartItemMapper cartItemMapper;

    private static final String CUSTOMER_ID_SAMPLE = "customerId";
    private static final Long PRODUCT_ID_SAMPLE = 1L;
    private static final int QUANTITY_SAMPLE = 5;

    @BeforeEach
    void setUp() {
        cartItemMapper = new CartItemMapper();
    }

    @Nested
    class ToGetVmTest {

        @Test
        void toGetVm_shouldMapCartItemToGetVm() {
            CartItem cartItem = CartItem.builder()
                .customerId(CUSTOMER_ID_SAMPLE)
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(QUANTITY_SAMPLE)
                .build();

            CartItemGetVm result = cartItemMapper.toGetVm(cartItem);

            assertThat(result).isNotNull();
            assertThat(result.customerId()).isEqualTo(CUSTOMER_ID_SAMPLE);
            assertThat(result.productId()).isEqualTo(PRODUCT_ID_SAMPLE);
            assertThat(result.quantity()).isEqualTo(QUANTITY_SAMPLE);
        }

        @Test
        void toGetVm_withDifferentValues_shouldMapCorrectly() {
            CartItem cartItem = CartItem.builder()
                .customerId("differentUserId")
                .productId(999L)
                .quantity(10)
                .build();

            CartItemGetVm result = cartItemMapper.toGetVm(cartItem);

            assertThat(result.customerId()).isEqualTo("differentUserId");
            assertThat(result.productId()).isEqualTo(999L);
            assertThat(result.quantity()).isEqualTo(10);
        }
    }

    @Nested
    class ToCartItemFromPostVmTest {

        @Test
        void toCartItem_shouldMapCartItemPostVmToCartItem() {
            CartItemPostVm cartItemPostVm = CartItemPostVm.builder()
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(QUANTITY_SAMPLE)
                .build();

            CartItem result = cartItemMapper.toCartItem(cartItemPostVm, CUSTOMER_ID_SAMPLE);

            assertThat(result).isNotNull();
            assertThat(result.getCustomerId()).isEqualTo(CUSTOMER_ID_SAMPLE);
            assertThat(result.getProductId()).isEqualTo(PRODUCT_ID_SAMPLE);
            assertThat(result.getQuantity()).isEqualTo(QUANTITY_SAMPLE);
        }

        @Test
        void toCartItem_withDifferentValues_shouldMapCorrectly() {
            CartItemPostVm cartItemPostVm = CartItemPostVm.builder()
                .productId(555L)
                .quantity(3)
                .build();

            CartItem result = cartItemMapper.toCartItem(cartItemPostVm, "anotherUserId");

            assertThat(result.getCustomerId()).isEqualTo("anotherUserId");
            assertThat(result.getProductId()).isEqualTo(555L);
            assertThat(result.getQuantity()).isEqualTo(3);
        }
    }

    @Nested
    class ToCartItemFromManualParametersTest {

        @Test
        void toCartItem_shouldMapFromManualParameters() {
            CartItem result = cartItemMapper.toCartItem(CUSTOMER_ID_SAMPLE, PRODUCT_ID_SAMPLE, QUANTITY_SAMPLE);

            assertThat(result).isNotNull();
            assertThat(result.getCustomerId()).isEqualTo(CUSTOMER_ID_SAMPLE);
            assertThat(result.getProductId()).isEqualTo(PRODUCT_ID_SAMPLE);
            assertThat(result.getQuantity()).isEqualTo(QUANTITY_SAMPLE);
        }

        @Test
        void toCartItem_withDifferentParameters_shouldMapCorrectly() {
            CartItem result = cartItemMapper.toCartItem("testUser", 888L, 7);

            assertThat(result.getCustomerId()).isEqualTo("testUser");
            assertThat(result.getProductId()).isEqualTo(888L);
            assertThat(result.getQuantity()).isEqualTo(7);
        }

        @Test
        void toCartItem_withQuantityOne_shouldMapCorrectly() {
            CartItem result = cartItemMapper.toCartItem(CUSTOMER_ID_SAMPLE, PRODUCT_ID_SAMPLE, 1);

            assertThat(result.getQuantity()).isEqualTo(1);
        }
    }

    @Nested
    class ToGetVmsTest {

        @Test
        void toGetVms_shouldMapListOfCartItems() {
            CartItem cartItem1 = CartItem.builder()
                .customerId(CUSTOMER_ID_SAMPLE)
                .productId(1L)
                .quantity(1)
                .build();
            CartItem cartItem2 = CartItem.builder()
                .customerId(CUSTOMER_ID_SAMPLE)
                .productId(2L)
                .quantity(2)
                .build();
            CartItem cartItem3 = CartItem.builder()
                .customerId(CUSTOMER_ID_SAMPLE)
                .productId(3L)
                .quantity(3)
                .build();

            List<CartItem> cartItems = List.of(cartItem1, cartItem2, cartItem3);

            List<CartItemGetVm> result = cartItemMapper.toGetVms(cartItems);

            assertThat(result).hasSize(3);
            assertThat(result.get(0).productId()).isEqualTo(1L);
            assertThat(result.get(1).productId()).isEqualTo(2L);
            assertThat(result.get(2).productId()).isEqualTo(3L);
            assertThat(result.get(0).quantity()).isEqualTo(1);
            assertThat(result.get(1).quantity()).isEqualTo(2);
            assertThat(result.get(2).quantity()).isEqualTo(3);
        }

        @Test
        void toGetVms_withEmptyList_shouldReturnEmptyList() {
            List<CartItem> cartItems = List.of();

            List<CartItemGetVm> result = cartItemMapper.toGetVms(cartItems);

            assertThat(result).isEmpty();
        }

        @Test
        void toGetVms_withSingleItem_shouldMapSingleItem() {
            CartItem cartItem = CartItem.builder()
                .customerId(CUSTOMER_ID_SAMPLE)
                .productId(100L)
                .quantity(5)
                .build();

            List<CartItemGetVm> result = cartItemMapper.toGetVms(List.of(cartItem));

            assertThat(result).hasSize(1);
            assertThat(result.get(0).productId()).isEqualTo(100L);
            assertThat(result.get(0).customerId()).isEqualTo(CUSTOMER_ID_SAMPLE);
            assertThat(result.get(0).quantity()).isEqualTo(5);
        }

        @Test
        void toGetVms_shouldPreserveAllProperties() {
            CartItem cartItem1 = CartItem.builder()
                .customerId("user1")
                .productId(10L)
                .quantity(2)
                .build();
            CartItem cartItem2 = CartItem.builder()
                .customerId("user2")
                .productId(20L)
                .quantity(4)
                .build();

            List<CartItemGetVm> result = cartItemMapper.toGetVms(List.of(cartItem1, cartItem2));

            assertThat(result.get(0).customerId()).isEqualTo("user1");
            assertThat(result.get(1).customerId()).isEqualTo("user2");
        }
    }
}
