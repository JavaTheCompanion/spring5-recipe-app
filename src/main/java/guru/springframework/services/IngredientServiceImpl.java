package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientCommandToIngredientConverter;
import guru.springframework.converters.IngredientToIngredientCommandConverter;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.exceptions.ResourceNotFoundException;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommandConverter ingredientToIngredientCommandConverter;
    private final IngredientCommandToIngredientConverter ingredientCommandToIngredientConverter;
    private final RecipeRepository recipeRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;

    public IngredientServiceImpl(IngredientToIngredientCommandConverter ingredientToIngredientCommandConverter,
                                 IngredientCommandToIngredientConverter ingredientCommandToIngredientConverter,
                                 RecipeRepository recipeRepository, UnitOfMeasureRepository unitOfMeasureRepository) {
        this.ingredientToIngredientCommandConverter = ingredientToIngredientCommandConverter;
        this.ingredientCommandToIngredientConverter = ingredientCommandToIngredientConverter;
        this.recipeRepository = recipeRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    @Override
    public IngredientCommand findByRecipeIdAndIngredientId(Long recipeId, Long ingredientId) {

        Optional<Recipe> recipeOptional = this.recipeRepository.findById(recipeId);

        if (recipeOptional.isEmpty()) {
            throw new ResourceNotFoundException("Recipe with ID [" + recipeId + "] Not Found");
        }

        Recipe recipe = recipeOptional.get();

        Optional<IngredientCommand> ingredientCommandOptional = recipe.getIngredients().stream()
                .filter(ingredient -> ingredient.getId().equals(ingredientId))
                .map(this.ingredientToIngredientCommandConverter::convert).findFirst();

        if (ingredientCommandOptional.isEmpty()) {
            throw new ResourceNotFoundException("Ingredient with ID [" + ingredientId + "] Not Found");
        }

        return ingredientCommandOptional.get();
    }

    @Override
    @Transactional
    public IngredientCommand saveIngredientCommand(IngredientCommand command) {
        final Optional<Recipe> recipeOptional = this.recipeRepository.findById(command.getRecipeId());

        if (recipeOptional.isEmpty()) {
            throw new ResourceNotFoundException("Recipe with ID [" + command.getRecipeId() + "] Not Found");
        } else {
            final Recipe recipe = recipeOptional.get();

            final Optional<Ingredient> ingredientOptional = recipe
                    .getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(command.getId()))
                    .findFirst();

            if (ingredientOptional.isPresent()) {
                // update existing Ingredient
                final Ingredient ingredientFound = ingredientOptional.get();

                ingredientFound.setDescription(command.getDescription());
                ingredientFound.setAmount(command.getAmount());

                final Optional<UnitOfMeasure> unitOfMeasure = this.unitOfMeasureRepository.findById(command.getUom().getId());

                if(unitOfMeasure.isEmpty()) {
                    throw new ResourceNotFoundException("UnitOfMeasure with ID [" + command.getUom().getId() + "] Not Found");
                }

                ingredientFound.setUom(unitOfMeasure.get());
            } else {
                // add new Ingredient
                final Ingredient ingredient = this.ingredientCommandToIngredientConverter.convert(command);
                ingredient.setRecipe(recipe);
                recipe.addIngredient(ingredient);
            }

            final Recipe savedRecipe = this.recipeRepository.save(recipe);

            Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
                    .filter(recipeIngredients -> recipeIngredients.getId().equals(command.getId()))
                    .findFirst();

            // check by description
            if (savedIngredientOptional.isEmpty()) {
                //not totally safe... But best guess
                savedIngredientOptional = savedRecipe.getIngredients().stream()
                        .filter(recipeIngredient -> recipeIngredient.getDescription().equals(command.getDescription()))
                        .filter(recipeIngredient -> recipeIngredient.getAmount().equals(command.getAmount()))
                        .filter(recipeIngredient -> recipeIngredient.getUom().getId().equals(command.getUom().getId()))
                        .findFirst();
            }

            // to do check for fail
            return this.ingredientToIngredientCommandConverter.convert(savedIngredientOptional.get());
        }
    }

    @Override
    public void deleteById(Long recipeId, Long idToDelete) {

        final Optional<Recipe> recipeOptional = this.recipeRepository.findById(recipeId);

        if (recipeOptional.isPresent()) {
            final Recipe recipe = recipeOptional.get();

            final Optional<Ingredient> ingredientOptional = recipe
                    .getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(idToDelete))
                    .findFirst();

            if (ingredientOptional.isPresent()) {
                final Ingredient ingredientToDelete = ingredientOptional.get();
                ingredientToDelete.setRecipe(null);
                recipe.getIngredients().remove(ingredientOptional.get());
                this.recipeRepository.save(recipe);
            }
        } else {
            throw new ResourceNotFoundException("Recipe with ID [" + recipeId + "] Not Found");
        }
    }

}