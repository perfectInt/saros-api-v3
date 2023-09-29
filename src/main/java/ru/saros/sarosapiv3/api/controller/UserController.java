package ru.saros.sarosapiv3.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.saros.sarosapiv3.domain.StatusResponse;
import ru.saros.sarosapiv3.domain.user.ChangeUserRoleRequest;
import ru.saros.sarosapiv3.domain.user.User;
import ru.saros.sarosapiv3.domain.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v3/users")
@CrossOrigin
@Tag(name = "User controller", description = "Endpoints for user's manipulations")
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'MODERATOR')")
    @Operation(tags = "User controller", description = "Get a list of users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'MODERATOR')")
    @Operation(tags = "User controller", description = "Get a concrete user by its id")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/role")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @Operation(tags = "User controller", description = "Change someone's user role")
    public User changeRole(@Valid @RequestBody ChangeUserRoleRequest request) {
        return userService.changeUserRole(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    @Operation(tags = "User controller", description = "Delete user by id")
    public StatusResponse deleteUser(@PathVariable Long id) {
        return userService.deleteUserById(id);
    }
}
