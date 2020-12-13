package io.github.nandandesai.secure.dto.requests;

import io.github.nandandesai.secure.validators.ValidImage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class AddUserPhotoRequest {
    private Integer id;

    @ValidImage
    private MultipartFile photo;
}
