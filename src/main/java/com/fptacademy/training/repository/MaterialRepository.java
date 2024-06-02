package com.fptacademy.training.repository;

import com.fptacademy.training.domain.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Long> {}
