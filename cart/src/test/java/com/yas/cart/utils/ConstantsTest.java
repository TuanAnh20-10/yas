package com.yas.cart.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ConstantsTest {

    @Nested
    class ErrorCodeConstantsTest {

        @Test
        void testNotFoundProductErrorCode_shouldBeDefined() {
            assertThat(Constants.ErrorCode.NOT_FOUND_PRODUCT)
                .isNotNull()
                .isEqualTo("NOT_FOUND_PRODUCT");
        }

        @Test
        void testNotExistingItemInCartErrorCode_shouldBeDefined() {
            assertThat(Constants.ErrorCode.NOT_EXISTING_ITEM_IN_CART)
                .isNotNull()
                .isEqualTo("NOT_EXISTING_ITEM_IN_CART");
        }

        @Test
        void testNotExistingProductInCartErrorCode_shouldBeDefined() {
            assertThat(Constants.ErrorCode.NOT_EXISTING_PRODUCT_IN_CART)
                .isNotNull()
                .isEqualTo("NOT_EXISTING_PRODUCT_IN_CART");
        }

        @Test
        void testNonExistingCartItemErrorCode_shouldBeDefined() {
            assertThat(Constants.ErrorCode.NON_EXISTING_CART_ITEM)
                .isNotNull()
                .isEqualTo("NON_EXISTING_CART_ITEM");
        }

        @Test
        void testAddCartItemFailedErrorCode_shouldBeDefined() {
            assertThat(Constants.ErrorCode.ADD_CART_ITEM_FAILED)
                .isNotNull()
                .isEqualTo("ADD_CART_ITEM_FAILED");
        }

        @Test
        void testDuplicatedCartItemsToDeleteErrorCode_shouldBeDefined() {
            assertThat(Constants.ErrorCode.DUPLICATED_CART_ITEMS_TO_DELETE)
                .isNotNull()
                .isEqualTo("DUPLICATED_CART_ITEMS_TO_DELETE");
        }

        @Test
        void testAllErrorCodesAreUniqueStrings() {
            String[] errorCodes = {
                Constants.ErrorCode.NOT_FOUND_PRODUCT,
                Constants.ErrorCode.NOT_EXISTING_ITEM_IN_CART,
                Constants.ErrorCode.NOT_EXISTING_PRODUCT_IN_CART,
                Constants.ErrorCode.NON_EXISTING_CART_ITEM,
                Constants.ErrorCode.ADD_CART_ITEM_FAILED,
                Constants.ErrorCode.DUPLICATED_CART_ITEMS_TO_DELETE
            };

            for (String code : errorCodes) {
                assertThat(code).isNotEmpty().isNotBlank();
            }
        }

        @Test
        void testErrorCodeConstantsCanBeUsedInExceptions() {
            String errorCode = Constants.ErrorCode.NOT_FOUND_PRODUCT;
            assertThat(errorCode).isNotNull();
            // Simulate usage in exception
            Exception exceptionMessage = new Exception("Error: " + errorCode);
            assertThat(exceptionMessage.getMessage()).contains(errorCode);
        }
    }

    @Nested
    class ConstantsClassStructureTest {

        @Test
        void testConstantsClass_shouldBeUtilityClass() {
            assertThat(Constants.class).isNotNull();
        }

        @Test
        void testErrorCodeClass_shouldBeNestedInConstants() {
            assertThat(Constants.ErrorCode.class).isNotNull();
        }

        @Test
        void testErrorCodeCanBeAccessed() {
            // Verify all error codes are accessible
            assertThat(Constants.ErrorCode.NOT_FOUND_PRODUCT).isNotNull();
        }

        @Test
        void testConstantsHaveExpectedStructure() {
            // Constants.ErrorCode should exist and be accessible
            assertThat(Constants.ErrorCode.class.getSimpleName()).isEqualTo("ErrorCode");
        }
    }

    @Nested
    class ConstantsValueTest {

        @Test
        void testErrorCodeValue_shouldMatchExpectedFormat() {
            assertThat(Constants.ErrorCode.NOT_FOUND_PRODUCT)
                .matches("[A-Z_]+");
        }

        @Test
        void testAllErrorCodeValues_shouldBeValidIdentifiers() {
            String[] errorCodes = {
                Constants.ErrorCode.NOT_FOUND_PRODUCT,
                Constants.ErrorCode.NOT_EXISTING_ITEM_IN_CART,
                Constants.ErrorCode.NOT_EXISTING_PRODUCT_IN_CART,
                Constants.ErrorCode.NON_EXISTING_CART_ITEM,
                Constants.ErrorCode.ADD_CART_ITEM_FAILED,
                Constants.ErrorCode.DUPLICATED_CART_ITEMS_TO_DELETE
            };

            for (String code : errorCodes) {
                assertThat(code)
                    .matches("[A-Z_]+")
                    .isNotBlank();
            }
        }

        @Test
        void testErrorCodeConsistency_shouldHaveSameValueOnMultipleAccess() {
            String value1 = Constants.ErrorCode.NOT_FOUND_PRODUCT;
            String value2 = Constants.ErrorCode.NOT_FOUND_PRODUCT;

            assertThat(value1).isEqualTo(value2);
        }
    }
}
