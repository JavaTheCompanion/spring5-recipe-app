package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientCommandToIngredientConverter;
import guru.springframework.converters.IngredientToIngredientCommandConverter;
import guru.springframework.converters.UnitOfMeasureCommandToUnitOfMeasureConverter;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommandConverter;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.UnitOfMeasureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class IngredientServiceImplTest {

    private final IngredientToIngredientCommandConverter ingredientToIngredientCommandConverter;
    private final IngredientCommandToIngredientConverter ingredientCommandToIngredientConverter;

    @Mock
    RecipeRepository recipeRepository;

    @Mock
    UnitOfMeasureRepository unitOfMeasureRepository;

    IngredientService ingredientService;

    // init converters
    public IngredientServiceImplTest() {
        this.ingredientToIngredientCommandConverter = new IngredientToIngredientCommandConverter(new UnitOfMeasureToUnitOfMeasureCommandConverter());
        this.ingredientCommandToIngredientConverter = new IngredientCommandToIngredientConverter(new UnitOfMeasureCommandToUnitOfMeasureConverter());
    }

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        this.ingredientService = new IngredientServiceImpl(ingredientToIngredientCommandConverter, ingredientCommandToIngredientConverter,
                recipeRepository, unitOfMeasureRepository);
    }

    @Test
    public void findByRecipeIdAndIngredientIdHappyPath() {
        // given
        Recipe recipe = new Recipe();
        recipe.setId(1L);

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setId(1L);

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setId(2L);

        Ingredient ingredient3 = new Ingredient();
        ingredient3.setId(3L);

        recipe.addIngredient(ingredient1);
        recipe.addIngredient(ingredient2);
        recipe.addIngredient(ingredient3);

        Optional<Recipe> recipeOptional = Optional.of(recipe);

        when(this.recipeRepository.findById(anyLong())).thenReturn(recipeOptional);

        // when
        IngredientCommand ingredientCommand = this.ingredientService.findByRecipeIdAndIngredientId(1L, 3L);

        // then
        assertEquals(Long.valueOf(3L), ingredientCommand.getId());
        assertEquals(Long.valueOf(1L), ingredientCommand.getRecipeId());
        verify(this.recipeRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testSaveRecipeCommand() {
        // given
        IngredientCommand command = new IngredientCommand();
        command.setId(3L);
        command.setRecipeId(2L);

        Optional<Recipe> recipeOptional = Optional.of(new Recipe());

        Recipe savedRecipe = new Recipe();
        savedRecipe.addIngredient(new Ingredient());
        savedRecipe.getIngredients().iterator().next().setId(3L);

        when(this.recipeRepository.findById(anyLong())).thenReturn(recipeOptional);
        when(this.recipeRepository.save(any())).thenReturn(savedRecipe);

        // when
        IngredientCommand savedCommand = this.ingredientService.saveIngredientCommand(command);

        // then
        assertEquals(Long.valueOf(3L), savedCommand.getId());
        verify(this.recipeRepository, times(1)).findById(anyLong());
        verify(this.recipeRepository, times(1)).save(any(Recipe.class));

    }

    @Test
    public void testDeleteById() {
        // given
        Recipe recipe = new Recipe();

        Ingredient ingredient = new Ingredient();
        ingredient.setId(3L);

        recipe.addIngredient(ingredient);

        ingredient.setRecipe(recipe);

        Optional<Recipe> recipeOptional = Optional.of(recipe);

        when(this.recipeRepository.findById(anyLong())).thenReturn(recipeOptional);

        // when
        ingredientService.deleteById(1L, 3L);

        //then
        verify(this.recipeRepository, times(1)).findById(anyLong());
        verify(this.recipeRepository, times(1)).save(any(Recipe.class));
    }
}