package com.fptacademy.training.repository;

import com.fptacademy.training.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {}
