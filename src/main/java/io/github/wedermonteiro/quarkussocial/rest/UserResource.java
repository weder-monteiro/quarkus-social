package io.github.wedermonteiro.quarkussocial.rest;

import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.github.wedermonteiro.quarkussocial.rest.domain.model.User;
import io.github.wedermonteiro.quarkussocial.rest.domain.repository.UserRepository;
import io.github.wedermonteiro.quarkussocial.rest.dto.CreateUserRequest;
import io.github.wedermonteiro.quarkussocial.rest.dto.ResponseError;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserRepository repository;
    private Validator validator;
    
    @Inject
    public UserResource(UserRepository userRepository, Validator validator) {
        this.repository = userRepository;
        this.validator = validator;
    };

    @POST
    @Transactional
    public Response save(CreateUserRequest userRequest) {
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);

        if(!violations.isEmpty()) {
            return ResponseError
                    .createFromValidation(violations)
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = new User();
        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());

        repository.persist(user);

        return Response.status(Response.Status.CREATED.getStatusCode())
                .entity(user)
                .build();
    }

    @GET
    public Response listAll() {
        return Response.ok(repository.findAll().list()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        User user = repository.findById(id);

        if(user != null) {
            repository.delete(user);

            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, CreateUserRequest userData) {
        User user = repository.findById(id);

        if(user != null) {
            user.setName(userData.getName());
            user.setAge(userData.getAge());

            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}