package com.cutm.smo.controller;

import com.cutm.smo.services.InsightsService;
import com.cutm.smo.util.LoggingUtil;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/insights")
@CrossOrigin(origins = "*")
public class InsightsController {
    private final InsightsService insightsService;

    public InsightsController(InsightsService insightsService) { this.insightsService = insightsService; }

    @GetMapping("/dashboard")
    public Map<String, Long> getDashboard() {
        long startTime = System.currentTimeMillis();
        try {
            log.info("=== GET INSIGHTS DASHBOARD START ===");
            long totalPO = insightsService.getTotalPurchaseOrders();
            log.debug("Total Purchase Orders: {}", totalPO);
            long totalGrns = insightsService.getTotalGrns();
            log.debug("Total GRNs: {}", totalGrns);
            long totalInventory = insightsService.getTotalInventoryItems();
            log.debug("Total Inventory Items: {}", totalInventory);
            long totalWip = insightsService.getTotalWipRecords();
            log.debug("Total WIP Records: {}", totalWip);
            
            Map<String, Long> dashboard = Map.of(
                "totalPurchaseOrders", totalPO,
                "totalGrns", totalGrns,
                "totalInventoryItems", totalInventory,
                "totalWipRecords", totalWip
            );
            
            log.info("Dashboard data retrieved successfully");
            long endTime = System.currentTimeMillis();
            LoggingUtil.logPerformance(log, "Get Insights Dashboard", startTime, endTime);
            log.info("=== GET INSIGHTS DASHBOARD END - SUCCESS ===");
            return dashboard;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            LoggingUtil.logError(log, "Failed to get insights dashboard", e);
            LoggingUtil.logPerformance(log, "Get Insights Dashboard (Failed)", startTime, endTime);
            throw e;
        }
    }

    @GetMapping("/supervisor")
    public Map<String, Object> getSupervisorInsights() {
        long startTime = System.currentTimeMillis();
        try {
            log.info("=== GET SUPERVISOR INSIGHTS START ===");
            long activeWip = insightsService.getTotalWipRecords();
            log.debug("Active WIP Count: {}", activeWip);
            
            // Calculate bottleneck operations (simplified - count WIP records as proxy)
            int bottleneckOps = activeWip > 10 ? 2 : 0;
            String balancingHint = bottleneckOps > 0 
                ? "Production line has bottlenecks. Consider rebalancing workload."
                : "Production line is balanced.";
            
            Map<String, Object> insights = Map.of(
                "activeWipCount", (int) activeWip,
                "bottleneckOperationCount", bottleneckOps,
                "lineBalancingHint", balancingHint
            );
            
            log.info("Supervisor insights retrieved successfully");
            long endTime = System.currentTimeMillis();
            LoggingUtil.logPerformance(log, "Get Supervisor Insights", startTime, endTime);
            log.info("=== GET SUPERVISOR INSIGHTS END - SUCCESS ===");
            return insights;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            LoggingUtil.logError(log, "Failed to get supervisor insights", e);
            LoggingUtil.logPerformance(log, "Get Supervisor Insights (Failed)", startTime, endTime);
            throw e;
        }
    }
}