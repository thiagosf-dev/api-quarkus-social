package com.api.repository;

import java.util.List;

import com.api.model.FollowerModel;
import com.api.model.UserModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<FollowerModel> {

  public boolean follows(UserModel follower, UserModel user) {
    var params = Parameters.with("follower", follower).and("user", user);

    return find("follower = :follower and user = :user", params)
        .firstResultOptional()
        .isPresent();
  }

  public List<FollowerModel> findByUser(Long userId) {
    return find("user.id", userId).list();
  }

  public void deleteByFollowerAndUserId(Long followerId, Long userId) {
    var params = Parameters
        .with("userId", userId)
        .and("followerId", followerId)
        .map();

    delete("follower.id = :followerId and user.id = :userId", params);
  }

}
