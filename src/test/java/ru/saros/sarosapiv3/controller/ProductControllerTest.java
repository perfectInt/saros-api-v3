package ru.saros.sarosapiv3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.saros.sarosapiv3.api.exception.ProductNotFoundException;
import ru.saros.sarosapiv3.domain.product.Product;
import ru.saros.sarosapiv3.domain.product.ProductResponse;
import ru.saros.sarosapiv3.domain.product.ProductService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import({ProductService.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductControllerTest {
    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    String title, category, description;
    Integer price;
    MockMultipartFile[] files;

    Long id;
    Product product;

    @BeforeAll
    public void init() {
        title = "Test title";
        category = "Test category";
        description = "Test description";
        price = 2500;
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        files = new MockMultipartFile[2];
        files[0] = new MockMultipartFile("images[]", "image1.jpg", "multipart/form-data", "test data".getBytes());
        files[1] = new MockMultipartFile("images[]", "image2.jpg", "multipart/form-data", "test data".getBytes());
    }

    @Test
    @Order(1)
    @WithMockUser(authorities = {"ADMINISTRATOR"})
    @DisplayName("Checking if products are getting created correctly")
    public void createProductTest() throws Exception {

        for (int i = 0; i < 20; i++) {
            mockMvc.perform(
                    MockMvcRequestBuilders.multipart("/api/v3/products")
                            .file(files[0])
                            .file(files[1])
                            .param("title", title)
                            .param("category", category)
                            .param("description", description)
                            .param("price", String.valueOf(price))
            );
        }

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/v3/products")
                        .file(files[0])
                        .file(files[1])
                        .param("title", title)
                        .param("category", category)
                        .param("description", description)
                        .param("price", String.valueOf(price))
        ).andExpect(status().isCreated()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertNotNull(response);
        System.out.println(response);

        product = mapper.readValue(response, Product.class);
        assertNotNull(product);
        id = product.getId();
        assertEquals(title, product.getTitle());
    }

    @Test
    @Order(2)
    @WithAnonymousUser
    @DisplayName("Checking if users can access products list")
    public void getNewProductTest() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/products/" + id)
                .accept(MediaType.APPLICATION_JSON_VALUE);

        MvcResult mvcResult = mockMvc.perform(requestBuilderGet).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        ProductResponse productView = mapper.readValue(response, ProductResponse.class);

        assertNotNull(response);
        assertEquals("Test title", productView.getTitle());
        assertEquals(product.getImages().get(0).getId(), productView.getImagesIds().get(0));
        System.out.println(productView);
    }

    @Test
    @Order(3)
    @WithAnonymousUser
    @DisplayName("Checking if users can access products list by pages")
    public void getAllProductsByPageTest() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/products")
                .accept(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilderGet)
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(20));

        requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/products?page=1")
                .accept(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilderGet)
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(1));
    }

    @Test
    @Order(4)
    @WithAnonymousUser
    @DisplayName("Checking if users can access products list by categories")
    public void getAllProductsByPageAndCategoryTest() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/products?category=" + category)
                .accept(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilderGet)
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(20));

        requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/products?category=another+Category")
                .accept(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilderGet)
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(0));
    }

    @Test
    @Order(5)
    @WithAnonymousUser
    @DisplayName("Checking that user without grants cannot get access to admin's endpoints")
    public void checkAccessTest() throws Exception {
        RequestBuilder requestBuilderDelete = MockMvcRequestBuilders
                .delete("/api/v1/products/delete/" + id);

        mockMvc.perform(requestBuilderDelete).andExpect(status().isForbidden());
    }

    @Test
    @Order(6)
    @WithMockUser(authorities = {"ADMINISTRATOR"})
    @DisplayName("Checking if product is getting deleted")
    public void deleteProductTest() throws Exception {
        RequestBuilder requestBuilderDelete = MockMvcRequestBuilders
                .delete("/api/v3/products/delete/" + id);

        mockMvc.perform(requestBuilderDelete).andExpect(status().isNoContent());

        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/products/" + id)
                .accept(MediaType.APPLICATION_JSON_VALUE);

        MvcResult mvcResult = mockMvc.perform(requestBuilderGet).andReturn();
        ProductNotFoundException exception = (ProductNotFoundException) mvcResult.getResolvedException();

        assertNotNull(exception);
        mockMvc.perform(requestBuilderGet).andExpect(status().isNotFound());
    }
}
