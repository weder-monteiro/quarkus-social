package io.github.wedermonteiro.quarkussocial.rest.dto;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseError {

    public static final int UNPROCESSABLE_ENTITY_STATUS = 442;
    
    private String message;
    private Collection<FieldError> errors;

    public static <T> ResponseError createFromValidation(Set<ConstraintViolation<T>> violations) {
        List<FieldError> errorList = violations.stream()
            .map(v -> new FieldError(v.getPropertyPath().toString(), v.getMessage()))
            .collect(Collectors.toList());

        return new ResponseError("Validation Error", errorList);
    }

    public Response withStatusCode(int code) {
        return Response.status(code).entity(this).build();
    }

}
