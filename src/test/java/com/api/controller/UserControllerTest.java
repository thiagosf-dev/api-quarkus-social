package com.api.controller;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.api.dto.CreateUserRequestDto;
import com.api.dto.ResponseError;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
class UserControllerTest {

  @Test
  @DisplayName("should create an user succesfully")
  void createUser() {
    var userRequest = new CreateUserRequestDto();
    userRequest.setName("Test");
    userRequest.setAge(99);

    var response = given()
        .contentType(ContentType.JSON)
        .body(userRequest)
        .when()
        .post("/users")
        .then()
        .extract()
        .response();

    assertEquals(201, response.statusCode());
    assertNotNull(response.jsonPath().getString("id"));
  }

  // @Test
  @DisplayName("SHould return erros when json is not valid")
  void createUserValidationError() {
    var userRequest = new CreateUserRequestDto();
    userRequest.setName(null);
    userRequest.setAge(null);

    var response = given()
        .contentType(ContentType.JSON)
        .body(userRequest)
        .when()
        .post("/users")
        .then()
        .extract()
        .response();

    assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
    assertEquals("Validation Error", response.jsonPath().getString("message"));
  }

}
