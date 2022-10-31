package io.github.wedermonteiro.quarkussocial.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.github.wedermonteiro.quarkussocial.rest.domain.model.Follower;
import io.github.wedermonteiro.quarkussocial.rest.domain.model.User;
import io.github.wedermonteiro.quarkussocial.rest.domain.repository.FollowerRepository;
import io.github.wedermonteiro.quarkussocial.rest.domain.repository.UserRepository;
import io.github.wedermonteiro.quarkussocial.rest.dto.FollowerRequest;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private FollowerRepository followerRepository;
    private UserRepository userRepository;

    @Inject
    public FollowerResource(
        FollowerRepository followerRepository,
        UserRepository userRepository
    ) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest request) {
        User user = userRepository.findById(userId);

        if(user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        User follower = userRepository.findById(request.getFollowerId());

        if(!followerRepository.follows(follower, user)) {
            var entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);
    
            followerRepository.persist(entity);
        };

        return Response.status(Status.NO_CONTENT).build();
    }

}