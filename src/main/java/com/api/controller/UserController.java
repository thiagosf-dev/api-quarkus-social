package com.api.controller;

import java.util.Set;

import com.api.dto.CreateUserRequestDto;
import com.api.dto.ResponseError;
import com.api.model.UserModel;
import com.api.repository.UserRepository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserController {

  private UserRepository userRepository;
  private Validator validator;

  @Inject
  public UserController(UserRepository userRepository, Validator validator) {
    this.userRepository = userRepository;
    this.validator = validator;
  }

  @POST
  @Transactional
  public Response createUser(CreateUserRequestDto userRequest) {
    Set<ConstraintViolation<CreateUserRequestDto>> violations = validator.validate(userRequest);

    if (!violations.isEmpty())
      return ResponseError
          .createFromValidation(violations)
          .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);

    UserModel userModel = new UserModel();
    userModel.setAge(userRequest.getAge());
    userModel.setName(userRequest.getName());

    this.userRepository.persist(userModel);

    return Response
        .status(Response.Status.CREATED)
        .entity(userModel)
        .build();
  }

  @GET
  public Response listAllUsers() {
    PanacheQuery<UserModel> users = this.userRepository.findAll();
    return Response
        .status(Response.Status.OK)
        .entity(users.list())
        .build();
  }

  @DELETE
  @Path("/{id}")
  @Transactional
  public Response deleteUser(@PathParam("id") Long id) {
    UserModel userModel = this.userRepository.findById(id);

    if (userModel == null)
      return Response
          .status(Response.Status.NOT_FOUND)
          .build();

    this.userRepository.delete(userModel);

    return Response
        .status(Response.Status.NO_CONTENT)
        .build();
  }

  @PUT
  @Path("/{id}")
  @Transactional
  public Response updateUser(@PathParam("id") Long id, CreateUserRequestDto userRequest) {
    UserModel userModel = this.userRepository.findById(id);

    if (userModel == null)
      return Response
          .status(Response.Status.NOT_FOUND)
          .build();

    userModel.setAge(userRequest.getAge());
    userModel.setName(userRequest.getName());

    this.userRepository.persist(userModel);

    return Response
        .status(Response.Status.OK)
        .entity(userModel)
        .build();
  }

}
