package org.example.ebookstore.repositories;

import org.example.ebookstore.entities.ScheduledTaskAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledTaskAuditRepository extends JpaRepository<ScheduledTaskAudit, Long> {
}
