package io.github.nandandesai.insecure.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;

public class ImageConstraintValidator implements ConstraintValidator<ValidImage, MultipartFile> {
    private Logger logger = LoggerFactory.getLogger(ImageConstraintValidator.class);

    @Override
    public boolean isValid(MultipartFile image, ConstraintValidatorContext context) {
//        try {
//
//            //maybe validate height and width here
//        } catch (IOException e) {
//            context.buildConstraintViolationWithTemplate("File is not an image")
//                    .addConstraintViolation()
//                    .disableDefaultConstraintViolation();
//            return false;
//        }
        return true;
    }
}
