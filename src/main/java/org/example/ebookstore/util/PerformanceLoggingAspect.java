package org.example.ebookstore.util;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.ebookstore.entities.PerformanceLog;
import org.example.ebookstore.repositories.PerformanceLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class PerformanceLoggingAspect {
    private final PerformanceLogRepository performanceLogRepository;
    private HttpServletRequest request;

    @Autowired
    public PerformanceLoggingAspect(PerformanceLogRepository performanceLogRepository, HttpServletRequest request) {
        this.performanceLogRepository = performanceLogRepository;
        this.request = request;
    }

    @Pointcut("within(@org.springframework.stereotype.Controller *) || " +
            "within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {
    }

    @Around("controllerMethods()")
    public Object logPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            recordPerformanceLog(joinPoint, duration, getStatusCode(result));
        }
    }

    private void recordPerformanceLog(ProceedingJoinPoint joinPoint, long duration, Integer statusCode) {
        PerformanceLog log = new PerformanceLog();
        log.setTimestamp(LocalDateTime.now());
        log.setRequestUrl(request.getRequestURL().toString());
        log.setDurationMs((int) duration);
        log.setStatusCode(statusCode);
        this.performanceLogRepository.save(log);
    }

    private Integer getStatusCode(Object result) {
        if (result instanceof ResponseEntity) {
            return ((ResponseEntity<?>) result).getStatusCode().value();
        }
        return 200;
    }
}
