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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.saros.sarosapiv3.api.exception.NewsNotFoundException;
import ru.saros.sarosapiv3.domain.news.News;
import ru.saros.sarosapiv3.domain.news.NewsResponse;
import ru.saros.sarosapiv3.domain.news.NewsService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import({NewsService.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NewsControllerIT {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    News news;
    Long id;

    @BeforeAll
    public void init() {
        news = new News();
        news.setTitle("Test title");
        news.setDescription("Test description");
    }

    @Test
    @Order(1)
    @WithMockUser(authorities = {"ADMINISTRATOR", "MODERATOR"})
    public void createNewsTest() throws Exception {
        RequestBuilder requestBuilderPost = MockMvcRequestBuilders.post("/api/v3/news/create")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(news));

        MvcResult mvcResult = mockMvc.perform(requestBuilderPost).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        news = mapper.readValue(response, News.class);

        assertNotNull(news);
        id = news.getId();
        assertNotNull(id);
    }

    @Test
    @Order(2)
    @WithAnonymousUser
    public void getCreatedNewsTest() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/news/" + id)
                .accept(MediaType.APPLICATION_JSON_VALUE);

        MvcResult mvcResult = mockMvc.perform(requestBuilderGet).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        NewsResponse newsResponse = mapper.readValue(response, NewsResponse.class);
        assertNotNull(newsResponse);
        assertEquals(id, newsResponse.getId());
        assertEquals(news.getTitle(), newsResponse.getTitle());
    }

    @Test
    @Order(3)
    @WithAnonymousUser
    public void getNewsByPagesTest() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/news")
                .accept(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilderGet)
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(1));

        requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/news?page=1")
                .accept(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilderGet)
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(0));
    }

    @Test
    @Order(4)
    @WithMockUser(authorities = {"ADMINISTRATOR"})
    public void updateNewsTest() throws Exception {
        news.setTitle("Another test title");

        RequestBuilder requestBuilderPut = MockMvcRequestBuilders.put("/api/v3/news/update")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(news));

        mockMvc.perform(requestBuilderPut);

        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/news")
                .accept(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilderGet)
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(1));

        requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/news/" + id)
                .accept(MediaType.APPLICATION_JSON_VALUE);

        MvcResult mvcResult = mockMvc.perform(requestBuilderGet).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        NewsResponse newsResponse = mapper.readValue(response, NewsResponse.class);
        assertNotNull(newsResponse);
        assertEquals(id, newsResponse.getId());
        assertEquals("Another test title", newsResponse.getTitle());
    }

    @Test
    @Order(5)
    @WithMockUser(authorities = {"ADMINISTRATOR"})
    public void deleteNewsTest() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/news/" + id)
                .accept(MediaType.APPLICATION_JSON_VALUE);

        RequestBuilder requestBuilderDelete = MockMvcRequestBuilders.delete("/api/v3/news/delete/" + id);

        mockMvc.perform(requestBuilderDelete);

        MvcResult mvcResult = mockMvc.perform(requestBuilderGet).andReturn();
        NewsNotFoundException exception = (NewsNotFoundException) mvcResult.getResolvedException();

        assertNotNull(exception);
        mockMvc.perform(requestBuilderGet).andExpect(status().isNotFound());
    }
}
