package io.github.wedermonteiro.quarkussocial.rest;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response.Status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.wedermonteiro.quarkussocial.rest.domain.model.User;
import io.github.wedermonteiro.quarkussocial.rest.domain.repository.UserRepository;
import io.github.wedermonteiro.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;

    Long userId;

    @BeforeEach
    @Transactional
    public void setUp() {
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();
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
    @DisplayName("Should return 404 when UserId doesn´t exist")
    public void userNotFoundTest() {
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

}