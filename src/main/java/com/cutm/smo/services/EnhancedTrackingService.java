package com.cutm.smo.services;

import com.cutm.smo.dto.TrackingRequest;
import com.cutm.smo.models.*;
import com.cutm.smo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class EnhancedTrackingService {

    @Autowired
    private TempActiveAssignmentRepository tempActiveAssignmentRepository;

    @Autowired
    private TempAssignmentLogRepository tempAssignmentLogRepository;

    @Autowired
    private WipTrackingRepository wipTrackingRepository;

    @Autowired
    private BinRepository binRepository;

    @Autowired
    private TrackingValidationService validationService;

    /**
     * Process tracking request with two-phase workflow
     * Automatically detects assignment vs completion based on existing records
     */
    @Transactional
    public Map<String, Object> processTracking(TrackingRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Step 1: Run all validation checks
            TrackingValidationService.ValidationResult validationResult = validationService.runAllValidations(request);
            if (!validationResult.isValid()) {
                response.put("success", false);
                response.put("message", validationResult.getErrorMessage());
                return response;
            }

            Long empId = validationResult.getEmpId();

            // Step 2: Check if combination already exists in temp_active_assignments
            Optional<TempActiveAssignment> existingAssignment = tempActiveAssignmentRepository
                .findByMachineQrAndTrayQrAndEmpId(request.getMachineQr(), request.getTrayQr(), empId);

            if (existingAssignment.isPresent()) {
                // COMPLETION FLOW - Assignment exists, complete it
                return processCompletion(request, existingAssignment.get(), empId);
            } else {
                // ASSIGNMENT FLOW - No existing assignment, create new one
                // Run additional assignment-specific validations
                TrackingValidationService.ValidationResult assignmentValidation = validationService.runAssignmentValidations(request, empId);
                if (!assignmentValidation.isValid()) {
                    response.put("success", false);
                    response.put("message", assignmentValidation.getErrorMessage());
                    return response;
                }
                
                return processAssignment(request, empId);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error processing tracking: " + e.getMessage());
            return response;
        }
    }

    /**
     * ASSIGNMENT FLOW - Create new assignment
     */
    private Map<String, Object> processAssignment(TrackingRequest request, Long empId) {
        Map<String, Object> response = new HashMap<>();

        // Create new temp assignment
        TempActiveAssignment assignment = new TempActiveAssignment();
        assignment.setMachineQr(request.getMachineQr());
        assignment.setTrayQr(request.getTrayQr());
        assignment.setEmpId(empId);
        assignment.setAssignedBy(request.getSupervisorId());
        assignment.setStatus("assigned");

        tempActiveAssignmentRepository.save(assignment);

        // Log the assignment event
        logEvent(request, empId, "ASSIGN", "Worker assigned to machine and tray");

        response.put("success", true);
        response.put("flowType", "ASSIGNMENT");
        response.put("message", "Worker assigned to Machine & Tray");
        response.put("tempId", assignment.getTempId());
        response.put("machineQr", request.getMachineQr());
        response.put("employeeQr", request.getEmployeeQr());
        response.put("trayQr", request.getTrayQr());

        return response;
    }

    /**
     * COMPLETION FLOW - Complete existing assignment
     */
    private Map<String, Object> processCompletion(TrackingRequest request, TempActiveAssignment assignment, Long empId) {
        Map<String, Object> response = new HashMap<>();

        // Update assignment status to completed
        assignment.setStatus("completed");
        tempActiveAssignmentRepository.save(assignment);

        // Update bin.lastOperationId so node metrics can track which operation this bin last completed
        if (request.getOperationId() != null) {
            binRepository.findByQrCode(assignment.getTrayQr()).ifPresent(bin -> {
                bin.setLastOperationId(request.getOperationId());
                binRepository.save(bin);
            });
        }

        // Move to main wiptracking table
        WipTracking wipTracking = createWipTrackingRecord(assignment, request);
        wipTrackingRepository.save(wipTracking);

        // Log the completion event
        logEvent(request, empId, "COMPLETE", "Job completed and moved to main tables");

        // Clean up temp assignment (remove completed record)
        tempActiveAssignmentRepository.delete(assignment);

        response.put("success", true);
        response.put("flowType", "COMPLETION");
        response.put("message", "Job Completed & moved to main tables");
        response.put("wipTrackingId", wipTracking.getWipId());
        response.put("machineQr", request.getMachineQr());
        response.put("employeeQr", request.getEmployeeQr());
        response.put("trayQr", request.getTrayQr());

        return response;
    }

    /**
     * Create WipTracking record from completed assignment
     */
    private WipTracking createWipTrackingRecord(TempActiveAssignment assignment, TrackingRequest request) {
        WipTracking tracking = new WipTracking();
        
        // Generate new WIP ID
        Long maxId = wipTrackingRepository.findMaxWipTrackingId();
        tracking.setWipId(maxId != null ? maxId + 1 : 1L);
        
        tracking.setOperatorId(assignment.getEmpId());
        tracking.setStartTime(assignment.getStartTime());
        tracking.setEndTime(LocalDateTime.now());
        tracking.setStatus(request.getStatus());
        
        // Note: In production, you would parse machine_qr and tray_qr to get actual IDs
        // For now, we'll leave these fields null or implement parsing logic as needed
        
        return tracking;
    }

    /**
     * Log tracking events for audit trail
     */
    private void logEvent(TrackingRequest request, Long empId, String eventType, String notes) {
        TempAssignmentLog log = new TempAssignmentLog();
        log.setMachineQr(request.getMachineQr());
        log.setTrayQr(request.getTrayQr());
        log.setEmpId(empId);
        log.setEventType(eventType);
        log.setSupervisorId(request.getSupervisorId());
        log.setNotes(notes);

        tempAssignmentLogRepository.save(log);
    }
}