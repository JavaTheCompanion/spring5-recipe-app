package guru.springframework.converters;

import guru.springframework.commands.CategoryCommand;
import guru.springframework.domain.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryCommandToCategoryConverterTest {

    public static final Long ID_VALUE = 1L;
    public static final String DESCRIPTION = "description";
    CategoryCommandToCategoryConverter categoryCommandToCategoryConverter;

    @BeforeEach
    public void setUp() throws Exception {
        categoryCommandToCategoryConverter = new CategoryCommandToCategoryConverter();
    }

    @Test
    public void testNullObject() {
        assertNull(categoryCommandToCategoryConverter.convert(null));
    }

    @Test
    public void testEmptyObject() {
        assertNotNull(categoryCommandToCategoryConverter.convert(new CategoryCommand()));
    }

    @Test
    public void convert() throws Exception {
        // given
        CategoryCommand categoryCommand = new CategoryCommand();
        categoryCommand.setId(ID_VALUE);
        categoryCommand.setDescription(DESCRIPTION);

        // when
        Category category = categoryCommandToCategoryConverter.convert(categoryCommand);

        // then
        assertEquals(ID_VALUE, category.getId());
        assertEquals(DESCRIPTION, category.getDescription());
    }

}