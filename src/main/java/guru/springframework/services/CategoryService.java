package guru.springframework.services;

import guru.springframework.domain.Category;

import java.util.List;

public interface CategoryService {

    List<Category> selectAllCategories();

}
