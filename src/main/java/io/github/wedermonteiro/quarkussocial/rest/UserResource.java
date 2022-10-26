package io.github.wedermonteiro.quarkussocial.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import io.github.wedermonteiro.quarkussocial.rest.dto.CreateUserRequest;

@Path("/users")
public class UserResource {
    
    @POST
    public Response createUser(CreateUserRequest userRequest) {
        return Response.ok().build();
    }

}