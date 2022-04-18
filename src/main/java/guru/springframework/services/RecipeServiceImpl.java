package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipeConverter;
import guru.springframework.converters.RecipeToRecipeCommandConverter;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.ResourceNotFoundException;
import guru.springframework.repositories.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeCommandToRecipeConverter recipeCommandToRecipeConverter;
    private final RecipeToRecipeCommandConverter recipeToRecipeCommandConverter;

    public RecipeServiceImpl(RecipeRepository recipeRepository,
                             RecipeCommandToRecipeConverter recipeCommandToRecipeConverter,
                             RecipeToRecipeCommandConverter recipeToRecipeCommandConverter) {
        this.recipeRepository = recipeRepository;
        this.recipeCommandToRecipeConverter = recipeCommandToRecipeConverter;
        this.recipeToRecipeCommandConverter = recipeToRecipeCommandConverter;
    }

    @Override
    public List<Recipe> getRecipes() {
        final List<Recipe> recipeList = new ArrayList<>();
        this.recipeRepository.findAll().forEach(recipeList::add);

        recipeList.sort(Comparator.comparing(Recipe::getId));

        return recipeList;
    }

    @Override
    public Recipe findById(Long recipeId) {
        final Optional<Recipe> recipeOptional = this.recipeRepository.findById(recipeId);

        if (recipeOptional.isEmpty()) {
            throw new ResourceNotFoundException("Recipe with ID [" + recipeId + "] Not Found");
        }

        final Recipe recipe = recipeOptional.get();
        recipe.getIngredients().sort(Comparator.comparing(Ingredient::getDescription));

        return recipe;
    }

    @Override
    @Transactional
    public RecipeCommand findCommandById(Long recipeId) {
        return this.recipeToRecipeCommandConverter.convert(this.findById(recipeId));
    }

    @Override
    @Transactional
    public RecipeCommand saveRecipeCommand(RecipeCommand command) {
        final Recipe detachedRecipe = this.recipeCommandToRecipeConverter.convert(command);

        final Recipe savedRecipe = this.recipeRepository.save(detachedRecipe);

        return this.recipeToRecipeCommandConverter.convert(savedRecipe);
    }

    @Override
    public void deleteById(Long recipeId) {
        this.recipeRepository.deleteById(recipeId);
    }

    @Override
    @Transactional
    public void saveRecipeImage(Long recipeId, MultipartFile imageFile) {
        try {
            final Recipe recipe = this.recipeRepository.findById(recipeId).orElse(null);

            if (Objects.nonNull(recipe)) {
                recipe.setImage(imageFile.getBytes());
                this.recipeRepository.save(recipe);
            } else {
                throw new ResourceNotFoundException("Recipe with ID [" + recipeId + "] not found");
            }
        } catch (final IOException e) {
            log.error("Error occurred while saving Recipe Image", e);
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }

}
