package com.codecentric.retailbank.repository;

import com.codecentric.retailbank.model.security.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LogRepository extends JpaRepository<Log, UUID> {
}