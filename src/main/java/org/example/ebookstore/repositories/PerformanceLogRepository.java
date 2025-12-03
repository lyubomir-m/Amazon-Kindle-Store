package org.example.ebookstore.repositories;

import org.example.ebookstore.entities.PerformanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceLogRepository extends JpaRepository<PerformanceLog, Long> {
}
