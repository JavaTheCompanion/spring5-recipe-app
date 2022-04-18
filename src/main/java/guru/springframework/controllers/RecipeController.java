package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.domain.Difficulty;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.ResourceNotFoundException;
import guru.springframework.services.CategoryService;
import guru.springframework.services.RecipeService;
import guru.springframework.utils.ImageLoadUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class RecipeController {

    private static final String RECIPE_FORM_URL = "recipe/form";

    @Value("classpath:/static/images/upload.png")
    private Resource uploadImageFile;

    private final RecipeService recipeService;
    private final CategoryService categoryService;

    public RecipeController(RecipeService recipeService, CategoryService categoryService) {
        this.recipeService = recipeService;
        this.categoryService = categoryService;
    }

    @GetMapping("/recipe/{id}/show")
    public String showById(@PathVariable Long id, Model model) {
        final Recipe recipe = this.recipeService.findById(id);

        model.addAttribute("recipe", recipe);

        return "recipe/show";
    }

    @GetMapping("recipe/new")
    public String newRecipe(Model model) {
        model.addAttribute("recipe", new RecipeCommand());

        model.addAttribute("allCategories", this.categoryService.selectAllCategories());
        model.addAttribute("difficultyValues", this.selectAllDifficultyValues());

        return RECIPE_FORM_URL;
    }

    @GetMapping("recipe/{id}/update")
    public String updateRecipe(@PathVariable Long id, Model model) {
        final RecipeCommand recipeCommand = this.recipeService.findCommandById(id);

        model.addAttribute("recipe", recipeCommand);
        model.addAttribute("recipeIngredients", recipeCommand.getIngredients());
        model.addAttribute("difficultyValues", this.selectAllDifficultyValues());
        model.addAttribute("allCategories", this.categoryService.selectAllCategories());

        System.out.println("hello " + recipeCommand.getCategories());

        return RECIPE_FORM_URL;
    }

    @PostMapping("recipe")
    public String saveOrUpdate(@Valid @ModelAttribute("recipe") RecipeCommand command,
                               BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> {
                log.debug(objectError.toString());
            });

            if (command.getId() != null) {
                final RecipeCommand recipeCommand = this.recipeService.findCommandById(command.getId());
                model.addAttribute("recipeIngredients", recipeCommand.getIngredients());
            }

            model.addAttribute("difficultyValues", this.selectAllDifficultyValues());
            model.addAttribute("allCategories", this.categoryService.selectAllCategories());

            return RECIPE_FORM_URL;
        }

        if (command.getId() != null) {
            final RecipeCommand recipeCommand = this.recipeService.findCommandById(command.getId());

            if (recipeCommand.getImage() != null) {
                command.setImage(recipeCommand.getImage());
            }
        }

        final RecipeCommand savedCommand = this.recipeService.saveRecipeCommand(command);

        return "redirect:/recipe/" + savedCommand.getId() + "/show";
    }

    @GetMapping("recipe/{id}/delete")
    public String deleteById(@PathVariable Long id) {
        this.recipeService.deleteById(id);
        return "redirect:/";
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("recipe/{id}/image/upload")
    public String showImageUploadForm(@PathVariable Long id, Model model) {
        model.addAttribute("recipeId", id);
        return "recipe/image-upload-form";
    }

    @PostMapping("recipe/{id}/image")
    public String uploadRecipeImage(@PathVariable Long id, @RequestParam("recipeImage") MultipartFile file) {
        this.recipeService.saveRecipeImage(id, file);
        return "redirect:/recipe/" + id + "/show";
    }

    @GetMapping("recipe/{id}/image")
    public void renderImageFromDB(@PathVariable Long id, HttpServletResponse response) throws IOException {
        final RecipeCommand recipeCommand = this.recipeService.findCommandById(id);

        if (recipeCommand.getImage() == null) {
            recipeCommand.setImage(ImageLoadUtils.loadImage(this.uploadImageFile));
        }

        response.setContentType("image/jpeg");
        final InputStream is = new ByteArrayInputStream(recipeCommand.getImage());
        IOUtils.copy(is, response.getOutputStream());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(Exception exception) {
        final ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("404-error");
        modelAndView.addObject("exception", exception);

        return modelAndView;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<String> selectAllDifficultyValues() {
        return Arrays.stream(Difficulty.values()).map(Enum::name).collect(Collectors.toList());
    }

}