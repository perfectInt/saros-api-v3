package ru.saros.sarosapiv3.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.saros.sarosapiv3.api.exception.PasswordException;
import ru.saros.sarosapiv3.api.exception.UserAlreadyExistsException;
import ru.saros.sarosapiv3.api.exception.UserNotFoundException;
import ru.saros.sarosapiv3.api.security.config.JwtService;
import ru.saros.sarosapiv3.api.security.util.Role;
import ru.saros.sarosapiv3.domain.StatusResponse;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public User registerUser(RegistrationRequest registrationRequest) {
        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent())
            throw new UserAlreadyExistsException("User with " + registrationRequest.getEmail() + " email already exists!");
        if (!Objects.equals(registrationRequest.getPassword(), registrationRequest.getPasswordConfirmation()))
            throw new PasswordException("Passwords are not matching!");
        User user = userMapper.toEntity(registrationRequest);
        user.setPassword(encoder.encode(registrationRequest.getPassword()));
        user.setRole(Role.USER);
        if (userRepository.findAll().isEmpty()) {
            user.setRole(Role.ADMINISTRATOR);
            user.setNonLocked(true);
        }
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public StatusResponse deleteUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        if (user.getRole() == Role.ADMINISTRATOR) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        userRepository.deleteById(user.getId());
        return new StatusResponse(user.getEmail() + " was deleted successfully!");
    }

    public User changeUserRole(ChangeUserRoleRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(UserNotFoundException::new);
        if (Objects.equals(request.getRole(), Role.ADMINISTRATOR.name()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (Objects.equals(user.getRole().name(), request.getRole()))
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        user.setRole(Role.valueOf(request.getRole()));
        return userRepository.save(user);
    }

    public String login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(UserNotFoundException::new);
        return jwtService.generateToken(user);
    }
//
//    public UserLockResponse changerUserAccess(UserLockRequest request) {
//        User user = userRepository.findUserByUsername(request.getUsername()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//        if (user.getRole() == Role.ADMINISTRATOR) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
//        user.setNonLocked(!Objects.equals(request.getOperation(), Operation.LOCK.name()));
//        userRepository.save(user);
//        return new UserLockResponse("User " + user.getUsername() + " was " + (Objects.equals(request.getOperation(), "LOCK") ? "locked" : "unlocked"));
//    }
}
