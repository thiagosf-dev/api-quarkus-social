package com.api.dto;

import java.util.List;

import lombok.Data;

@Data
public class FollowerPerUserResponseDto {

  private Integer followersCount;

  private List<FollowerResponseDto> content;

}
