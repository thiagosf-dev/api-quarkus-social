package com.api.controller;

import java.util.stream.Collectors;

import com.api.dto.FollowerPerUserResponseDto;
import com.api.dto.FollowerRequestDto;
import com.api.dto.FollowerResponseDto;
import com.api.model.FollowerModel;
import com.api.repository.FollowerRepository;
import com.api.repository.UserRepository;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/users/{userId}/followers")
public class FollowerController {

  private FollowerRepository followerRepository;
  private UserRepository userRepository;
  private static final String USER_NOT_FOUND_MESSAGE = "Usuário não encontrado.";

  @Inject
  public FollowerController(FollowerRepository followerRepository, UserRepository userRepository) {
    this.followerRepository = followerRepository;
    this.userRepository = userRepository;
  }

  @PUT
  @Transactional
  public Response followUser(@PathParam("userId") Long userId, FollowerRequestDto followerRequestDto) {
    if (userId.equals(followerRequestDto.getFollowerId())) {
      return Response
          .status(Response.Status.CONFLICT)
          .entity("Você não pode seguir a si mesmo.")
          .build();
    }

    var user = this.userRepository.findById(userId);

    if (user == null) {
      return Response
          .status(Response.Status.NOT_FOUND)
          .entity(FollowerController.USER_NOT_FOUND_MESSAGE)
          .build();
    }

    var follower = this.userRepository.findById(followerRequestDto.getFollowerId());

    if (!this.followerRepository.follows(follower, user)) {
      var entity = new FollowerModel();
      entity.setFollower(follower);
      entity.setUser(user);
      this.followerRepository.persist(entity);
    }

    return Response
        .status(Response.Status.NO_CONTENT)
        .build();
  }

  @GET
  public Response listFollowers(@PathParam("userId") Long userId) {
    var user = this.userRepository.findById(userId);

    if (user == null) {
      return Response
          .status(Response.Status.NOT_FOUND)
          .entity(FollowerController.USER_NOT_FOUND_MESSAGE)
          .build();
    }

    var list = this.followerRepository.findByUser(userId);

    FollowerPerUserResponseDto followerPerUserResponseDto = new FollowerPerUserResponseDto();
    followerPerUserResponseDto.setFollowersCount(list.size());

    var followersList = list
        .stream()
        .map(FollowerResponseDto::new)
        .collect(Collectors.toList());

    followerPerUserResponseDto.setContent(followersList);

    return Response
        .ok(followerPerUserResponseDto)
        .build();
  }

  @DELETE
  @Transactional
  public Response unfollowUser(
      @PathParam("userId") Long userId,
      @QueryParam("followerId") Long followerId) {
    var user = this.userRepository.findById(userId);

    if (user == null) {
      return Response
          .status(Response.Status.NOT_FOUND)
          .entity(FollowerController.USER_NOT_FOUND_MESSAGE)
          .build();
    }

    this.followerRepository.deleteByFollowerAndUserId(followerId, userId);

    return Response
        .status(Response.Status.NO_CONTENT)
        .entity(followerId)
        .build();
  }

}
