package com.fptacademy.training.repository;

import com.fptacademy.training.domain.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileStorageRepository extends JpaRepository<FileStorage, Long> { }
