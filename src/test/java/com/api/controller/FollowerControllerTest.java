package com.api.controller;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.api.dto.FollowerRequestDto;
import com.api.model.FollowerModel;
import com.api.model.UserModel;
import com.api.repository.FollowerRepository;
import com.api.repository.UserRepository;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@QuarkusTest
@TestHTTPEndpoint(FollowerController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FollowerControllerTest {

  @Inject
  UserRepository userRepository;

  @Inject
  FollowerRepository followerRepository;

  private Long userId;
  private Long followerId;

  @BeforeEach
  @Transactional
  void setup() {
    var user = new UserModel();
    user.setAge(999);
    user.setName("test");
    this.userRepository.persist(user);
    this.userId = user.getId();

    var follower = new UserModel();
    follower.setAge(999);
    follower.setName("test");
    this.userRepository.persist(follower);
    this.followerId = follower.getId();

    FollowerModel followerModel = new FollowerModel();
    followerModel.setFollower(follower);
    followerModel.setUser(user);
    this.followerRepository.persist(followerModel);
  }

  @Test
  @Order(1)
  void sameUserAsFollowerTest() {
    var body = new FollowerRequestDto();
    body.setFollowerId(this.userId);

    given()
        .contentType(ContentType.JSON)
        .body(body)
        .pathParam("userId", this.userId)
        .when()
        .put()
        .then()
        .statusCode(Response.Status.CONFLICT.getStatusCode())
        .body(Matchers.is("Você não pode seguir a si mesmo."));
  }

  @Test
  @DisplayName("should return 404 on follower a user when user id doesn't exist")
  @Order(2)
  void userNotFoundWhenTryingToFollowTest() {
    var body = new FollowerRequestDto();
    body.setFollowerId(this.userId);

    var inexistentUserId = -999;

    given()
        .contentType(ContentType.JSON)
        .body(body)
        .pathParam("userId", inexistentUserId)
        .when()
        .put()
        .then()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode())
        .body(Matchers.is("Usuário não encontrado."));
  }

  @Test
  @DisplayName("should follow a user")
  @Order(3)
  void FollowerUserTest() {
    var body = new FollowerRequestDto();
    body.setFollowerId(this.followerId);

    given()
        .contentType(ContentType.JSON)
        .body(body)
        .pathParam("userId", userId)
        .when()
        .put()
        .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }

  @Test
  @DisplayName("should return 404 on list user followers and user id doesn't exist")
  @Order(4)
  void userNotFoundWhenListFollowersTest() {
    var inexistentUserId = -999;

    given()
        .contentType(ContentType.JSON)
        .pathParam("userId", inexistentUserId)
        .when()
        .get()
        .then()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode())
        .body(Matchers.is("Usuário não encontrado."));
  }

  @Test
  @DisplayName("should list a user's followers")
  @Order(5)
  void listFollowersTest() {
    var response = given()
        .contentType(ContentType.JSON)
        .pathParam("userId", this.userId)
        .when()
        .get()
        .then()
        .extract()
        .response();

    var followersCount = response.jsonPath().get("followersCount");

    assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
    assertEquals(1, followersCount);
  }

  @Test
  @DisplayName("should return 404 on unfollow user and user id doesn't exist")
  @Order(4)
  void userNotFoundWhenUnfollowingAUserTest() {
    var inexistentUserId = -999;

    given()
        .contentType(ContentType.JSON)
        .pathParam("userId", inexistentUserId)
        .queryParam("followerId", this.followerId)
        .when()
        .delete()
        .then()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode())
        .body(Matchers.is("Usuário não encontrado."));
  }

  @Test
  @DisplayName("should unfollow a user")
  @Order(4)
  void unfollowingAUserTest() {
    given()
        .pathParam("userId", this.userId)
        .queryParam("followerId", this.followerId)
        .when()
        .delete()
        .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }

}
