package com.cutm.smo.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cutm.smo.util.LoggingUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        long startTime = System.currentTimeMillis();
        try {
            log.info("=== HEALTH CHECK START ===");
            Map<String, String> response = Map.of("status", "ok", "service", "smo-backend");
            log.info("Health check successful");
            long endTime = System.currentTimeMillis();
            LoggingUtil.logPerformance(log, "Health Check", startTime, endTime);
            log.info("=== HEALTH CHECK END - SUCCESS ===");
            return response;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            LoggingUtil.logError(log, "Health check failed", e);
            LoggingUtil.logPerformance(log, "Health Check (Failed)", startTime, endTime);
            throw e;
        }
    }
}
