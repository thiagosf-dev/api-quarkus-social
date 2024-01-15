package com.api.dto;

import java.time.LocalDateTime;

import com.api.model.PostModel;

import lombok.Data;

@Data
public class PostResponseDto {

  private String text;

  private LocalDateTime dateTime;

  public static PostResponseDto fromEntity(PostModel postModel) {
    PostResponseDto responseDto = new PostResponseDto();
    responseDto.setDateTime(postModel.getDateTime());
    responseDto.setText(postModel.getText());

    return responseDto;
  }

}
