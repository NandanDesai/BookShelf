package io.github.nandandesai.secure.dto.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginRequest {
    @NotNull(message = "Email cannot be Null")
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @NotNull(message = "Password cannot be Null")
    @NotEmpty(message = "Password cannot be empty")
    private String password;
}
