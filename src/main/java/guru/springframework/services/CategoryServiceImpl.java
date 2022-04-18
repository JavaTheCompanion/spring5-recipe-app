package guru.springframework.services;

import guru.springframework.commands.CategoryCommand;
import guru.springframework.converters.CategoryToCategoryCommandConverter;
import guru.springframework.domain.Category;
import guru.springframework.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryToCategoryCommandConverter categoryToCategoryCommandConverter;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryToCategoryCommandConverter categoryToCategoryCommandConverter) {
        this.categoryRepository = categoryRepository;
        this.categoryToCategoryCommandConverter = categoryToCategoryCommandConverter;
    }

    @Override
    public List<Category> selectAllCategories() {
        final List<Category> categoryList = new ArrayList<>();

        this.categoryRepository.findAll().forEach(categoryList::add);

        return categoryList;
    }

}
