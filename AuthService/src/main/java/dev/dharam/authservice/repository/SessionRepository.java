package dev.dharam.authservice.repository;

import dev.dharam.authservice.models.Session;
import dev.dharam.authservice.models.SessionStatus;
import dev.dharam.authservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session,Long> {

    @Override
    Session  save(Session session);


    @Override
    <S extends Session> List<S> saveAll(Iterable<S> entities);

    List<Session> findByUserAndStatus(User user, SessionStatus status);
    Optional<Session> findByRefreshToken(String token);
}
