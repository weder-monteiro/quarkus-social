package io.github.wedermonteiro.quarkussocial.rest;

import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.github.wedermonteiro.quarkussocial.rest.domain.model.Post;
import io.github.wedermonteiro.quarkussocial.rest.domain.model.User;
import io.github.wedermonteiro.quarkussocial.rest.domain.repository.FollowerRepository;
import io.github.wedermonteiro.quarkussocial.rest.domain.repository.PostRepository;
import io.github.wedermonteiro.quarkussocial.rest.domain.repository.UserRepository;
import io.github.wedermonteiro.quarkussocial.rest.dto.CreatePostRequest;
import io.github.wedermonteiro.quarkussocial.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private UserRepository userRepository;
    private PostRepository postRepository;
    private FollowerRepository followerRepository;
    
    @Inject
    public void UserResource(
        UserRepository userRepository, 
        PostRepository postRepository,
        FollowerRepository followerRepository
    ) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
    };

    @POST
    @Transactional
    public Response save(
        @PathParam("userId") Long userId,
        CreatePostRequest createPostRequest
    ) {
        User user = userRepository.findById(userId);

        if(user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setText(createPostRequest.getText());
        post.setUser(user);
        
        postRepository.persist(post);

        return Response.status(Status.CREATED).build(); 
    }

    @GET
    public Response list(
        @PathParam("userId") Long userId, 
        @HeaderParam("followerId") Long followerId
    ) {
        User user = userRepository.findById(userId);

        if(user == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        if(followerId == null) {
            return Response
                .status(Status.BAD_REQUEST)
                .entity("You forgot the header followerId")
                .build();
        }

        User follower = userRepository.findById(followerId);

        if(follower == null) {
            return Response
                .status(Status.BAD_REQUEST)
                .entity("Inexistent followerId")
                .build();
        }

        boolean follows = followerRepository.follows(follower, user);

        if(!follows) {
            return Response
                .status(Status.FORBIDDEN)
                .entity("You can´t see these posts")
                .build();
        }

        PanacheQuery<Post> query = postRepository.find(
            "user", 
            Sort.by("dateTime", Direction.Descending), 
            user
        );

        var postResponseList = query.list()
            .stream()
            .map(PostResponse::fromEntity)
            .collect(Collectors.toList());

        return Response.ok(postResponseList).build();
    }
    
}
