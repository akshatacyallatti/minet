package com.example.minet.repositories;

import com.example.minet.entities.AppLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppLogRepository extends JpaRepository<AppLog, Long> {}

