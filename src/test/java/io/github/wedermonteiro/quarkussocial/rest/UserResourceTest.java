package io.github.wedermonteiro.quarkussocial.rest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.github.wedermonteiro.quarkussocial.rest.dto.CreateUserRequest;
import io.github.wedermonteiro.quarkussocial.rest.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserResourceTest {

    @TestHTTPResource("/users")
    URL apiURL;

    @Test
    @DisplayName("Should create an user sucessfully")
    @Order(1)
    public void createUserTest() {
        var user = new CreateUserRequest();

        user.setName("Fulano");
        user.setAge(30);

        Response response = 
            given()
                .contentType(ContentType.JSON)
                .body(user)
            .when()
                .post(apiURL)
            .then()
                .extract().response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @DisplayName("Should return error when json is not valid")
    @Order(2)
    public void createUserValidationError() {
        var user = new CreateUserRequest();

        user.setName(null);
        user.setAge(null);

        Response response =
            given()
                .contentType(ContentType.JSON)
                .body(user)
            .when()
                .post(apiURL)
            .then()
                .extract().response();

        List<Map<String, String>> errors = response.jsonPath().getList("errors");

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));
    }

    @Test
    @DisplayName("Should list all users")
    @Order(3)
    public void listAllUsersTest() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get(apiURL)
        .then()
            .statusCode(200)
            .body("size()", Matchers.is(1));
    }

}