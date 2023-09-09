package ru.saros.sarosapiv3.api.controller;

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
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'MODERATOR')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'MODERATOR')")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/role")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public User changeRole(@RequestBody ChangeUserRoleRequest request) {
        return userService.changeUserRole(request);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public StatusResponse deleteUser(@PathVariable String email) {
        return userService.deleteUserByEmail(email);
    }
}
