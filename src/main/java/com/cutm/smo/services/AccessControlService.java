package com.cutm.smo.services;

import java.util.Arrays;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.cutm.smo.util.LoggingUtil;
import lombok.extern.slf4j.Slf4j;

import com.cutm.smo.models.EmployeeInfo;
import com.cutm.smo.models.EmployeeLogin;
import com.cutm.smo.models.Role;
import com.cutm.smo.repositories.EmployeeInfoRepository;
import com.cutm.smo.repositories.EmployeeLoginRepository;

@Slf4j
@Service
public class AccessControlService {
    private final EmployeeInfoRepository employeeInfoRepository;
    private final EmployeeLoginRepository employeeLoginRepository;

    public AccessControlService(
            EmployeeInfoRepository employeeInfoRepository,
            EmployeeLoginRepository employeeLoginRepository) {
        this.employeeInfoRepository = employeeInfoRepository;
        this.employeeLoginRepository = employeeLoginRepository;
    }

    public Long require(String actorEmpId, String requiredActivity) {
        long startTime = System.currentTimeMillis();
        try {
            log.info("=== ACCESS CONTROL CHECK START ===");
            log.debug("Actor Employee ID: {}", actorEmpId);
            log.debug("Required Activity: {}", requiredActivity);
            
            Long actorId = parseEmpId(actorEmpId);
            EmployeeInfo actor = employeeInfoRepository.findById(actorId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid actor"));
            EmployeeLogin login = employeeLoginRepository.findById(actorId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid actor login"));

            if (!"ACTIVE".equalsIgnoreCase(actor.getStatus()) || !"ACTIVE".equalsIgnoreCase(login.getStatus())) {
                log.warn("Access denied: User {} is inactive", actorId);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is inactive");
            }
            Role role = actor.getRole();
            if (role == null || !"ACTIVE".equalsIgnoreCase(role.getStatus())) {
                log.warn("Access denied: Role for user {} is inactive", actorId);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Role is inactive");
            }
            String activity = role.getActivity() == null ? "" : role.getActivity();
            String normalizedRequired = requiredActivity.trim().toUpperCase(Locale.ROOT);
            boolean allowed = activity.equalsIgnoreCase("ALL")
                    || activity.equalsIgnoreCase("ADMIN")
                    || Arrays.stream(activity.split(","))
                            .map(a -> a.trim().toUpperCase(Locale.ROOT))
                            .anyMatch(a -> a.equals(normalizedRequired));
            if (!allowed) {
                log.warn("Access denied: User {} does not have permission for activity {}", actorId, requiredActivity);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied for " + requiredActivity);
            }
            
            log.info("Access granted for user {} to activity {}", actorId, requiredActivity);
            long endTime = System.currentTimeMillis();
            LoggingUtil.logPerformance(log, "Access Control Check", startTime, endTime);
            log.info("=== ACCESS CONTROL CHECK END - SUCCESS ===");
            return actorId;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            LoggingUtil.logError(log, "Access control check failed", e);
            LoggingUtil.logPerformance(log, "Access Control Check (Failed)", startTime, endTime);
            throw e;
        }
    }

    private Long parseEmpId(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (Exception ex) {
            log.warn("Failed to parse Employee ID: {}", value);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid actor identity");
        }
    }
}
