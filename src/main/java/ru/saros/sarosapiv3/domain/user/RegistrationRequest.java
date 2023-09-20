package ru.saros.sarosapiv3.domain.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

    @Pattern(regexp = "[a-zA-Z0-9-.]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+")
    private String email;

    @NotEmpty
    private String name;

    @Size(min = 8)
    private String password;

    private String passwordConfirmation;
}

