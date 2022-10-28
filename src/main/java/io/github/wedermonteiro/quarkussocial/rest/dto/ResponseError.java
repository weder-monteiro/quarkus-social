package io.github.wedermonteiro.quarkussocial.rest.dto;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;

public class ResponseError {

    public static final int UNPROCESSABLE_ENTITY_STATUS = 442;
    
    private String message;
    private Collection<FieldError> errors;

    public ResponseError(String message, Collection<FieldError> errors) {
        this.message = message;
        this.errors = errors;
    }

    public static <T> ResponseError createFromValidation(Set<ConstraintViolation<T>> violations) {
        var message = "Validation Error";
        List<FieldError> erros = violations.stream()
            .map(v -> new FieldError(v.getPropertyPath().toString(), v.getMessage()))
            .collect(Collectors.toList());

        return new ResponseError(message, erros);
    }

    public Response withStatusCode(int code) {
        return Response.status(code).entity(this).build();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Collection<FieldError> getErrors() {
        return errors;
    }

    public void setErrors(Collection<FieldError> errors) {
        this.errors = errors;
    }

}