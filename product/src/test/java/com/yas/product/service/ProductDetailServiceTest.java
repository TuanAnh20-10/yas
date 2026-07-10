package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.*;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailInfoVm;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductDetailServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MediaService mediaService;

    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;

    @InjectMocks
    private ProductDetailService productDetailService;

    private Product product;
    private final Long productId = 1L;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(productId);
        product.setPublished(true);
        product.setHasOptions(false);
        product.setThumbnailMediaId(10L);
        product.setAttributeValues(Collections.emptyList());
    }

    @Test
    void getProductDetailById_ShouldReturnBasicInfo_WhenProductHasNoVariations() {
        // Arrange
        Brand brand = new Brand();
        brand.setId(100L);
        brand.setName("Yas Brand");
        product.setBrand(brand);

        Category category = new Category();
        category.setName("Electronics");
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(category);
        product.setProductCategories(List.of(productCategory));

        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "http://image.url/10", ""));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        // Assert - Sửa từ result.id() sang result.getId()
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Yas Brand", result.getBrandName());
        assertEquals(1, result.getCategories().size());
        assertEquals("Electronics", result.getCategories().get(0).getName());
        assertTrue(result.getVariations().isEmpty());
    }

    @Test
    void getProductDetailById_ShouldReturnVariations_WhenProductHasOptions() {
        // Arrange
        product.setHasOptions(true);

        Product variation = new Product();
        variation.setId(2L);
        variation.setPublished(true);
        variation.setSku("VAR-01");

        // SỬA LỖI TẠI ĐÂY: Dùng List.of thay vì Set.of để khớp kiểu dữ liệu
        product.setProducts(List.of(variation));

        ProductOption option = new ProductOption();
        option.setId(5L);

        ProductOptionCombination combination = new ProductOptionCombination();
        combination.setProductOption(option);
        combination.setValue("Red");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(anyLong())).thenReturn(new NoFileMediaVm(1L, "", "", "url", ""));
        when(productOptionCombinationRepository.findAllByProduct(variation)).thenReturn(List.of(combination));

        // Act
        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        // Assert - Sửa sang Getter
        assertNotNull(result);
        assertEquals(1, result.getVariations().size());
        assertEquals("VAR-01", result.getVariations().get(0).sku()); // Kiểm tra ProductVariationGetVm là record hay class để sửa tương ứng
        assertEquals("Red", result.getVariations().get(0).options().get(5L));
    }

    @Test
    void getProductDetailById_ShouldHandleNullBrandAndCategories() {
        // Arrange
        product.setBrand(null);
        product.setProductCategories(null);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "url", ""));

        // Act
        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        // Assert
        assertNull(result.getBrandId());
        assertNull(result.getBrandName());
        assertTrue(result.getCategories().isEmpty());
    }

    @Test
    void getProductDetailById_ShouldThrowNotFound_WhenProductDoesNotExist() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(productId));
    }

    @Test
    void getProductDetailById_ShouldThrowNotFound_WhenProductIsUnpublished() {
        product.setPublished(false);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(productId));
    }

    @Test
    void getProductDetailById_ShouldReturnNullThumbnailAndEmptyImages_WhenMediaIsMissing() {
        product.setThumbnailMediaId(null);
        product.setProductImages(null);
        product.setProductCategories(new ArrayList<>());
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        assertNotNull(result);
        assertNull(result.getThumbnail());
        assertTrue(result.getProductImages().isEmpty());
    }

    @Test
    void getProductDetailById_ShouldFilterOutUnpublishedVariations() {
        product.setHasOptions(true);

        Product publishedVariation = new Product();
        publishedVariation.setId(2L);
        publishedVariation.setName("Published Variation");
        publishedVariation.setSlug("published-variation");
        publishedVariation.setSku("VAR-01");
        publishedVariation.setGtin("GTIN-V1");
        publishedVariation.setPrice(12.0);
        publishedVariation.setPublished(true);
        publishedVariation.setProductImages(List.of());

        Product unpublishedVariation = new Product();
        unpublishedVariation.setId(3L);
        unpublishedVariation.setPublished(false);

        product.setProducts(List.of(publishedVariation, unpublishedVariation));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "main-url"));
        when(productOptionCombinationRepository.findAllByProduct(publishedVariation)).thenReturn(List.of());

        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        assertEquals(1, result.getVariations().size());
        assertEquals("VAR-01", result.getVariations().getFirst().sku());
    }

    @Test
    void getProductDetailById_ShouldReturnVariationMediaAndOptions() {
        product.setHasOptions(true);

        Product variation = new Product();
        variation.setId(2L);
        variation.setPublished(true);
        variation.setName("Variation");
        variation.setSlug("variation");
        variation.setSku("VAR-02");
        variation.setGtin("GTIN-V2");
        variation.setPrice(20.0);
        variation.setThumbnailMediaId(20L);
        variation.setProductImages(List.of(ProductImage.builder().imageId(21L).product(variation).build()));
        product.setProducts(List.of(variation));

        ProductOption option = new ProductOption();
        option.setId(5L);
        ProductOptionCombination combination = new ProductOptionCombination();
        combination.setProductOption(option);
        combination.setValue("Blue");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "main-url"));
        when(mediaService.getMedia(20L)).thenReturn(new NoFileMediaVm(20L, "", "", "", "thumb-url"));
        when(mediaService.getMedia(21L)).thenReturn(new NoFileMediaVm(21L, "", "", "", "image-url"));
        when(productOptionCombinationRepository.findAllByProduct(variation)).thenReturn(List.of(combination));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        assertEquals(1, result.getVariations().size());
        assertEquals("thumb-url", result.getVariations().getFirst().thumbnail().url());
        assertEquals("image-url", result.getVariations().getFirst().productImages().getFirst().url());
        assertEquals("Blue", result.getVariations().getFirst().options().get(5L));
    }
}
