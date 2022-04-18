package guru.springframework.controllers;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.RecipeCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.ResourceNotFoundException;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Comparator;

@Slf4j
@Controller
public class IngredientController {

    private final IngredientService ingredientService;
    private final RecipeService recipeService;
    private final UnitOfMeasureService unitOfMeasureService;

    public IngredientController(IngredientService ingredientService,
                                RecipeService recipeService,
                                UnitOfMeasureService unitOfMeasureService) {
        this.ingredientService = ingredientService;
        this.recipeService = recipeService;
        this.unitOfMeasureService = unitOfMeasureService;
    }

    @GetMapping("/recipe/{recipeId}/ingredients")
    public String listIngredients(@PathVariable Long recipeId, Model model) {
        // use command object to avoid lazy load errors in Thymeleaf.
        final RecipeCommand recipeCommand = this.recipeService.findCommandById(recipeId);

        // sort Ingredient by Description
        recipeCommand.getIngredients().sort(Comparator.comparing(IngredientCommand::getDescription));

        model.addAttribute("recipe", recipeCommand);

        return "recipe/ingredients/list";
    }

    @GetMapping("recipe/{recipeId}/ingredients/{ingredientId}/show")
    public String showRecipeIngredient(@PathVariable Long recipeId,
                                       @PathVariable Long ingredientId,
                                       Model model) {
        model.addAttribute("ingredient", this.ingredientService.findByRecipeIdAndIngredientId(recipeId, ingredientId));
        return "recipe/ingredients/show";
    }

    @GetMapping("recipe/{recipeId}/ingredients/new")
    public String newRecipe(@PathVariable Long recipeId, Model model) {
        // make sure we have a Recipe for given ID
        final Recipe recipe = this.recipeService.findById(recipeId);

        if (recipe == null) {
            throw new ResourceNotFoundException("Recipe with ID [" + recipeId + "] Not Found");
        }

        // need to return Parent ID for hidden form property
        final IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setRecipeId(recipeId);

        // init uom
        ingredientCommand.setUom(new UnitOfMeasureCommand());

        model.addAttribute("ingredient", ingredientCommand);
        model.addAttribute("uomList", this.unitOfMeasureService.selectAllUnitOfMeasureCommands());

        return "recipe/ingredients/form";
    }

    @GetMapping("recipe/{recipeId}/ingredients/{ingredientId}/update")
    public String updateRecipeIngredient(@PathVariable Long recipeId,
                                         @PathVariable Long ingredientId, Model model) {

        model.addAttribute("ingredient", this.ingredientService.findByRecipeIdAndIngredientId(recipeId, ingredientId));

        model.addAttribute("uomList", this.unitOfMeasureService.selectAllUnitOfMeasureCommands());

        return "recipe/ingredients/form";
    }

    @PostMapping("recipe/{recipeId}/ingredients")
    public String saveOrUpdate(@ModelAttribute IngredientCommand command) {
        final IngredientCommand savedCommand = this.ingredientService.saveIngredientCommand(command);
        return "redirect:/recipe/" + savedCommand.getRecipeId() + "/ingredients/";
    }

    @GetMapping("recipe/{recipeId}/ingredients/{ingredientId}/delete")
    public String deleteIngredient(@PathVariable Long recipeId, @PathVariable Long ingredientId) {
        this.ingredientService.deleteById(recipeId, ingredientId);
        return "redirect:/recipe/" + recipeId + "/ingredients";
    }

}
