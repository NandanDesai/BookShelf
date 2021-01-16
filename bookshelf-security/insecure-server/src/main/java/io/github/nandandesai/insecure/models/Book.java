package io.github.nandandesai.insecure.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Book {
    private int id;

    private String title;

    private String description;

    private String pdfFileName;

    private String coverPhotoName;

    private Role role;
}
