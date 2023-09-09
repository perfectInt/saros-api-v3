package ru.saros.sarosapiv3.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.saros.sarosapiv3.domain.user.LoginRequest;
import ru.saros.sarosapiv3.domain.user.RegistrationRequest;
import ru.saros.sarosapiv3.domain.user.User;
import ru.saros.sarosapiv3.domain.user.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/auth")
@CrossOrigin
public class AuthController {

    private final UserService userService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUser(@RequestBody RegistrationRequest registrationRequest) {
        return userService.registerUser(registrationRequest);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public String login(@RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }
}
