package io.github.wedermonteiro.quarkussocial.rest.domain.repository;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import io.github.wedermonteiro.quarkussocial.rest.domain.model.Follower;
import io.github.wedermonteiro.quarkussocial.rest.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user) {
        var params = Parameters
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

}
