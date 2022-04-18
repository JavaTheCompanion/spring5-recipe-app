package guru.springframework.converters;

import guru.springframework.commands.CategoryCommand;
import guru.springframework.domain.Category;
import guru.springframework.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StringToCategoryCommandConverterTest {

    public static final Long ID_VALUE = 1L;
    public static final String DESCRIPTION = "description";

    StringToCategoryCommandConverter stringToCategoryCommandConverter;

    @Mock
    CategoryRepository categoryRepository;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        this.stringToCategoryCommandConverter = new StringToCategoryCommandConverter(this.categoryRepository, new CategoryToCategoryCommandConverter());
    }

    @Test
    public void testNullObject() {
        assertNull(this.stringToCategoryCommandConverter.convert(null));
    }

    @Test
    public void convert() throws Exception {
        // given
        Category category = new Category();
        category.setId(ID_VALUE);
        category.setDescription(DESCRIPTION);

        // when
        when(this.categoryRepository.findById(ID_VALUE)).thenReturn(Optional.of(category));
        CategoryCommand categoryCommand = stringToCategoryCommandConverter.convert(ID_VALUE.toString());

        // then
        assertEquals(ID_VALUE, categoryCommand.getId());
        assertEquals(DESCRIPTION, categoryCommand.getDescription());
    }

}