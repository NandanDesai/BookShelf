package io.github.nandandesai.insecure.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;

public class PdfConstraintValidator implements ConstraintValidator<ValidPdf, MultipartFile> {
    private Logger logger = LoggerFactory.getLogger(PdfConstraintValidator.class);

    @Override
    public boolean isValid(MultipartFile pdfFile, ConstraintValidatorContext context) {
//        try {
//
//            logger.info("It's a PDF file");
//            return true;
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
//        logger.info("It's not a PDF file");
//        return false;
        return true;
    }
}
