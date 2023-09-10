package ru.saros.sarosapiv3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.saros.sarosapiv3.domain.user.LoginRequest;
import ru.saros.sarosapiv3.domain.user.RegistrationRequest;
import ru.saros.sarosapiv3.domain.user.UserService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import({UserService.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerTest {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    RegistrationRequest registrationRequest;
    LoginRequest loginRequest;

    @BeforeAll
    public void init() {
        registrationRequest = new RegistrationRequest(
                "la@mail.ru",
                "Jotaro Kujo",
                "12345678",
                "12345678"
        );
        loginRequest = new LoginRequest(
                "la@mail.ru",
                "12345678"
        );
    }

    @Test
    @Order(1)
    @DisplayName("Registration test")
    public void registrationTest() throws Exception {

        RequestBuilder requestBuilderPost = MockMvcRequestBuilders.post("/api/v3/auth/registration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(registrationRequest));

        MvcResult mvcResult = mockMvc.perform(requestBuilderPost)
                .andExpect(status().isCreated())
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertNotNull(response);
        assertTrue(response.length() > 20);
    }

    @Test
    @Order(2)
    @DisplayName("Registration with the same data")
    public void secondRegistrationTest() throws Exception {
        RequestBuilder requestBuilderPost = MockMvcRequestBuilders.post("/api/v3/auth/registration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(registrationRequest));

        mockMvc.perform(requestBuilderPost).andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    @DisplayName("Registration if password and passwordConfirmation are not equal")
    public void passwordRegistrationTest() throws Exception {
        registrationRequest.setPasswordConfirmation("123456789");
        RequestBuilder requestBuilderPost = MockMvcRequestBuilders.post("/api/v3/auth/registration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(registrationRequest));

        mockMvc.perform(requestBuilderPost).andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    @Disabled
    @DisplayName("Registration with incorrect email format")
    public void incorrectEmailFormatRegistrationTest() throws Exception {
        registrationRequest.setPasswordConfirmation("12345678");
        registrationRequest.setEmail("notemail");

        RequestBuilder requestBuilderPost = MockMvcRequestBuilders.post("/api/v3/auth/registration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(registrationRequest));

        mockMvc.perform(requestBuilderPost).andExpect(status().is4xxClientError());
    }

    @Test
    @Order(5)
    @DisplayName("Log in test")
    public void logInTest() throws Exception {
        RequestBuilder requestBuilderPost = MockMvcRequestBuilders.post("/api/v3/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(loginRequest));

        MvcResult mvcResult = mockMvc.perform(requestBuilderPost)
                .andExpect(status().isOk())
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertNotNull(response);
        assertTrue(response.length() > 20);
    }

    @Test
    @Order(6)
    @DisplayName("Attempt to log in throw not existing user")
    public void incorrectEmailLogInTest() throws Exception {
        loginRequest.setEmail("l@mail.ru");

        RequestBuilder requestBuilderPost = MockMvcRequestBuilders.post("/api/v3/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(loginRequest));

        mockMvc.perform(requestBuilderPost).andExpect(status().isForbidden());
    }

    @Test
    @Order(7)
    @DisplayName("Attempt to log in with incorrect password")
    public void incorrectPasswordLogInTest() throws Exception {
        loginRequest.setEmail("la@mail.ru");
        loginRequest.setPassword("123455678");

        RequestBuilder requestBuilderPost = MockMvcRequestBuilders.post("/api/v3/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(loginRequest));

        mockMvc.perform(requestBuilderPost).andExpect(status().isForbidden());
    }
}
