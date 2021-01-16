package io.github.nandandesai.insecure.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Role {
    private String name;

    private Integer value; //higher the value, more the privilege. By this logic, admin is supposed to
    // have the highest value
}
