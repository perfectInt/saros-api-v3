package ru.saros.sarosapiv3.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Auth controller", description = "Endpoints for log in or registry")
public class AuthController {

    private final UserService userService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(tags = "Auth controller", description = "Registry endpoint. In the future it maybe will be closed endpoint.")
    public User registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        return userService.registerUser(registrationRequest);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(tags = "Auth controller", description = "Log in endpoint")
    public String login(@Valid @RequestBody LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }
}
