package com.api.controller;

import static io.restassured.RestAssured.*;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.api.dto.CreatePostRequestDto;
import com.api.model.FollowerModel;
import com.api.model.PostModel;
import com.api.model.UserModel;
import com.api.repository.FollowerRepository;
import com.api.repository.PostRepository;
import com.api.repository.UserRepository;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
@TestHTTPEndpoint(PostController.class) // para identificar quais as URL's que serão feitas as requisições
class PostControllerTest {

  @Inject
  UserRepository userRepository;

  @Inject
  FollowerRepository followerRepository;

  @Inject
  PostRepository postRepository;

  private Long userId;
  private Long inexistentUserId = -999l;
  private Long inexistentFollowerId = -999l;
  private Long userNotFollowerId;
  private Long userFollowerId;

  @BeforeEach
  @Transactional
  void setup() {
    var user = new UserModel();
    user.setAge(999);
    user.setName("test");
    this.userRepository.persist(user);
    this.userId = user.getId();

    var userNotFollower = new UserModel();
    userNotFollower.setAge(999);
    userNotFollower.setName("test");
    this.userRepository.persist(userNotFollower);
    this.userNotFollowerId = userNotFollower.getId();

    var userFollower = new UserModel();
    userFollower.setAge(999);
    userFollower.setName("test");
    this.userRepository.persist(userFollower);
    this.userFollowerId = userFollower.getId();

    FollowerModel followerModel = new FollowerModel();
    followerModel.setFollower(userFollower);
    followerModel.setUser(user);
    this.followerRepository.persist(followerModel);

    PostModel postModel = new PostModel();
    postModel.setText("teste");
    postModel.setUser(user);
    this.postRepository.persist(postModel);
  }

  @Test
  @DisplayName("should create a post for a user")
  void createPostTest() {
    CreatePostRequestDto createPostRequestDto = new CreatePostRequestDto();
    createPostRequestDto.setText("test");

    given()
        .contentType(ContentType.JSON)
        .body(createPostRequestDto)
        .pathParam("userId", userId)
        .when()
        .post()
        .then()
        .statusCode(201);
  }

  @Test
  @DisplayName("should return 404 when trying to create a post for an inexistent user")
  void postForAnInexistentUserTest() {
    CreatePostRequestDto createPostRequestDto = new CreatePostRequestDto();
    createPostRequestDto.setText("test");

    given()
        .contentType(ContentType.JSON)
        .body(createPostRequestDto)
        .pathParam("userId", inexistentUserId)
        .when()
        .post()
        .then()
        .statusCode(404);
  }

  @Test
  @DisplayName("should return 404 when user doesn't exist")
  void listPostUserNotFoundTest() {
    given()
        .contentType(ContentType.JSON)
        .pathParam("userId", inexistentUserId)
        .when()
        .get()
        .then()
        .statusCode(404);
  }

  @Test
  @DisplayName("should return 400 when followerId header is not present")
  void listPostFollowerHeaderNotSendTest() {
    given()
        .contentType(ContentType.JSON)
        .pathParam("userId", this.userId)
        .when()
        .get()
        .then()
        .statusCode(400)
        .body(Matchers.is("Você não possui autorização para executar esta operação."));
  }

  @Test
  @DisplayName("should return 404 when follower doesn't exist")
  void listPostFollowerNotFoundTest() {
    given()
        .contentType(ContentType.JSON)
        .pathParam("userId", userId)
        .header("followerId", this.inexistentFollowerId)
        .when()
        .get()
        .then()
        .statusCode(400)
        .body(Matchers.is("Seguidor inexistente."));
  }

  @Test
  @DisplayName("should return 403 when follower isn't a follower")
  void listPostNotAFollowerTest() {
    given()
        .contentType(ContentType.JSON)
        .pathParam("userId", userId)
        .header("followerId", this.userNotFollowerId)
        .when()
        .get()
        .then()
        .statusCode(403)
        .body(Matchers.is("Você não possui autorização para executar esta operação."));
  }

  @Test
  @DisplayName("should return posts")
  void listPostsTest() {
    given()
        .contentType(ContentType.JSON)
        .pathParam("userId", userId)
        .header("followerId", this.userFollowerId)
        .when()
        .get()
        .then()
        .statusCode(200)
        .body("size()", Matchers.greaterThanOrEqualTo(0));
  }

}
