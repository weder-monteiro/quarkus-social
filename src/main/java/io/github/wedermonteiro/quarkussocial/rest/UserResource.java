package io.github.wedermonteiro.quarkussocial.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;
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

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserRepository userRepository;
    
    @Inject
    public UserResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    };

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userRequest) {
        User user = new User();
        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());

        userRepository.persist(user);

        return Response.ok(user).build();
    }

    @GET
    public Response listAllUsers() {
        return Response.ok(userRepository.findAll().list()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        User user = userRepository.findById(id);

        if(user != null) {
            userRepository.delete(user);

            return Response.ok().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userData) {
        User user = userRepository.findById(id);

        if(user != null) {
            user.setName(userData.getName());
            user.setAge(userData.getAge());

            return Response.ok().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}