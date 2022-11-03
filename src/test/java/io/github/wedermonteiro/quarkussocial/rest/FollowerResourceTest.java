package io.github.wedermonteiro.quarkussocial.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response.Status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.wedermonteiro.quarkussocial.rest.domain.model.Follower;
import io.github.wedermonteiro.quarkussocial.rest.domain.model.User;
import io.github.wedermonteiro.quarkussocial.rest.domain.repository.FollowerRepository;
import io.github.wedermonteiro.quarkussocial.rest.domain.repository.UserRepository;
import io.github.wedermonteiro.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    public void setUp() {
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        var follower = new User();
        follower.setAge(31);
        follower.setName("Cicrano");
        userRepository.persist(follower);
        followerId = follower.getId();

        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
    }

    @Test
    @DisplayName("Should return 409 when followerId is equal to UserId")
    public void sameUserAsFollowerTest() {
        var follower = new FollowerRequest();
        follower.setFollowerId(userId);

        given()
            .contentType(ContentType.JSON)
            .body(follower)
            .pathParam("userId", userId)
        .when()
            .put()
        .then()
            .statusCode(Status.CONFLICT.getStatusCode())
            .body(Matchers.is("You can´t follow yourself"));
    }

    @Test
    @DisplayName("Should return 404 on follow a user when UserId doesn't exist")
    public void userNotFoundWhenTryingToFollowTest() {
        var inexistentUserId = 999;
        var follower = new FollowerRequest();
        follower.setFollowerId(userId);

        given()
            .contentType(ContentType.JSON)
            .body(follower)
            .pathParam("userId", inexistentUserId)
        .when()
            .put()
        .then()
            .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should follow a user")
    public void followUserTest() {
        var follower = new FollowerRequest();
        follower.setFollowerId(followerId);

        given()
            .contentType(ContentType.JSON)
            .body(follower)
            .pathParam("userId", userId)
        .when()
            .put()
        .then()
            .statusCode(Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 on list user followers and UserId doesn't exist")
    public void userNotFoundWhenListingFollowersTest() {
        var inexistentUserId = 999;

        given()
            .pathParam("userId", inexistentUserId)
        .when()
            .get()
        .then()
            .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should list a user's followers")
    public void listFollowersTest() {
        Response response =
            given()
                .pathParam("userId", userId)
            .when()
                .get()
            .then()
                .extract().response();

        Object followersCount = response.jsonPath().get("followersCount");
        List<Object> followersContent = response.jsonPath().getList("content");

        assertEquals(Status.OK.getStatusCode(), response.getStatusCode());
        assertEquals(1, followersCount);
        assertEquals(1, followersContent.size());
    }

    @Test
    @DisplayName("Should return 404 on unfollow user and UserId doesn't exist")
    public void userNotFoundWhenUnFollowingAUserTest() {
        var inexistentUserId = 999;

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", inexistentUserId)
            .queryParam("followerId", followerId)
        .when()
            .delete()
        .then()
            .statusCode(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should unfollow an user")
    public void unfollowUserTest() {
        given()
            .pathParam("userId", userId)
            .queryParam("followerId", followerId)
        .when()
            .delete()
        .then()
            .statusCode(Status.NO_CONTENT.getStatusCode());
    }
}