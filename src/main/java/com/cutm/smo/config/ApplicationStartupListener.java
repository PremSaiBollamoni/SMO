package com.cutm.smo.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * Listener to log application startup completion
 */
@Slf4j
@Component
public class ApplicationStartupListener {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${app.base-url:http://localhost}")
    private String baseUrl;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        String fullUrl = baseUrl + ":" + serverPort;
        String healthUrl = fullUrl + "/api/health";
        
        // Print to console with explicit flush for Render compatibility
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                ║");
        System.out.println("║          ✅ SMO BACKEND APPLICATION STARTED SUCCESSFULLY       ║");
        System.out.println("║                                                                ║");
        System.out.println("║  Server: " + String.format("%-50s", fullUrl) + "║");
        System.out.println("║  Health: " + String.format("%-50s", healthUrl) + "║");
        System.out.println("║  Logs:   logs/ directory                                       ║");
        System.out.println("║                                                                ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println("\n");
        System.out.flush(); // Ensure output is flushed immediately
        
        // Also log to file
        log.info("╔════════════════════════════════════════════════════════════════╗");
        log.info("║          ✅ SMO BACKEND APPLICATION STARTED SUCCESSFULLY       ║");
        log.info("║  Server: " + fullUrl);
        log.info("║  Health: " + healthUrl);
        log.info("║  Logs:   logs/ directory");
        log.info("╚════════════════════════════════════════════════════════════════╝");
    }
}

