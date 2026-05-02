package com.yas.product.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.model.enumeration.DimensionUnit;
import com.yas.product.model.enumeration.FilterExistInWhSelection;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRelatedRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductCheckoutListVm;
import com.yas.product.viewmodel.product.ProductDetailGetVm;
import com.yas.product.viewmodel.product.ProductDetailVm;
import com.yas.product.viewmodel.product.ProductEsDetailVm;
import com.yas.product.viewmodel.product.ProductExportingDetailVm;
import com.yas.product.viewmodel.product.ProductFeatureGetVm;
import com.yas.product.viewmodel.product.ProductGetCheckoutListVm;
import com.yas.product.viewmodel.product.ProductGetDetailVm;
import com.yas.product.viewmodel.product.ProductInfoVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductOptionValueDisplay;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductPutVm;
import com.yas.product.viewmodel.product.ProductQuantityPostVm;
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import com.yas.product.viewmodel.product.ProductSlugGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import com.yas.product.viewmodel.product.ProductVariationGetVm;
import com.yas.product.viewmodel.product.ProductsGetVm;
import com.yas.product.viewmodel.product.ProductVariationPostVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePostVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePutVm;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private MediaService mediaService;
    @Mock private BrandRepository brandRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductCategoryRepository productCategoryRepository;
    @Mock private ProductImageRepository productImageRepository;
    @Mock private ProductOptionRepository productOptionRepository;
    @Mock private ProductOptionValueRepository productOptionValueRepository;
    @Mock private ProductOptionCombinationRepository productOptionCombinationRepository;
    @Mock private ProductRelatedRepository productRelatedRepository;

    @InjectMocks
    private ProductService productService;

    private Product buildProduct(Long id, String slug) {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("BrandX");

        Product product = new Product();
        product.setId(id);
        product.setName("Product " + id);
        product.setSlug(slug);
        product.setSku("SKU-" + id);
        product.setGtin("GTIN-" + id);
        product.setPrice(99.99);
        product.setAllowedToOrder(true);
        product.setPublished(true);
        product.setFeatured(false);
        product.setVisibleIndividually(true);
        product.setStockTrackingEnabled(false);
        product.setStockQuantity(0L);
        product.setTaxClassId(1L);
        product.setThumbnailMediaId(1L);
        product.setShortDescription("Short");
        product.setDescription("Description");
        product.setSpecification("Specification");
        product.setDimensionUnit(DimensionUnit.CM);
        product.setLength(10.0);
        product.setWidth(5.0);
        product.setHeight(3.0);
        product.setWeight(1.5);
        product.setBrand(brand);
        product.setProductCategories(new ArrayList<>());
        product.setProductImages(new ArrayList<>());
        product.setProducts(new ArrayList<>());
        product.setRelatedProducts(new ArrayList<>());
        product.setAttributeValues(new ArrayList<>());
        return product;
    }

    private ProductPostVm buildProductPostVm() {
        return new ProductPostVm(
            "Test Product",
            "test-product",
            1L,
            List.of(1L),
            "Short desc",
            "Description",
            "Specification",
            "SKU-001",
            "GTIN-001",
            1.5,
            DimensionUnit.CM,
            10.0,
            5.0,
            3.0,
            99.99,
            true,
            true,
            false,
            true,
            false,
            "Meta Title",
            "meta,keyword",
            "Meta Description",
            1L,
            List.of(1L, 2L),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            1L
        );
    }

    private ProductPutVm buildProductPutVm() {
        return new ProductPutVm(
            "Updated Product",
            "updated-slug",
            109.99,
            true,
            true,
            false,
            true,
            false,
            1L,
            List.of(1L),
            "Short desc updated",
            "Description updated",
            "Specification updated",
            "SKU-001",
            "GTIN-001",
            1.5,
            DimensionUnit.CM,
            10.0,
            5.0,
            3.0,
            "Meta Title",
            "meta,keyword",
            "Meta Description",
            1L,
            List.of(1L, 2L),
            List.of(),
            List.of(new ProductOptionValuePutVm(10L, "TEXT", 1, List.of("Red"))),
            List.of(new ProductOptionValueDisplay(10L, "TEXT", 1, "Red")),
            List.of(),
            1L
        );
    }

    private ProductPostVm buildProductPostVmWithEmptyImages() {
        return new ProductPostVm(
            "Test Product",
            "test-no-images",
            1L,
            List.of(1L),
            "Short desc",
            "Description",
            "Specification",
            "SKU-002",
            "GTIN-002",
            1.5,
            DimensionUnit.CM,
            10.0,
            5.0,
            3.0,
            50.0,
            true,
            true,
            false,
            true,
            false,
            "Meta Title",
            "meta,keyword",
            "Meta Description",
            null,
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            1L
        );
    }

    @Test
    void createProduct_noVariations_returnsSavedProduct() {
        ProductPostVm vm = buildProductPostVm();
        Brand brand = new Brand();
        brand.setId(1L);
        Product savedProduct = buildProduct(10L, "test-product");

        when(productRepository.findBySlugAndIsPublishedTrue("test-product")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("GTIN-001")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("SKU-001")).thenReturn(Optional.empty());
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(new Category()));
        when(productImageRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productCategoryRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        ProductGetDetailVm result = productService.createProduct(vm);

        assertThat(result).isEqualTo(new ProductGetDetailVm(10L, "Product 10", "test-product"));
        verify(productRepository, atLeastOnce()).save(any(Product.class));
    }

    @Test
    void createProduct_lengthLessThanWidth_throwsBadRequestException() {
        ProductPostVm vm = new ProductPostVm(
            "Test",
            "slug",
            null,
            List.of(),
            "s",
            "d",
            "sp",
            "sku",
            "gtin",
            1.0,
            DimensionUnit.CM,
            3.0,
            5.0,
            2.0,
            10.0,
            true,
            true,
            false,
            true,
            false,
            null,
            null,
            null,
            null,
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            null
        );

        assertThatThrownBy(() -> productService.createProduct(vm))
            .isInstanceOf(BadRequestException.class);
    }

    @Test
    void createProduct_duplicateSlug_throwsDuplicatedException() {
        ProductPostVm vm = buildProductPostVm();
        when(productRepository.findBySlugAndIsPublishedTrue("test-product"))
            .thenReturn(Optional.of(buildProduct(99L, "test-product")));

        assertThatThrownBy(() -> productService.createProduct(vm))
            .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void createProduct_brandNotFound_throwsNotFoundException() {
        ProductPostVm vm = buildProductPostVm();
        when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(brandRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(vm))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createProduct_emptyImages_deletesExistingImages() {
        ProductPostVm vm = buildProductPostVmWithEmptyImages();
        Brand brand = new Brand();
        brand.setId(1L);
        Product savedProduct = buildProduct(10L, "test-no-images");
        Category category = new Category();
        category.setId(1L);

        when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(categoryRepository.findAllById(anyList())).thenReturn(List.of(category));
        when(productImageRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productCategoryRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        productService.createProduct(vm);

        verify(productImageRepository).deleteByProductId(10L);
    }

    @Test
    void updateProduct_productNotFound_throwsNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(99L, buildProductPutVm()))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateProduct_sameSlug_updatesWithoutDuplicateError() {
        Product existing = buildProduct(1L, "updated-slug");
        Category category = new Category();
        category.setId(1L);
        ProductOption option = new ProductOption();
        option.setId(10L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.findBySlugAndIsPublishedTrue("updated-slug")).thenReturn(Optional.of(existing));
        when(productRepository.findByGtinAndIsPublishedTrue("GTIN-001")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("SKU-001")).thenReturn(Optional.empty());
        when(productRepository.findAllById(anyList())).thenReturn(List.of());
        when(categoryRepository.findAllById(anyList())).thenReturn(List.of(category));
        when(productCategoryRepository.findAllByProductId(anyLong())).thenReturn(List.of());
        when(productImageRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productOptionRepository.findAllByIdIn(List.of(10L))).thenReturn(List.of(option));
        when(productOptionValueRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        assertThatNoException().isThrownBy(() -> productService.updateProduct(1L, buildProductPutVm()));
        verify(productOptionValueRepository).deleteAllByProductId(1L);
    }

    @Test
    void getProductsWithFilter_returnsPagedResult() {
        Product product = buildProduct(1L, "slug-1");
        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        when(productRepository.getProductsWithFilter(anyString(), anyString(), any())).thenReturn(page);

        ProductListGetVm result = productService.getProductsWithFilter(0, 10, "name", "brand");

        assertThat(result.productContent()).hasSize(1);
        assertThat(result.pageNo()).isZero();
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void getProductDetail_returnsMappedDetail() {
        Product product = buildProduct(1L, "slug");
        Category category = new Category();
        category.setId(1L);
        category.setName("Category A");
        product.setProductCategories(List.of(ProductCategory.builder().product(product).category(category).build()));
        product.setProductImages(List.of(ProductImage.builder().product(product).imageId(2L).build()));

        when(productRepository.findBySlugAndIsPublishedTrue("slug")).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "", "", "", "thumb-url"));
        when(mediaService.getMedia(2L)).thenReturn(new NoFileMediaVm(2L, "", "", "", "gallery-url"));

        ProductDetailGetVm result = productService.getProductDetail("slug");

        assertThat(result.name()).isEqualTo("Product 1");
        assertThat(result.brandName()).isEqualTo("BrandX");
        assertThat(result.productCategories()).containsExactly("Category A");
        assertThat(result.thumbnailMediaUrl()).isEqualTo("thumb-url");
        assertThat(result.productImageMediaUrls()).containsExactly("gallery-url");
    }

    @Test
    void getProductById_returnsMappedVm() {
        Product product = buildProduct(1L, "slug");
        Category category = new Category();
        category.setId(1L);
        category.setName("Category A");
        product.setProductCategories(List.of(ProductCategory.builder().product(product).category(category).build()));
        product.setProductImages(List.of(ProductImage.builder().product(product).imageId(2L).build()));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "", "", "", "thumb-url"));
        when(mediaService.getMedia(2L)).thenReturn(new NoFileMediaVm(2L, "", "", "", "gallery-url"));

        ProductDetailVm result = productService.getProductById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.brandId()).isEqualTo(1L);
        assertThat(result.categories()).hasSize(1);
        assertThat(result.thumbnailMedia().url()).isEqualTo("thumb-url");
        assertThat(result.productImageMedias()).hasSize(1);
    }

    @Test
    void getLatestProducts_whenCountNonPositive_returnsEmptyList() {
        assertThat(productService.getLatestProducts(0)).isEmpty();
    }

    @Test
    void getLatestProducts_whenProductsExist_returnsMappedList() {
        when(productRepository.getLatestProducts(PageRequest.of(0, 2)))
            .thenReturn(List.of(buildProduct(1L, "slug-1"), buildProduct(2L, "slug-2")));

        assertThat(productService.getLatestProducts(2)).hasSize(2);
    }

    @Test
    void getProductsByBrand_returnsThumbnailVms() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setSlug("brand-slug");

        when(brandRepository.findBySlug("brand-slug")).thenReturn(Optional.of(brand));
        when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(brand))
            .thenReturn(List.of(buildProduct(1L, "slug")));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "", "", "", "thumb-url"));

        List<ProductThumbnailVm> result = productService.getProductsByBrand("brand-slug");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().thumbnailUrl()).isEqualTo("thumb-url");
    }

    @Test
    void getProductsByMultiQuery_returnsPagedProducts() {
        Product product = buildProduct(1L, "slug");
        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(
            eq("name"), eq("category"), eq(10.0), eq(100.0), any(Pageable.class))).thenReturn(page);
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "", "", "", "thumb-url"));

        ProductsGetVm result = productService.getProductsByMultiQuery(0, 10, " Name ", "category", 10.0, 100.0);

        assertThat(result.productContent()).hasSize(1);
        assertThat(result.productContent().getFirst().thumbnailUrl()).isEqualTo("thumb-url");
    }

    @Test
    void updateProductQuantity_updatesStockQuantity() {
        Product product = buildProduct(1L, "slug");
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));

        productService.updateProductQuantity(List.of(new ProductQuantityPostVm(1L, 25L)));

        assertThat(product.getStockQuantity()).isEqualTo(25L);
    }

    @Test
    void subtractStockQuantity_mergesDuplicateProductIds() {
        Product product = buildProduct(1L, "slug");
        product.setStockTrackingEnabled(true);
        product.setStockQuantity(30L);
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));

        productService.subtractStockQuantity(List.of(
            new ProductQuantityPutVm(1L, 5L),
            new ProductQuantityPutVm(1L, 3L)
        ));

        assertThat(product.getStockQuantity()).isEqualTo(22L);
    }

    @Test
    void restoreStockQuantity_increasesStock() {
        Product product = buildProduct(1L, "slug");
        product.setStockTrackingEnabled(true);
        product.setStockQuantity(5L);
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));

        productService.restoreStockQuantity(List.of(new ProductQuantityPutVm(1L, 10L)));

        assertThat(product.getStockQuantity()).isEqualTo(15L);
    }

    @Test
    void getProductsForWarehouse_returnsProductInfoVms() {
        Product product = buildProduct(1L, "slug");
        when(productRepository.findProductForWarehouse(anyString(), anyString(), anyList(), eq("ALL")))
            .thenReturn(List.of(product));

        List<ProductInfoVm> result = productService.getProductsForWarehouse(
            "name", "sku", List.of(1L), FilterExistInWhSelection.ALL);

        assertThat(result).hasSize(1);
    }

    @Test
    void getProductVariationsByParentId_whenParentHasOptions_returnsPublishedVariationsOnly() {
        Product parent = buildProduct(1L, "parent");
        parent.setHasOptions(true);

        Product variation = buildProduct(2L, "variation");
        variation.setPublished(true);
        variation.setThumbnailMediaId(2L);
        variation.setProductImages(List.of(ProductImage.builder().product(variation).imageId(3L).build()));

        Product hiddenVariation = buildProduct(3L, "hidden");
        hiddenVariation.setPublished(false);

        ProductOption option = new ProductOption();
        option.setId(10L);
        ProductOptionCombination combination = new ProductOptionCombination();
        combination.setProductOption(option);
        combination.setValue("Red");

        parent.setProducts(List.of(variation, hiddenVariation));

        when(productRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(productOptionCombinationRepository.findAllByProduct(variation)).thenReturn(List.of(combination));
        when(mediaService.getMedia(2L)).thenReturn(new NoFileMediaVm(2L, "", "", "", "thumb-url"));
        when(mediaService.getMedia(3L)).thenReturn(new NoFileMediaVm(3L, "", "", "", "image-url"));

        List<ProductVariationGetVm> result = productService.getProductVariationsByParentId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().options()).containsEntry(10L, "Red");
        assertThat(result.getFirst().thumbnail().url()).isEqualTo("thumb-url");
    }

    @Test
    void getProductVariationsByParentId_whenParentHasNoOptions_returnsEmptyList() {
        Product parent = buildProduct(1L, "parent");
        parent.setHasOptions(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(parent));

        assertThat(productService.getProductVariationsByParentId(1L)).isEmpty();
    }

    @Test
    void deleteProduct_forVariation_removesOptionCombinationsAndUnpublishes() {
        Product parent = buildProduct(99L, "parent");
        Product variation = buildProduct(1L, "variation");
        variation.setParent(parent);

        ProductOptionCombination combination = new ProductOptionCombination();

        when(productRepository.findById(1L)).thenReturn(Optional.of(variation));
        when(productOptionCombinationRepository.findAllByProduct(variation)).thenReturn(List.of(combination));

        productService.deleteProduct(1L);

        assertThat(variation.isPublished()).isFalse();
        verify(productOptionCombinationRepository).deleteAll(List.of(combination));
        verify(productRepository).save(variation);
    }

    @Test
    void getProductSlug_forVariation_returnsParentSlugAndVariantId() {
        Product parent = buildProduct(99L, "parent-slug");
        Product variation = buildProduct(1L, "variation-slug");
        variation.setParent(parent);

        when(productRepository.findById(1L)).thenReturn(Optional.of(variation));

        ProductSlugGetVm result = productService.getProductSlug(1L);

        assertThat(result.slug()).isEqualTo("parent-slug");
        assertThat(result.productVariantId()).isEqualTo(1L);
    }

    @Test
    void getProductEsDetailById_returnsMappedFields() {
        Product product = buildProduct(1L, "slug");
        Category category = new Category();
        category.setId(1L);
        category.setName("Category A");
        product.setProductCategories(List.of(ProductCategory.builder().product(product).category(category).build()));
        product.setAttributeValues(List.of());

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductEsDetailVm result = productService.getProductEsDetailById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.brand()).isEqualTo("BrandX");
        assertThat(result.categories()).containsExactly("Category A");
    }

    @Test
    void getListFeaturedProducts_returnsPagedFeaturedProducts() {
        Product product = buildProduct(1L, "slug");
        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        when(productRepository.getFeaturedProduct(any())).thenReturn(page);
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));

        ProductFeatureGetVm result = productService.getListFeaturedProducts(0, 10);

        assertThat(result.productList()).hasSize(1);
        assertThat(result.totalPage()).isEqualTo(1);
    }

    @Test
    void getProductCheckoutList_returnsCheckoutItems() {
        Product product = buildProduct(1L, "slug");
        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        when(productRepository.findAllPublishedProductsByIds(List.of(1L), PageRequest.of(0, 10))).thenReturn(page);
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "", "", "", "http://thumb"));

        ProductGetCheckoutListVm result = productService.getProductCheckoutList(0, 10, List.of(1L));

        assertThat(result.productCheckoutListVms()).extracting(ProductCheckoutListVm::thumbnailUrl)
            .containsExactly("http://thumb");
    }

    @Test
    void exportProducts_returnsExportingDetailVms() {
        Product product = buildProduct(1L, "slug");
        when(productRepository.getExportingProducts("", "")).thenReturn(List.of(product));

        List<ProductExportingDetailVm> result = productService.exportProducts("", "");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).brandName()).isEqualTo("BrandX");
    }

    @Test
    void createProduct_duplicateSlugAcrossVariations_throwsDuplicatedException() {
        ProductVariationPostVm variationOne = new ProductVariationPostVm(
            "Var1", "dup-slug", "SKU-V1", "G-V1", 50.0, null, List.of(), Map.of());
        ProductVariationPostVm variationTwo = new ProductVariationPostVm(
            "Var2", "dup-slug", "SKU-V2", "G-V2", 60.0, null, List.of(), Map.of());

        ProductPostVm vm = new ProductPostVm(
            "Test",
            "main-slug",
            null,
            List.of(),
            "s",
            "d",
            "sp",
            "SKU-MAIN",
            null,
            1.0,
            DimensionUnit.CM,
            10.0,
            5.0,
            3.0,
            99.0,
            true,
            true,
            false,
            true,
            false,
            null,
            null,
            null,
            null,
            List.of(),
            List.of(variationOne, variationTwo),
            List.of(new ProductOptionValuePostVm(10L, "TEXT", 1, List.of("Red"))),
            List.of(new ProductOptionValueDisplay(10L, "TEXT", 1, "Red")),
            List.of(),
            null
        );

        assertThatThrownBy(() -> productService.createProduct(vm))
            .isInstanceOf(DuplicatedException.class);
    }
}
