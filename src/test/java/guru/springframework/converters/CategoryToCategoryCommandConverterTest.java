package guru.springframework.converters;

import guru.springframework.commands.CategoryCommand;
import guru.springframework.domain.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryToCategoryCommandConverterTest {

    public static final Long ID_VALUE = 1L;
    public static final String DESCRIPTION = "description";
    CategoryToCategoryCommandConverter categoryToCategoryCommandConverter;

    @BeforeEach
    public void setUp() throws Exception {
        categoryToCategoryCommandConverter = new CategoryToCategoryCommandConverter();
    }

    @Test
    public void testNullObject() {
        assertNull(categoryToCategoryCommandConverter.convert(null));
    }

    @Test
    public void testEmptyObject() {
        assertNotNull(categoryToCategoryCommandConverter.convert(new Category()));
    }

    @Test
    public void convert() throws Exception {
        // given
        Category category = new Category();
        category.setId(ID_VALUE);
        category.setDescription(DESCRIPTION);

        // when
        CategoryCommand categoryCommand = categoryToCategoryCommandConverter.convert(category);

        // then
        assertEquals(ID_VALUE, categoryCommand.getId());
        assertEquals(DESCRIPTION, categoryCommand.getDescription());
    }

}