package com.api.dto;

import com.api.model.FollowerModel;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FollowerResponseDto {

  private Long id;

  private String name;

  public FollowerResponseDto(FollowerModel follower) {
    this(follower.getFollower().getId(), follower.getFollower().getName());
  }

  public FollowerResponseDto(Long id, String name) {
    this.id = id;
    this.name = name;
  }

}
