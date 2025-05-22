package com.aim.advice.repository;

import com.aim.advice.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUserId(String username);
    Optional<User> findByUserId(String userId);
}
