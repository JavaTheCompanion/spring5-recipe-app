package guru.springframework.converters;

import guru.springframework.commands.CategoryCommand;
import guru.springframework.domain.Category;
import guru.springframework.repositories.CategoryRepository;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * This Converter used to convert String (Category ID from CheckBox) to CategoryCommand object,
 * so that binding will happen as expected
 */
@Component
public class StringToCategoryCommandConverter implements Converter<String, CategoryCommand> {

    private final CategoryRepository categoryRepository;
    private final CategoryToCategoryCommandConverter categoryToCategoryCommandConverter;

    public StringToCategoryCommandConverter(CategoryRepository categoryRepository,
                                            CategoryToCategoryCommandConverter categoryToCategoryCommandConverter) {
        this.categoryRepository = categoryRepository;
        this.categoryToCategoryCommandConverter = categoryToCategoryCommandConverter;
    }

    @Synchronized
    @Nullable
    @Override
    public CategoryCommand convert(String source) {

        if (source == null) {
            return null;
        }

        final Category category = this.categoryRepository.findById(Long.valueOf(source)).orElse(null);

        if (category != null) {
            return this.categoryToCategoryCommandConverter.convert(category);
        }

        return null;
    }

}
