package guru.springframework.controllers;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.repositories.CategoryRepository;
import guru.springframework.services.CategoryService;
import guru.springframework.services.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RecipeImageControllerTest {

    @Mock
    RecipeService recipeService;

    @Mock
    CategoryService categoryService;

    RecipeController controller;

    MockMvc mockMvc;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        controller = new RecipeController(recipeService, categoryService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    public void getImageUploadForm() throws Exception {
        mockMvc.perform(get("/recipe/1/image/upload"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("recipeId"));
    }

    @Test
    public void handleImagePost() throws Exception {
        final MockMultipartFile multipartFile = new MockMultipartFile("recipeImage",
                "testing.txt", "text/plain", "Spring Framework Guru".getBytes());

        mockMvc.perform(multipart("/recipe/1/image").file(multipartFile))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/recipe/1/show"));

        verify(recipeService, times(1)).saveRecipeImage(anyLong(), any());
    }

    @Test
    public void renderImageFromDB() throws Exception {
        // given
        RecipeCommand command = new RecipeCommand();
        command.setId(1L);

        String s = "fake image text";

        command.setImage(s.getBytes());

        when(recipeService.findCommandById(anyLong())).thenReturn(command);

        // when
        MockHttpServletResponse response = mockMvc.perform(get("/recipe/1/image"))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        byte[] responseBytes = response.getContentAsByteArray();

        assertEquals(s.getBytes().length, responseBytes.length);
    }

    @Test
    public void testGetImageNumberFormatException() throws Exception {

        mockMvc.perform(get("/recipe/dummy/image"))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("500-error"));

    }

}