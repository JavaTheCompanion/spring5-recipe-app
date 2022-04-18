package guru.springframework.services;

import guru.springframework.commands.CategoryCommand;
import guru.springframework.converters.CategoryToCategoryCommandConverter;
import guru.springframework.domain.Category;
import guru.springframework.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class CategoryServiceImplTest {

    @Mock
    CategoryRepository categoryRepository;

    CategoryService categoryService;


    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        this.categoryService = new CategoryServiceImpl(this.categoryRepository, new CategoryToCategoryCommandConverter());
    }

    @Test
    public void testSelectAllCategories() {
        // given
        Category category1 = new Category();
        category1.setId(1L);
        category1.setDescription("American");

        Category category2 = new Category();
        category1.setId(2L);
        category1.setDescription("Mexican");

        when(this.categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

        // when
        List<Category> categoryList = this.categoryService.selectAllCategories();

        // then
        assertNotNull(categoryList);
        assertEquals(2, categoryList.size());
        verify(this.categoryRepository, times(1)).findAll();
    }

}