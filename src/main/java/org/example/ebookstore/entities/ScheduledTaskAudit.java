package org.example.ebookstore.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "scheduled_task_audit")
public class ScheduledTaskAudit extends BaseEntity {
    @Column(name = "task_name", length = 50, nullable = false)
    private String taskName;
    @Column(name = "last_run_date")
    private LocalDate lastRunDate;

    public ScheduledTaskAudit() {
    }

    public ScheduledTaskAudit(String taskName, LocalDate lastRunDate) {
        this.taskName = taskName;
        this.lastRunDate = lastRunDate;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public LocalDate getLastRunDate() {
        return lastRunDate;
    }

    public void setLastRunDate(LocalDate lastRunDate) {
        this.lastRunDate = lastRunDate;
    }
}
