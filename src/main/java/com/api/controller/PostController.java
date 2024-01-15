package com.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.api.dto.CreatePostRequestDto;
import com.api.dto.PostResponseDto;
import com.api.model.PostModel;
import com.api.model.UserModel;
import com.api.repository.FollowerRepository;
import com.api.repository.PostRepository;
import com.api.repository.UserRepository;

import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostController {

  private UserRepository userRepository;
  private PostRepository repository;
  private FollowerRepository followerRepository;

  @Inject
  public PostController(
      UserRepository userRepository,
      PostRepository repository,
      FollowerRepository followerRepository) {
    this.userRepository = userRepository;
    this.repository = repository;
    this.followerRepository = followerRepository;
  }

  @POST
  @Transactional
  public Response save(@PathParam("userId") Long userId, CreatePostRequestDto createPostRequestDto) {
    UserModel userModel = this.userRepository.findById(userId);

    if (userModel == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }

    PostModel newPost = new PostModel();
    newPost.setText(createPostRequestDto.getText());
    newPost.setUser(userModel);

    this.repository.persist(newPost);

    return Response
        .status(Response.Status.CREATED)
        .build();
  }

  @GET
  public Response list(
      @PathParam("userId") Long userId,
      @HeaderParam("followerId") Long followerId) {
    UserModel userModel = this.userRepository.findById(userId);

    if (userModel == null) {
      return Response
          .status(Response.Status.NOT_FOUND)
          .entity("Usuário inválido.")
          .build();
    }

    if (followerId == null) {
      return Response
          .status(Response.Status.BAD_REQUEST)
          .entity("Você não possui autorização para executar esta operação.")
          .build();
    }

    UserModel follower = this.userRepository.findById(followerId);

    if (follower == null) {
      return Response
          .status(Response.Status.BAD_REQUEST)
          .entity("Seguidor inexistente.")
          .build();
    }

    boolean follows = this.followerRepository.follows(follower, userModel);

    if (!follows) {
      return Response
          .status(Response.Status.FORBIDDEN)
          .entity("Você não possui autorização para executar esta operação.")
          .build();
    }

    List<PostModel> posts = this.repository.find(
        "user",
        Sort.by("dateTime", Sort.Direction.Descending),
        userModel).list();

    List<PostResponseDto> list = posts
        .stream()
        .map(PostResponseDto::fromEntity)
        .collect(Collectors.toList());

    return Response
        .status(Response.Status.OK)
        .entity(list)
        .build();
  }

}
