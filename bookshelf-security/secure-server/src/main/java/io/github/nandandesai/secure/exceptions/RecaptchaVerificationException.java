package io.github.nandandesai.secure.exceptions;

import io.github.nandandesai.secure.dto.responses.ErrorResponse;
import io.github.nandandesai.secure.dto.responses.Response;
import io.github.nandandesai.secure.dto.responses.ResponseType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class RecaptchaVerificationException extends Exception{
    private final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public RecaptchaVerificationException(String message) {
        super(message);
    }

    ResponseEntity<Response> getResponseEntity() {
        List<String> details = new ArrayList<String>();
        details.add(super.getMessage());
        ErrorResponse errorResponse = new ErrorResponse().setMessage("reCAPTCHA verification failed")
                .setDetails(details);
        Response<ErrorResponse> response = new Response<>();
        response.setPayload(errorResponse);
        response.setType(ResponseType.RECAPTCHA_FAILED);
        return new ResponseEntity<Response>(response, httpStatus);
    }
}
