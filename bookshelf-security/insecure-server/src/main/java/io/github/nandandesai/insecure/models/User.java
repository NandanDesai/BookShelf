package io.github.nandandesai.insecure.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class User {
    private int id;

    private String email;

    private String password;

    private String fullName;

    private String profilePicName;

    private LocalDateTime lastLogin;

    private Role role;
}
