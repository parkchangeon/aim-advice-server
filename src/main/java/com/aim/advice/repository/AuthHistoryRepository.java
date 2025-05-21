package com.aim.advice.repository;

import com.aim.advice.domain.AuthHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AuthHistoryRepository extends JpaRepository<AuthHistory, Long> {
    List<AuthHistory> findByUserIdAndAction(String userId, String action);
}
