package io.github.nandandesai.insecure.dto.requests;

import io.github.nandandesai.insecure.validators.ValidImage;
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
