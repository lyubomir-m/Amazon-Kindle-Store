package org.example.ebookstore.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "performance_logs")
public class PerformanceLog extends BaseEntity {
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "request_url", nullable = false)
    private String requestUrl;
    @Column(name = "duration_ms", nullable = false)
    private Integer durationMs;
    @Column(name = "status_code", nullable = false)
    private Integer statusCode;

    public PerformanceLog() {
    }

    public PerformanceLog(LocalDateTime timestamp, String requestUrl, Integer durationMs, Integer statusCode) {
        this.timestamp = timestamp;
        this.requestUrl = requestUrl;
        this.durationMs = durationMs;
        this.statusCode = statusCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Integer getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Integer durationMs) {
        this.durationMs = durationMs;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
