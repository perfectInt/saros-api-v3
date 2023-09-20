package ru.saros.sarosapiv3.domain.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @Pattern(regexp = "[a-zA-Z0-9-.]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+")
    private String email;

    @NotEmpty
    private String password;
}

