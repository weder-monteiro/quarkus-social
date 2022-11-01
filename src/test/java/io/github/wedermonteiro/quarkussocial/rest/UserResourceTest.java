package io.github.wedermonteiro.quarkussocial.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.wedermonteiro.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class UserResourceTest {

    @Test
    @DisplayName("Should create an user sucessfully")
    public void createUserTest() {
        var user = new CreateUserRequest();

        user.setName("Fulano");
        user.setAge(30);

        Response response = 
            given()
                .contentType(ContentType.JSON)
                .body(user)
            .when()
                .post("/users")
            .then()
                .extract().response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }

}