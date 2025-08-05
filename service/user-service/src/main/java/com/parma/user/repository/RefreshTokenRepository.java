package com.parma.user.repository;

import com.parma.user.model.RefreshToken;
import com.parma.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByToken(String token);

    void deleteAllByUserIn(List<User> users);

    Optional<RefreshToken> findByUser(User user);
}
