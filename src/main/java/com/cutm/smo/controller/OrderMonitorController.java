package com.cutm.smo.controller;

import com.cutm.smo.services.AccessControlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class OrderMonitorController {
    private final AccessControlService accessControlService;

    public OrderMonitorController(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @GetMapping("/approved-orders")
    public List<Map<String, Object>> getApprovedOrders(@RequestParam String actorEmpId) {
        log.info("=== GET APPROVED ORDERS START ===");
        log.debug("Actor Employee ID: {}", actorEmpId);

        // Check access (GM or Supervisor)
        try {
            accessControlService.require(actorEmpId, "PP_APPROVE");
        } catch (Exception e1) {
            try {
                accessControlService.require(actorEmpId, "SUPERVISOR_MONITOR_WIP");
            } catch (Exception e2) {
                throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "Access denied: Requires PP_APPROVE or SUPERVISOR_MONITOR_WIP activity"
                );
            }
        }

        // Mock data - replace with actual database query
        List<Map<String, Object>> orders = new ArrayList<>();

        Map<String, Object> order1 = new HashMap<>();
        order1.put("order_id", "ORD-2026-045");
        order1.put("product_name", "Men's Shirt");
        order1.put("routing_id", 1);
        orders.add(order1);

        Map<String, Object> order2 = new HashMap<>();
        order2.put("order_id", "ORD-2026-046");
        order2.put("product_name", "Women's Jeans");
        order2.put("routing_id", 2);
        orders.add(order2);

        Map<String, Object> order3 = new HashMap<>();
        order3.put("order_id", "ORD-2026-047");
        order3.put("product_name", "Kids T-Shirt");
        order3.put("routing_id", 3);
        orders.add(order3);

        log.info("Retrieved {} approved orders", orders.size());
        log.info("=== GET APPROVED ORDERS END - SUCCESS ===");
        return orders;
    }

    @GetMapping("/order-status/{routingId}")
    public Map<String, Object> getOrderStatus(
            @PathVariable Long routingId,
            @RequestParam String actorEmpId) {
        log.info("=== GET ORDER STATUS START ===");
        log.debug("Actor Employee ID: {}", actorEmpId);
        log.debug("Routing ID: {}", routingId);

        // Check access (GM or Supervisor)
        try {
            accessControlService.require(actorEmpId, "PP_APPROVE");
        } catch (Exception e1) {
            try {
                accessControlService.require(actorEmpId, "SUPERVISOR_MONITOR_WIP");
            } catch (Exception e2) {
                throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "Access denied: Requires PP_APPROVE or SUPERVISOR_MONITOR_WIP activity"
                );
            }
        }

        // Mock data - replace with actual database query
        Map<String, Object> status = new HashMap<>();
        status.put("order_id", "ORD-2026-045");
        status.put("order_qty", 500);
        status.put("completed", 325);
        status.put("pending", 175);
        status.put("progress_percent", 65.0);
        status.put("expected_completion_date", LocalDate.now().plusDays(5).toString());
        status.put("avg_time_per_unit", "12.5 min");

        log.info("Retrieved order status for routing: {}", routingId);
        log.info("=== GET ORDER STATUS END - SUCCESS ===");
        return status;
    }
}
