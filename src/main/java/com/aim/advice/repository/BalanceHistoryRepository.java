package com.aim.advice.repository;

import com.aim.advice.domain.balance.BalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BalanceHistoryRepository extends JpaRepository<BalanceHistory, Long> {
}
