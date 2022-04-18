package guru.springframework.converters;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Recipe;
import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class RecipeCommandToRecipeConverter implements Converter<RecipeCommand, Recipe> {

    private final CategoryCommandToCategoryConverter categoryCommandToCategoryConverter;
    private final IngredientCommandToIngredientConverter ingredientCommandToIngredientConverter;
    private final NotesCommandToNotesConverter notesCommandToNotesConverter;

    public RecipeCommandToRecipeConverter(CategoryCommandToCategoryConverter categoryCommandToCategoryConverter,
                                          IngredientCommandToIngredientConverter ingredientCommandToIngredientConverter,
                                          NotesCommandToNotesConverter notesCommandToNotesConverter) {
        this.categoryCommandToCategoryConverter = categoryCommandToCategoryConverter;
        this.ingredientCommandToIngredientConverter = ingredientCommandToIngredientConverter;
        this.notesCommandToNotesConverter = notesCommandToNotesConverter;
    }

    @Synchronized
    @Nullable
    @Override
    public Recipe convert(RecipeCommand source) {

        if(source == null) {
            return null;
        }

        final Recipe recipe = new Recipe();
        recipe.setId(source.getId());
        recipe.setCookTime(source.getCookTime());
        recipe.setPrepTime(source.getPrepTime());
        recipe.setDescription(source.getDescription());
        recipe.setDifficulty(source.getDifficulty());
        recipe.setDirections(source.getDirections());
        recipe.setServings(source.getServings());
        recipe.setSource(source.getSource());
        recipe.setUrl(source.getUrl());

        recipe.setNotes(this.notesCommandToNotesConverter.convert(source.getNotes()));

        if (source.getCategories() != null && source.getCategories().size() > 0) {
            source.getCategories()
                    .forEach(category -> recipe.getCategories().add(this.categoryCommandToCategoryConverter.convert(category)));
        }

        if (source.getIngredients() != null && source.getIngredients().size() > 0) {
            source.getIngredients()
                    .forEach(ingredient -> recipe.getIngredients().add(this.ingredientCommandToIngredientConverter.convert(ingredient)));
        }

        recipe.setImage(source.getImage());

        return recipe;
    }

}
