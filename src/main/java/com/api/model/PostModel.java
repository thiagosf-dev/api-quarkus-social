package com.api.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "posts")
@Data
public class PostModel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "text", length = 255, nullable = false)
  private String text;

  @Column(name = "date_time", nullable = false)
  private LocalDateTime dateTime;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserModel user;

  @PrePersist
  public void prePersist() {
    this.setDateTime(LocalDateTime.now());
  }

}
