package ru.saros.sarosapiv3.domain.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegistrationRequest registrationRequest) {
        User user = new User();
        user.setName(registrationRequest.getName());
        user.setEmail(registrationRequest.getEmail());
        return user;
    }
}
