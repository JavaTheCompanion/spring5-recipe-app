package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipeConverter;
import guru.springframework.converters.RecipeToRecipeCommandConverter;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.ResourceNotFoundException;
import guru.springframework.repositories.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RecipeServiceImplTest {

    RecipeServiceImpl recipeService;

    @Captor
    ArgumentCaptor<Recipe> argumentCaptor;

    @Mock
    RecipeRepository recipeRepository;

    @Mock
    RecipeToRecipeCommandConverter recipeToRecipeCommandConverter;

    @Mock
    RecipeCommandToRecipeConverter recipeCommandToRecipeConverter;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        recipeService = new RecipeServiceImpl(recipeRepository, recipeCommandToRecipeConverter, recipeToRecipeCommandConverter);
    }

    @Test
    public void getRecipeByIdTest() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        Optional<Recipe> recipeOptional = Optional.of(recipe);

        when(this.recipeRepository.findById(anyLong())).thenReturn(recipeOptional);

        Recipe recipeReturned = recipeService.findById(1L);

        assertNotNull(recipeReturned);
        verify(this.recipeRepository, times(1)).findById(anyLong());
        verify(this.recipeRepository, never()).findAll();
    }

    @Test
    public void getRecipeByIdTestNotFound() {
        assertThrows(ResourceNotFoundException.class,
                () -> {
                    Optional<Recipe> recipeOptional = Optional.empty();

                    when(this.recipeRepository.findById(anyLong())).thenReturn(recipeOptional);

                    this.recipeService.findById(1L);
                });
    }

    @Test
    public void getRecipeCommandByIdTest() {
        // when
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        Optional<Recipe> recipeOptional = Optional.of(recipe);

        when(this.recipeRepository.findById(anyLong())).thenReturn(recipeOptional);

        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId(1L);

        when(this.recipeToRecipeCommandConverter.convert(any())).thenReturn(recipeCommand);

        // when
        RecipeCommand commandById = this.recipeService.findCommandById(1L);

        // then
        assertNotNull(commandById);
        verify(this.recipeRepository, times(1)).findById(anyLong());
        verify(this.recipeRepository, never()).findAll();
    }

    @Test
    public void getRecipesTest() {

        Recipe recipe = new Recipe();
        List<Recipe> recipeList = new ArrayList<>();
        recipeList.add(recipe);

        when(this.recipeService.getRecipes()).thenReturn(recipeList);

        List<Recipe> recipes = this.recipeService.getRecipes();

        assertEquals(recipes.size(), 1);
        verify(this.recipeRepository, times(1)).findAll();
        verify(this.recipeRepository, never()).findById(anyLong());
    }

    @Test
    public void testDeleteById() {
        // given
        Long idToDelete = 2L;

        // when
        this.recipeService.deleteById(idToDelete);

        // then
        verify(this.recipeRepository, times(1)).deleteById(anyLong());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void saveImageFile() throws Exception {
        // given
        Long id = 1L;
        final MultipartFile multipartFile = new MockMultipartFile("imageFile", "testing.txt", "text/plain",
                "Spring Framework Guru".getBytes());

        final Recipe recipe = new Recipe();
        recipe.setId(id);
        Optional<Recipe> recipeOptional = Optional.of(recipe);

        when(this.recipeRepository.findById(anyLong())).thenReturn(recipeOptional);

        //ArgumentCaptor<Recipe> argumentCaptor = ArgumentCaptor.forClass(Recipe.class);

        // when
        this.recipeService.saveRecipeImage(id, multipartFile);

        // then
        verify(this.recipeRepository, times(1)).save(argumentCaptor.capture());
        Recipe savedRecipe = argumentCaptor.getValue();
        assertEquals(multipartFile.getBytes().length, savedRecipe.getImage().length);
    }

}