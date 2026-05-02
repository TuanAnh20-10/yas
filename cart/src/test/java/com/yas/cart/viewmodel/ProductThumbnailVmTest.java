package com.yas.cart.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ProductThumbnailVmTest {

    private static final Long PRODUCT_ID_SAMPLE = 1L;
    private static final String PRODUCT_NAME_SAMPLE = "Product 1";
    private static final String PRODUCT_SLUG_SAMPLE = "product-1";
    private static final String THUMBNAIL_URL_SAMPLE = "http://example.com/product1.jpg";

    @Nested
    class ProductThumbnailVmConstructorTest {

        @Test
        void testConstructor_shouldCreateProductThumbnailVm() {
            ProductThumbnailVm productThumbnailVm = new ProductThumbnailVm(
                PRODUCT_ID_SAMPLE,
                PRODUCT_NAME_SAMPLE,
                PRODUCT_SLUG_SAMPLE,
                THUMBNAIL_URL_SAMPLE
            );

            assertThat(productThumbnailVm).isNotNull();
            assertThat(productThumbnailVm.id()).isEqualTo(PRODUCT_ID_SAMPLE);
            assertThat(productThumbnailVm.name()).isEqualTo(PRODUCT_NAME_SAMPLE);
            assertThat(productThumbnailVm.slug()).isEqualTo(PRODUCT_SLUG_SAMPLE);
            assertThat(productThumbnailVm.thumbnailUrl()).isEqualTo(THUMBNAIL_URL_SAMPLE);
        }

        @Test
        void testConstructor_withDifferentValues_shouldCreateCorrectly() {
            ProductThumbnailVm productThumbnailVm = new ProductThumbnailVm(
                999L,
                "Different Product",
                "different-product",
                "http://example.com/different.jpg"
            );

            assertThat(productThumbnailVm.id()).isEqualTo(999L);
            assertThat(productThumbnailVm.name()).isEqualTo("Different Product");
            assertThat(productThumbnailVm.slug()).isEqualTo("different-product");
            assertThat(productThumbnailVm.thumbnailUrl()).isEqualTo("http://example.com/different.jpg");
        }
    }

    @Nested
    class ProductThumbnailVmRecordTest {

        @Test
        void testRecord_shouldAccessPropertiesCorrectly() {
            ProductThumbnailVm productThumbnailVm = new ProductThumbnailVm(
                PRODUCT_ID_SAMPLE,
                PRODUCT_NAME_SAMPLE,
                PRODUCT_SLUG_SAMPLE,
                THUMBNAIL_URL_SAMPLE
            );

            assertThat(productThumbnailVm.id()).isEqualTo(PRODUCT_ID_SAMPLE);
            assertThat(productThumbnailVm.name()).isEqualTo(PRODUCT_NAME_SAMPLE);
            assertThat(productThumbnailVm.slug()).isEqualTo(PRODUCT_SLUG_SAMPLE);
            assertThat(productThumbnailVm.thumbnailUrl()).isEqualTo(THUMBNAIL_URL_SAMPLE);
        }

        @Test
        void testRecord_withMultipleInstances_shouldMaintainIndependence() {
            ProductThumbnailVm vm1 = new ProductThumbnailVm(
                1L,
                "Product 1",
                "product-1",
                "http://example.com/product1.jpg"
            );
            ProductThumbnailVm vm2 = new ProductThumbnailVm(
                2L,
                "Product 2",
                "product-2",
                "http://example.com/product2.jpg"
            );

            assertThat(vm1.id()).isEqualTo(1L);
            assertThat(vm2.id()).isEqualTo(2L);
            assertThat(vm1.name()).isEqualTo("Product 1");
            assertThat(vm2.name()).isEqualTo("Product 2");
        }
    }

    @Nested
    class ProductThumbnailVmEqualsAndHashTest {

        @Test
        void testEquals_withSameValues_shouldReturnTrue() {
            ProductThumbnailVm vm1 = new ProductThumbnailVm(
                PRODUCT_ID_SAMPLE,
                PRODUCT_NAME_SAMPLE,
                PRODUCT_SLUG_SAMPLE,
                THUMBNAIL_URL_SAMPLE
            );
            ProductThumbnailVm vm2 = new ProductThumbnailVm(
                PRODUCT_ID_SAMPLE,
                PRODUCT_NAME_SAMPLE,
                PRODUCT_SLUG_SAMPLE,
                THUMBNAIL_URL_SAMPLE
            );

            assertThat(vm1).isEqualTo(vm2);
        }

        @Test
        void testEquals_withDifferentId_shouldReturnFalse() {
            ProductThumbnailVm vm1 = new ProductThumbnailVm(
                1L,
                PRODUCT_NAME_SAMPLE,
                PRODUCT_SLUG_SAMPLE,
                THUMBNAIL_URL_SAMPLE
            );
            ProductThumbnailVm vm2 = new ProductThumbnailVm(
                2L,
                PRODUCT_NAME_SAMPLE,
                PRODUCT_SLUG_SAMPLE,
                THUMBNAIL_URL_SAMPLE
            );

            assertThat(vm1).isNotEqualTo(vm2);
        }

        @Test
        void testEquals_withDifferentName_shouldReturnFalse() {
            ProductThumbnailVm vm1 = new ProductThumbnailVm(
                PRODUCT_ID_SAMPLE,
                "Product 1",
                PRODUCT_SLUG_SAMPLE,
                THUMBNAIL_URL_SAMPLE
            );
            ProductThumbnailVm vm2 = new ProductThumbnailVm(
                PRODUCT_ID_SAMPLE,
                "Product 2",
                PRODUCT_SLUG_SAMPLE,
                THUMBNAIL_URL_SAMPLE
            );

            assertThat(vm1).isNotEqualTo(vm2);
        }

        @Test
        void testHashCode_withSameValues_shouldReturnSameHashCode() {
            ProductThumbnailVm vm1 = new ProductThumbnailVm(
                PRODUCT_ID_SAMPLE,
                PRODUCT_NAME_SAMPLE,
                PRODUCT_SLUG_SAMPLE,
                THUMBNAIL_URL_SAMPLE
            );
            ProductThumbnailVm vm2 = new ProductThumbnailVm(
                PRODUCT_ID_SAMPLE,
                PRODUCT_NAME_SAMPLE,
                PRODUCT_SLUG_SAMPLE,
                THUMBNAIL_URL_SAMPLE
            );

            assertThat(vm1.hashCode()).isEqualTo(vm2.hashCode());
        }

        @Test
        void testToString_shouldIncludeAllProperties() {
            ProductThumbnailVm productThumbnailVm = new ProductThumbnailVm(
                PRODUCT_ID_SAMPLE,
                PRODUCT_NAME_SAMPLE,
                PRODUCT_SLUG_SAMPLE,
                THUMBNAIL_URL_SAMPLE
            );

            String toString = productThumbnailVm.toString();

            assertThat(toString).contains(String.valueOf(PRODUCT_ID_SAMPLE));
            assertThat(toString).contains(PRODUCT_NAME_SAMPLE);
        }
    }
}
