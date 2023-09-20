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
import ru.saros.sarosapiv3.domain.post.PostService;
import ru.saros.sarosapiv3.domain.post.PostView;
import ru.saros.sarosapiv3.domain.product.ProductResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import({PostService.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostControllerTest {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    String title, text;
    MockMultipartFile file;

    PostView postView;
    Long id;

    @BeforeAll
    public void init() {
        title = "Test title";
        text = "test text";
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        file = new MockMultipartFile("image", "image1.jpg", "multipart/form-data", "test data".getBytes());
    }

    @Test
    @Order(1)
    @WithMockUser(authorities = {"ADMINISTRATOR"})
    @DisplayName("Checking if posts are getting created correctly")
    public void createProductTest() throws Exception {

        for (int i = 0; i < 20; i++) {
            mockMvc.perform(
                    MockMvcRequestBuilders.multipart("/api/v3/posts")
                            .file(file)
                            .param("title", title)
                            .param("text", text)
            );
        }

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/v3/posts")
                        .file(file)
                        .param("title", title)
                        .param("text", text)
        ).andExpect(status().isCreated()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertNotNull(response);
        System.out.println(response);

        postView = mapper.readValue(response, PostView.class);
        assertNotNull(postView);
        id = postView.getId();
        assertEquals(title, postView.getTitle());
    }

    @Test
    @Order(2)
    @WithAnonymousUser
    @DisplayName("Checking if users can access concrete post")
    public void getNewProductTest() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/posts/" + id)
                .accept(MediaType.APPLICATION_JSON_VALUE);

        MvcResult mvcResult = mockMvc.perform(requestBuilderGet).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        PostView postView1 = mapper.readValue(response, PostView.class);

        assertNotNull(response);
        assertEquals("Test title", postView1.getTitle());
        assertEquals(postView.getImageId(), postView1.getImageId());
        System.out.println(postView1);
    }

    @Test
    @Order(3)
    @WithAnonymousUser
    @DisplayName("Checking if users can access all posts")
    public void getAllProductsByPageTest() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/posts")
                .accept(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilderGet)
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(21));
    }

    @Test
    @Order(5)
    @WithAnonymousUser
    @DisplayName("Checking that user without grants cannot get access to admin's endpoints")
    public void checkAccessTest() throws Exception {
        RequestBuilder requestBuilderDelete = MockMvcRequestBuilders
                .put("/api/v1/posts");

        mockMvc.perform(requestBuilderDelete).andExpect(status().isForbidden());
    }
}
