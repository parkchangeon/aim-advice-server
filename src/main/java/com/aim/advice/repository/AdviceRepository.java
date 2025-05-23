package com.aim.advice.repository;

import com.aim.advice.domain.advice.Advice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AdviceRepository extends JpaRepository<Advice, Long> {
}
