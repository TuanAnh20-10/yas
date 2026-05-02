package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.product.ProductApplication;
import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.category.CategoryGetDetailVm;
import com.yas.product.viewmodel.category.CategoryGetVm;
import com.yas.product.viewmodel.category.CategoryPostVm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = ProductApplication.class)
class CategoryServiceTest {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @MockitoBean
    private MediaService mediaService;
    @Autowired
    private CategoryService categoryService;

    private Category category;
    private NoFileMediaVm noFileMediaVm;

    @BeforeEach
    void setUp() {

        category = new Category();
        category.setName("name");
        category.setSlug("slug");
        category.setDescription("description");
        category.setMetaKeyword("metaKeyword");
        category.setMetaDescription("metaDescription");
        category.setDisplayOrder((short) 1);
        category.setIsPublished(true);
        category.setImageId(1L);
        categoryRepository.save(category);

        noFileMediaVm = new NoFileMediaVm(1L, "caption", "fileName", "mediaType", "url");
    }

    @AfterEach
    void tearDown() {
        productCategoryRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void getCategoryById_Success() {
        when(mediaService.getMedia(category.getImageId())).thenReturn(noFileMediaVm);
        CategoryGetDetailVm categoryGetDetailVm = categoryService.getCategoryById(category.getId());
        assertNotNull(categoryGetDetailVm);
        assertEquals("name", categoryGetDetailVm.name());
    }

    @Test
    void getCategories_Success() {
        when(mediaService.getMedia(any())).thenReturn(noFileMediaVm);
        Assertions.assertEquals(1, categoryService.getCategories("name").size());
        CategoryGetVm categoryGetVm = categoryService.getCategories("name").getFirst();
        assertEquals("name", categoryGetVm.name());
    }

    @Test
    void getCategoriesPageable_Success() {
        when(mediaService.getMedia(category.getImageId())).thenReturn(noFileMediaVm);
        Assertions.assertEquals(1, categoryService.getPageableCategories(0, 1).categoryContent().size());
        CategoryGetVm categoryGetVm = categoryService.getCategories("a").getFirst();
        assertEquals("name", categoryGetVm.name());
    }

    @Test
    void getCategoryById_NotFound() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoryById(999L);
        });
    }

    @Test
    void getCategoryById_NoImage() {
        category.setImageId(null);
        categoryRepository.save(category);

        CategoryGetDetailVm result = categoryService.getCategoryById(category.getId());

        Assertions.assertNull(result.categoryImage());
    }

    @Test
    void getCategories_Empty() {
        Assertions.assertEquals(0, categoryService.getCategories("not-exist").size());
    }

    @Test
    void create_Success() {
        var vm = new com.yas.product.viewmodel.category.CategoryPostVm(
                "new", "slug", null, null, null, null, (short)1, true, null
        );

        Category result = categoryService.create(vm);

        assertEquals("new", result.getName());
    }

    @Test
    void create_DuplicateName() {
        var vm = new CategoryPostVm(
                "name",
                "slug", null, null,
                null, null, (short)1, true, null
        );

        Assertions.assertThrows(DuplicatedException.class, () -> {
            categoryService.create(vm);
        });
    }

    @Test
    void create_ParentNotFound() {
        var vm = new CategoryPostVm(
                "new", "slug", null,
                999L,
                null, null, (short)1, true, null
        );

        Assertions.assertThrows(BadRequestException.class, () -> {
            categoryService.create(vm);
        });
    }

    @Test
    void update_Success() {
        var vm = new CategoryPostVm(
                "new", "slug", null, null,
                null, null, (short)1, true, null
        );

        categoryService.update(vm, category.getId());

        Category updated = categoryRepository.findById(category.getId()).get();
        assertEquals("new", updated.getName());
    }

    @Test
    void update_NotFound() {
        var vm = new com.yas.product.viewmodel.category.CategoryPostVm(
                "new", "slug", null, null, null, null, (short)1, true, null
        );

        Assertions.assertThrows(RuntimeException.class, () -> {
            categoryService.update(vm, 999L);
        });
    }

    @Test
    void update_DuplicateName() {
        Category another = new Category();
        another.setName("dup");
        another.setSlug("dup");
        categoryRepository.save(another);

        var vm = new CategoryPostVm(
                "dup", "slug", null, null,
                null, null, (short)1, true, null
        );

        Assertions.assertThrows(DuplicatedException.class, () -> {
            categoryService.update(vm, category.getId());
        });
    }

    @Test
    void update_ParentIsItself() {
        var vm = new CategoryPostVm(
                "new", "slug", null,
                category.getId(),
                null, null, (short)1, true, null
        );

        Assertions.assertThrows(BadRequestException.class, () -> {
            categoryService.update(vm, category.getId());
        });
    }

    @Test
    void getCategoryByIds_Success() {
        var result = categoryService.getCategoryByIds(java.util.List.of(category.getId()));

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getTopNthCategories_Success() {
        var result = categoryService.getTopNthCategories(5);

        Assertions.assertNotNull(result);
    }

    @Test
    void create_WithParent_Success() {
        Category parent = new Category();
        parent.setName("parent");
        parent.setSlug("p");
        categoryRepository.save(parent);

        var vm = new CategoryPostVm(
                "child", "slug", null,
                parent.getId(),
                null, null, (short)1, true, null
        );

        Category result = categoryService.create(vm);

        assertEquals(parent.getId(), result.getParent().getId());
    }
}