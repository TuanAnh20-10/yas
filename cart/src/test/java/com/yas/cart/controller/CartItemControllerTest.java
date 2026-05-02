package com.yas.cart.controller;

import tools.jackson.databind.ObjectMapper;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import com.yas.cart.service.CartItemService;
import com.yas.cart.viewmodel.CartItemDeleteVm;
import com.yas.cart.viewmodel.CartItemGetVm;
import com.yas.cart.viewmodel.CartItemPostVm;
import com.yas.cart.viewmodel.CartItemPutVm;
import com.yas.commonlibrary.exception.ApiExceptionHandler;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@ContextConfiguration(classes = {
    CartItemController.class,
    ApiExceptionHandler.class
})
@AutoConfigureMockMvc(addFilters = false)
class CartItemControllerTest {

    private static final Long PRODUCT_ID_SAMPLE = 1L;
    private static final String CUSTOMER_ID_SAMPLE = "customerId";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartItemService cartItemService;

    @Nested
    class AddToCartTest {

        private CartItemPostVm.CartItemPostVmBuilder cartItemPostVmBuilder;

        @BeforeEach
        void setUp() {
            cartItemPostVmBuilder = CartItemPostVm.builder()
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(1);
        }

        @Test
        void testAddToCart_whenProductIdIsNull_shouldReturnBadRequest() throws Exception {
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.productId(null).build();
            performAddCartItemAndExpectBadRequest(cartItemPostVm);
        }

        @Test
        void testAddToCart_whenQuantityIsNull_shouldReturnBadRequest() throws Exception {
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.quantity(null).build();
            performAddCartItemAndExpectBadRequest(cartItemPostVm);
        }

        @Test
        void testAddToCart_whenQuantityIsLessThanOne_shouldReturnBadRequest() throws Exception {
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.quantity(0).build();
            performAddCartItemAndExpectBadRequest(cartItemPostVm);
        }

        @Test
        void testAddToCart_whenRequestIsValid_shouldReturnCartItem() throws Exception {
            CartItemPostVm cartItemPostVm = cartItemPostVmBuilder.build();
            CartItemGetVm expectedCartItem = CartItemGetVm.builder()
                .productId(cartItemPostVm.productId())
                .quantity(cartItemPostVm.quantity())
                .build();

            when(cartItemService.addCartItem(cartItemPostVm)).thenReturn(expectedCartItem);

            mockMvc.perform(buildAddCartItemRequest(cartItemPostVm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(expectedCartItem.productId()))
                .andExpect(jsonPath("$.quantity").value(expectedCartItem.quantity()));

            verify(cartItemService).addCartItem(cartItemPostVm);
        }

        private void performAddCartItemAndExpectBadRequest(CartItemPostVm cartItemPostVm)
            throws Exception {
            mockMvc.perform(buildAddCartItemRequest(cartItemPostVm))
                .andExpect(status().isBadRequest());
        }

        private MockHttpServletRequestBuilder buildAddCartItemRequest(CartItemPostVm cartItemPostVm)
            throws Exception {
            return post("/storefront/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemPostVm));
        }
    }

    @Nested
    class UpdateCartItemTest {

        private CartItemPutVm cartItemPutVm;

        @Test
        void testUpdateCartItem_whenQuantityIsNull_shouldReturnBadRequest() throws Exception {
            cartItemPutVm = new CartItemPutVm(null);
            performUpdateCartItemAndExpectBadRequest(cartItemPutVm);
        }

        @Test
        void testUpdateCartItem_whenQuantityIsLessThanOne_shouldReturnBadRequest() throws Exception {
            cartItemPutVm = new CartItemPutVm(0);
            performUpdateCartItemAndExpectBadRequest(cartItemPutVm);
        }

        @Test
        void testUpdateCartItem_whenRequestIsValid_shouldReturnUpdatedCartItemGetVm() throws Exception {
            cartItemPutVm = new CartItemPutVm(1);
            CartItemGetVm expectedCartItemGetVm = CartItemGetVm
                .builder()
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(1)
                .customerId(CUSTOMER_ID_SAMPLE)
                .build();

            when(cartItemService.updateCartItem(anyLong(), any())).thenReturn(expectedCartItemGetVm);

            mockMvc.perform(buildUpdateCartItemRequest(PRODUCT_ID_SAMPLE, cartItemPutVm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(expectedCartItemGetVm.customerId()))
                .andExpect(jsonPath("$.productId").value(expectedCartItemGetVm.productId()))
                .andExpect(jsonPath("$.quantity").value(expectedCartItemGetVm.quantity()));

            verify(cartItemService).updateCartItem(anyLong(), any());
        }

        private void performUpdateCartItemAndExpectBadRequest(CartItemPutVm cartItemPutVm)
            throws Exception {
            mockMvc.perform(buildUpdateCartItemRequest(PRODUCT_ID_SAMPLE, cartItemPutVm))
                .andExpect(status().isBadRequest());
        }

        private MockHttpServletRequestBuilder buildUpdateCartItemRequest(Long productId, CartItemPutVm cartItemPutVm)
            throws Exception {
            return put("/storefront/cart/items/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemPutVm));
        }
    }

    @Nested
    class GetCartItemsTest {

        @Test
        void testGetCartItems_whenRequestIsValid_shouldReturnCartItems() throws Exception {
            CartItemGetVm expectedCartItem = CartItemGetVm.builder()
                .productId(1L)
                .quantity(1)
                .build();

            when(cartItemService.getCartItems()).thenReturn(List.of(expectedCartItem));

            mockMvc.perform(get("/storefront/cart/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(expectedCartItem.productId()))
                .andExpect(jsonPath("$[0].quantity").value(expectedCartItem.quantity()));

            verify(cartItemService).getCartItems();
        }
    }

    @Nested
    class DeleteOrAdjustCartItemTest {

        private CartItemDeleteVm cartItemPutVm;

        @Test
        void testDeleteOrAdjustCartItem_whenQuantityIsNull_shouldReturnBadRequest() throws Exception {
            cartItemPutVm = new CartItemDeleteVm(PRODUCT_ID_SAMPLE, null);
            performDeleteOrAdjustCartItemAndExpectBadRequest(cartItemPutVm);
        }

        @Test
        void testDeleteOrAdjustCartItem_whenQuantityIsLessThanOne_shouldReturnBadRequest() throws Exception {
            cartItemPutVm = new CartItemDeleteVm(PRODUCT_ID_SAMPLE, -1);
            performDeleteOrAdjustCartItemAndExpectBadRequest(cartItemPutVm);
        }

        @Test
        void testDeleteOrAdjustCartItem_whenRequestIsValid_shouldReturnUpdatedCartItems() throws Exception {
            CartItemDeleteVm cartItemDeleteVm = new CartItemDeleteVm(PRODUCT_ID_SAMPLE, 1);
            CartItemGetVm expectedCartItemGetVm = CartItemGetVm
                .builder()
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(1)
                .customerId(CUSTOMER_ID_SAMPLE)
                .build();

            when(cartItemService.deleteOrAdjustCartItem(anyList())).thenReturn(List.of(expectedCartItemGetVm));

            mockMvc.perform(buildDeleteOrAdjustCartItemRequest(cartItemDeleteVm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(expectedCartItemGetVm.customerId()))
                .andExpect(jsonPath("$[0].productId").value(expectedCartItemGetVm.productId()))
                .andExpect(jsonPath("$[0].quantity").value(expectedCartItemGetVm.quantity()));

            verify(cartItemService).deleteOrAdjustCartItem(anyList());
        }

        private void performDeleteOrAdjustCartItemAndExpectBadRequest(CartItemDeleteVm cartItemDeleteVm)
            throws Exception {
            mockMvc.perform(buildDeleteOrAdjustCartItemRequest(cartItemDeleteVm))
                .andExpect(status().isBadRequest());
        }

        private MockHttpServletRequestBuilder buildDeleteOrAdjustCartItemRequest(CartItemDeleteVm cartItemDeleteVm)
            throws Exception {
            return post("/storefront/cart/items/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(cartItemDeleteVm)));
        }
    }

    @Nested
    class DeleteCartItemTest {
        @Test
        void testDeleteCartItem_whenRequestIsValid_shouldReturnNoContent() throws Exception {
            doNothing().when(cartItemService).deleteCartItem(PRODUCT_ID_SAMPLE);

            mockMvc.perform(delete("/storefront/cart/items/1"))
                .andExpect(status().isNoContent());

            verify(cartItemService).deleteCartItem(PRODUCT_ID_SAMPLE);
        }

        @Test
        void testDeleteCartItem_withDifferentProductId_shouldReturnNoContent() throws Exception {
            Long differentProductId = 999L;
            doNothing().when(cartItemService).deleteCartItem(differentProductId);

            mockMvc.perform(delete("/storefront/cart/items/" + differentProductId))
                .andExpect(status().isNoContent());

            verify(cartItemService).deleteCartItem(differentProductId);
        }
    }

    @Nested
    class GetCartItemsEdgeCasesTest {

        @Test
        void testGetCartItems_whenEmptyCart_shouldReturnEmptyList() throws Exception {
            when(cartItemService.getCartItems()).thenReturn(List.of());

            mockMvc.perform(get("/storefront/cart/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

            verify(cartItemService).getCartItems();
        }

        @Test
        void testGetCartItems_whenMultipleItems_shouldReturnAllItems() throws Exception {
            List<CartItemGetVm> cartItems = List.of(
                CartItemGetVm.builder().productId(1L).quantity(1).customerId(CUSTOMER_ID_SAMPLE).build(),
                CartItemGetVm.builder().productId(2L).quantity(2).customerId(CUSTOMER_ID_SAMPLE).build(),
                CartItemGetVm.builder().productId(3L).quantity(3).customerId(CUSTOMER_ID_SAMPLE).build()
            );

            when(cartItemService.getCartItems()).thenReturn(cartItems);

            mockMvc.perform(get("/storefront/cart/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[1].productId").value(2))
                .andExpect(jsonPath("$[2].productId").value(3));

            verify(cartItemService).getCartItems();
        }
    }

    @Nested
    class DeleteOrAdjustCartItemEdgeCasesTest {

        @Test
        void testDeleteOrAdjustCartItem_withMultipleItems_shouldReturnAllRemainingItems() throws Exception {
            List<CartItemDeleteVm> deleteVms = List.of(
                new CartItemDeleteVm(1L, 1),
                new CartItemDeleteVm(2L, 1)
            );
            List<CartItemGetVm> remainingItems = List.of(
                CartItemGetVm.builder().productId(3L).quantity(2).customerId(CUSTOMER_ID_SAMPLE).build()
            );

            when(cartItemService.deleteOrAdjustCartItem(anyList())).thenReturn(remainingItems);

            mockMvc.perform(buildDeleteOrAdjustCartItemRequest(deleteVms.get(0)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

            verify(cartItemService).deleteOrAdjustCartItem(anyList());
        }

        @Test
        void testDeleteOrAdjustCartItem_withProductIdNull_shouldReturnBadRequest() throws Exception {
            CartItemDeleteVm cartItemDeleteVm = new CartItemDeleteVm(null, 1);

            mockMvc.perform(buildDeleteOrAdjustCartItemRequest(cartItemDeleteVm))
                .andExpect(status().isBadRequest());
        }

        private MockHttpServletRequestBuilder buildDeleteOrAdjustCartItemRequest(CartItemDeleteVm cartItemDeleteVm)
            throws Exception {
            return post("/storefront/cart/items/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(cartItemDeleteVm)));
        }
    }

    @Nested
    class UpdateCartItemEdgeCasesTest {

        @Test
        void testUpdateCartItem_withLargeQuantity_shouldReturnOk() throws Exception {
            CartItemPutVm cartItemPutVm = new CartItemPutVm(1000);
            CartItemGetVm expectedCartItemGetVm = CartItemGetVm.builder()
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(1000)
                .customerId(CUSTOMER_ID_SAMPLE)
                .build();

            when(cartItemService.updateCartItem(anyLong(), any())).thenReturn(expectedCartItemGetVm);

            mockMvc.perform(buildUpdateCartItemRequest(PRODUCT_ID_SAMPLE, cartItemPutVm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(1000));

            verify(cartItemService).updateCartItem(anyLong(), any());
        }

        @Test
        void testUpdateCartItem_withQuantityOne_shouldReturnOk() throws Exception {
            CartItemPutVm cartItemPutVm = new CartItemPutVm(1);
            CartItemGetVm expectedCartItemGetVm = CartItemGetVm.builder()
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(1)
                .customerId(CUSTOMER_ID_SAMPLE)
                .build();

            when(cartItemService.updateCartItem(anyLong(), any())).thenReturn(expectedCartItemGetVm);

            mockMvc.perform(buildUpdateCartItemRequest(PRODUCT_ID_SAMPLE, cartItemPutVm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(1));

            verify(cartItemService).updateCartItem(anyLong(), any());
        }

        private MockHttpServletRequestBuilder buildUpdateCartItemRequest(Long productId, CartItemPutVm cartItemPutVm)
            throws Exception {
            return put("/storefront/cart/items/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemPutVm));
        }
    }

    @Nested
    class AddToCartEdgeCasesTest {

        @Test
        void testAddToCart_withLargeQuantity_shouldReturnOk() throws Exception {
            CartItemPostVm cartItemPostVm = CartItemPostVm.builder()
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(500)
                .build();
            CartItemGetVm expectedCartItem = CartItemGetVm.builder()
                .productId(cartItemPostVm.productId())
                .quantity(cartItemPostVm.quantity())
                .customerId(CUSTOMER_ID_SAMPLE)
                .build();

            when(cartItemService.addCartItem(cartItemPostVm)).thenReturn(expectedCartItem);

            mockMvc.perform(buildAddCartItemRequest(cartItemPostVm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(500));

            verify(cartItemService).addCartItem(cartItemPostVm);
        }

        @Test
        void testAddToCart_withQuantityOne_shouldReturnOk() throws Exception {
            CartItemPostVm cartItemPostVm = CartItemPostVm.builder()
                .productId(PRODUCT_ID_SAMPLE)
                .quantity(1)
                .build();
            CartItemGetVm expectedCartItem = CartItemGetVm.builder()
                .productId(cartItemPostVm.productId())
                .quantity(cartItemPostVm.quantity())
                .customerId(CUSTOMER_ID_SAMPLE)
                .build();

            when(cartItemService.addCartItem(cartItemPostVm)).thenReturn(expectedCartItem);

            mockMvc.perform(buildAddCartItemRequest(cartItemPostVm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(1));

            verify(cartItemService).addCartItem(cartItemPostVm);
        }

        private MockHttpServletRequestBuilder buildAddCartItemRequest(CartItemPostVm cartItemPostVm)
            throws Exception {
            return post("/storefront/cart/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItemPostVm));
        }
    }
}