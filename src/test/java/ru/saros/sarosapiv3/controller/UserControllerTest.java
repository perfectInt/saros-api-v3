package ru.saros.sarosapiv3.controller;

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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.saros.sarosapiv3.api.exception.ProductNotFoundException;
import ru.saros.sarosapiv3.api.exception.UserNotFoundException;
import ru.saros.sarosapiv3.api.security.util.Role;
import ru.saros.sarosapiv3.domain.user.ChangeUserRoleRequest;
import ru.saros.sarosapiv3.domain.user.RegistrationRequest;
import ru.saros.sarosapiv3.domain.user.User;
import ru.saros.sarosapiv3.domain.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import({UserService.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    RegistrationRequest registrationRequest;

    ChangeUserRoleRequest request;

    @BeforeAll
    public void init() throws Exception {
        request = new ChangeUserRoleRequest(
                "test2@mail.ru",
                Role.MODERATOR.getAuthority()
        );
        registrationRequest = new RegistrationRequest(
                "la@mail.ru",
                "Jotaro Kujo",
                "12345678",
                "12345678"
        );

        for (int i = 1; i < 10; i++) {
            registrationRequest.setEmail("test" + i + "@mail.ru");
            RequestBuilder requestBuilderPost = MockMvcRequestBuilders.post("/api/v3/auth/registration")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .content(mapper.writeValueAsString(registrationRequest));
            mockMvc.perform(requestBuilderPost);
        }
    }

    @Test
    @Order(1)
    @WithAnonymousUser
    @DisplayName("Checking user without grants access to moderator's and admin's endpoints")
    public void checkAccessTest() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/users")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilderGet).andExpect(status().isForbidden());

        requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/users/" + 12)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilderGet).andExpect(status().isForbidden());
    }

    @Test
    @Order(2)
    @WithMockUser(authorities = {"ADMINISTRATOR", "MODERATOR"})
    @DisplayName("Checking if admin or moderator can get a list of users")
    public void getAllUsersTest() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/users")
                .accept(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilderGet)
                .andExpect(status().isOk())
                .andExpect(jsonPath("length()").value(9));
    }

    @Test
    @Order(3)
    @WithMockUser(authorities = {"ADMINISTRATOR", "MODERATOR"})
    @DisplayName("Checking if admin or moderator can get a concrete user")
    public void getUserTest() throws Exception {
        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/users/" + 1)
                .accept(MediaType.APPLICATION_JSON_VALUE);

        MvcResult mvcResult = mockMvc.perform(requestBuilderGet).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();

        User user = mapper.readValue(response, User.class);
        assertEquals("test1@mail.ru", user.getEmail());
    }

    @Test
    @Order(4)
    @WithMockUser(authorities = {"MODERATOR", "USER"})
    @DisplayName("Checking if user with default and moderator's grants cannot change some user's role and delete him")
    public void checkAccessForUpdateOrDeleteTest() throws Exception {
        RequestBuilder requestBuilderPut = MockMvcRequestBuilders.put("/api/v3/users/role")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request));
        mockMvc.perform(requestBuilderPut).andExpect(status().isForbidden());

        RequestBuilder requestBuilderDelete = MockMvcRequestBuilders.delete("/api/v3/users/2");
        mockMvc.perform(requestBuilderDelete).andExpect(status().isForbidden());
    }

    @Test
    @Order(5)
    @WithMockUser(authorities = {"ADMINISTRATOR"})
    @DisplayName("Checking if user gets updated and deleted by admin")
    public void updateAndDeleteUserByAdminTest() throws Exception {
        RequestBuilder requestBuilderPut = MockMvcRequestBuilders.put("/api/v3/users/role")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request));
        MvcResult mvcResult = mockMvc.perform(requestBuilderPut).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        User user = mapper.readValue(response, User.class);
        assertEquals(Role.MODERATOR, user.getRole());

        RequestBuilder requestBuilderDelete = MockMvcRequestBuilders.delete("/api/v3/users/2");
        mockMvc.perform(requestBuilderDelete).andExpect(status().isNoContent());

        RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get("/api/v3/users/" + 2)
                .accept(MediaType.APPLICATION_JSON_VALUE);
        mvcResult = mockMvc.perform(requestBuilderGet).andReturn();
        UserNotFoundException exception = (UserNotFoundException) mvcResult.getResolvedException();
        assertNotNull(exception);
        mockMvc.perform(requestBuilderGet).andExpect(status().isNotFound());
    }
}
