package io.github.wedermonteiro.quarkussocial.rest.domain.repository;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import io.github.wedermonteiro.quarkussocial.rest.domain.model.Follower;
import io.github.wedermonteiro.quarkussocial.rest.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user) {
        Map<String, Object> params = Parameters
            .with("follower", follower)
            .and("user", user)
            .map();

        PanacheQuery<Follower> query = find("follower = :follower and user = :user", params);

        return query.firstResultOptional().isPresent();
    }

    public List<Follower> findByUser(Long userId) {
        PanacheQuery<Follower> query = find("user.id", userId);

        return query.list();
    }

    public void deleteByFollowerAndUser(Long followerId, Long userId) {
        Map<String, Object> params = Parameters
            .with("userId", userId)
            .and("followerId", followerId)
            .map();

        delete("follower.id = :followerId and user.id = :userId", params);
    }

}
