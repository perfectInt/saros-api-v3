package ru.saros.sarosapiv3.service;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.saros.sarosapiv3.domain.image.Image;
import ru.saros.sarosapiv3.domain.image.ImageService;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public class ImageServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ImageService imageService;

    String title, category, description;
    Integer price;
    MockMultipartFile[] files;

    @BeforeAll
    @WithMockUser(authorities = {"ADMINISTRATOR"})
    public void init() throws Exception {
        title = "Test title";
        category = "Test category";
        description = "Test description";
        price = 2500;
        files = new MockMultipartFile[2];
        files[0] = new MockMultipartFile("images[]", "image1.jpg", "multipart/form-data", "test data".getBytes());
        files[1] = new MockMultipartFile("images[]", "image2.jpg", "multipart/form-data", "test data".getBytes());
    }

    @Test
    @Order(1)
    @WithMockUser(authorities = {"ADMINISTRATOR"})
    public void initProduct() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/v3/products")
                        .file(files[0])
                        .file(files[1])
                        .param("title", title)
                        .param("category", category)
                        .param("description", description)
                        .param("price", String.valueOf(price))
        ).andExpect(status().isCreated()).andReturn();
    }

    @Test
    @Order(2)
    public void getImageTest() {
        Image image = imageService.getImageById(1L);
        assertEquals("image1.jpg", image.getOriginalFileName());
    }
}
