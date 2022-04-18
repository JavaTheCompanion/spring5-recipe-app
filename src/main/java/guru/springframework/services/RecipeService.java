package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Recipe;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RecipeService {

    List<Recipe> getRecipes();

    Recipe findById(Long recipeId);

    RecipeCommand findCommandById(Long recipeId);

    RecipeCommand saveRecipeCommand(RecipeCommand command);

    void deleteById(Long recipeId);

    void saveRecipeImage(Long recipeId, MultipartFile imageFile);

}
