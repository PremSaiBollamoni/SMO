package com.cutm.smo.services;

import com.cutm.smo.dto.ProcessPlanResponse;
import com.cutm.smo.dto.ProcessPlanStepRequest;
import com.cutm.smo.dto.WorkflowEdge;
import com.cutm.smo.models.*;
import com.cutm.smo.repositories.*;
import com.cutm.smo.util.LoggingUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class ProcessPlanService {
    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_UNDER_REVIEW = "UNDER_REVIEW";
    private static final String STATUS_APPROVED = "APPROVED";
    private static final String STATUS_REJECTED = "REJECTED";

    private final RoutingRepository routingRepository;
    private final RoutingStepRepository routingStepRepository;
    private final OperationRepository operationRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    public ProcessPlanService(RoutingRepository routingRepository, RoutingStepRepository routingStepRepository,
            OperationRepository operationRepository, ProductRepository productRepository) {
        this.routingRepository = routingRepository;
        this.routingStepRepository = routingStepRepository;
        this.operationRepository = operationRepository;
        this.productRepository = productRepository;
        this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    @Transactional
    public ProcessPlanResponse createDraftProcessPlan(Long productId, List<Map<String, Object>> stepsRaw) {
        if (productId == null || productId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId must be a positive number");
        }
        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId does not exist");
        }

        List<ProcessPlanStepRequest> steps = parseAndValidateStrictSteps(stepsRaw);

        // Auto-generate routing_id
        Long routingId = routingRepository.findMaxRoutingId() + 1;

        Routing draftRouting = new Routing();
        draftRouting.setRoutingId(routingId);
        draftRouting.setProductId(productId);
        draftRouting.setVersion(routingRepository.findMaxVersionByProductId(productId) + 1);
        draftRouting.setStatus(STATUS_DRAFT);
        draftRouting.setApprovalStatus(STATUS_UNDER_REVIEW);
        draftRouting.setApprovedBy(null);
        draftRouting.setApprovedAt(null);
        draftRouting.setPreviousRoutingId(null);
        draftRouting = routingRepository.save(draftRouting);

        createOperationsAndSteps(draftRouting.getRoutingId(), steps);
        return getProcessPlan(draftRouting.getRoutingId());
    }

    @Transactional
    public ProcessPlanResponse cloneDraftFromExisting(Long sourceRoutingId, Long productId) {
        Routing source = routingRepository.findById(sourceRoutingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source routing not found"));

        Long targetProductId = productId == null ? source.getProductId() : productId;
        if (targetProductId == null || targetProductId <= 0 || !productRepository.existsById(targetProductId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId does not exist");
        }

        List<RoutingStep> sourceSteps = routingStepRepository.findByRoutingIdOrderByRoutingStepIdAsc(sourceRoutingId);
        if (sourceSteps.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source plan has no steps");
        }
        Set<Long> operationIds = sourceSteps.stream().map(RoutingStep::getOperationId).collect(Collectors.toSet());
        Map<Long, Operation> operationMap = operationRepository.findAllById(operationIds).stream()
                .collect(Collectors.toMap(Operation::getOperationId, o -> o));

        // Auto-generate new routing_id
        Long newRoutingId = routingRepository.findMaxRoutingId() + 1;

        Routing draftRouting = new Routing();
        draftRouting.setRoutingId(newRoutingId);
        draftRouting.setProductId(targetProductId);
        draftRouting.setVersion(routingRepository.findMaxVersionByProductId(targetProductId) + 1);
        draftRouting.setStatus(STATUS_DRAFT);
        draftRouting.setApprovalStatus(STATUS_UNDER_REVIEW);
        draftRouting.setPreviousRoutingId(sourceRoutingId);
        draftRouting.setApprovedBy(null);
        draftRouting.setApprovedAt(null);
        routingRepository.save(draftRouting);

        Long nextOperationId = operationRepository.findMaxOperationId() + 1;
        Long nextRoutingStepId = routingStepRepository.findMaxRoutingStepId() + 1;
        for (RoutingStep sourceStep : sourceSteps) {
            Operation sourceOp = operationMap.get(sourceStep.getOperationId());
            if (sourceOp == null) {
                continue;
            }
            Operation clonedOperation = new Operation();
            clonedOperation.setOperationId(nextOperationId++);
            clonedOperation.setName(sourceOp.getName());
            clonedOperation.setDescription(sourceOp.getDescription());
            clonedOperation.setSequence(sourceOp.getSequence());
            clonedOperation.setOperationType(sourceOp.getOperationType());
            clonedOperation.setStageGroup(sourceOp.getStageGroup());
            clonedOperation.setStandardTime(sourceOp.getStandardTime());
            operationRepository.save(clonedOperation);

            RoutingStep newStep = new RoutingStep();
            newStep.setRoutingStepId(nextRoutingStepId++);
            newStep.setRoutingId(newRoutingId);
            newStep.setOperationId(clonedOperation.getOperationId());
            newStep.setStageGroup(clonedOperation.getStageGroup());
            routingStepRepository.save(newStep);
        }

        return getProcessPlan(newRoutingId);
    }

    public ProcessPlanResponse getProcessPlan(Long routingId) {
        Optional<Routing> routingOpt = routingRepository.findById(routingId);
        if (routingOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Routing not found");
        }
        
        Routing routing = routingOpt.get();
        List<RoutingStep> steps = routingStepRepository.findByRoutingIdOrderByRoutingStepIdAsc(routingId);
        Set<Long> operationIds = steps.stream().map(RoutingStep::getOperationId).collect(Collectors.toSet());
        Map<Long, Operation> operationById = operationRepository.findAllById(operationIds).stream()
                .collect(Collectors.toMap(Operation::getOperationId, o -> o));
        
        List<ProcessPlanResponse.OperationResponse> operations = new ArrayList<>();
        for (RoutingStep step : steps) {
            Operation op = operationById.get(step.getOperationId());
            if (op != null) {
                operations.add(toOperationResponse(op));
            }
        }
        operations.sort(Comparator.comparing(ProcessPlanResponse.OperationResponse::getSequence, Comparator.nullsLast(Integer::compareTo)));
        
        // Build explicit edges from routing table
        List<WorkflowEdge> edges = buildExplicitEdges(steps, operationById);
        
        ProcessPlanResponse response = new ProcessPlanResponse();
        response.setRoutingId(routing.getRoutingId());
        response.setProductId(routing.getProductId());
        response.setVersion(routing.getVersion());
        response.setStatus(routing.getStatus());
        response.setApprovalStatus(routing.getApprovalStatus());
        response.setApprovedBy(routing.getApprovedBy());
        response.setApprovedAt(routing.getApprovedAt());
        response.setPreviousRoutingId(routing.getPreviousRoutingId());
        response.setOperations(operations);
        response.setEdges(edges);
        return response;
    }

    /**
     * Build explicit edges from routing table relationships.
     * Uses routing order and operation types to determine dependencies.
     */
    private List<WorkflowEdge> buildExplicitEdges(List<RoutingStep> steps, Map<Long, Operation> operationById) {
        List<WorkflowEdge> edges = new ArrayList<>();
        
        if (steps.isEmpty()) {
            return edges;
        }
        
        // Build a map of operation_id to operation for quick lookup
        Map<Long, Operation> opMap = new HashMap<>(operationById);
        
        // Process each step and determine its outgoing edges
        for (int i = 0; i < steps.size(); i++) {
            RoutingStep current = steps.get(i);
            Operation currentOp = opMap.get(current.getOperationId());
            
            if (currentOp == null) continue;
            
            // Determine outgoing edges based on operation type
            if (currentOp.isSequential()) {
                // Sequential: connect to next operation
                if (i + 1 < steps.size()) {
                    RoutingStep next = steps.get(i + 1);
                    Operation nextOp = opMap.get(next.getOperationId());
                    if (nextOp != null) {
                        if (nextOp.isParallelBranch()) {
                            // Branching: connect to all parallel ops in next stage
                            int nextStage = next.getStageGroup();
                            log.debug("[buildExplicitEdges] Sequential {} branches to stage {}", currentOp.getName(), nextStage);
                            for (int j = i + 1; j < steps.size(); j++) {
                                RoutingStep candidate = steps.get(j);
                                Operation candidateOp = opMap.get(candidate.getOperationId());
                                if (candidateOp != null && candidateOp.isParallelBranch() && candidate.getStageGroup() == nextStage) {
                                    edges.add(new WorkflowEdge(
                                        currentOp.getOperationId(),
                                        candidateOp.getOperationId(),
                                        currentOp.getName(),
                                        candidateOp.getName(),
                                        "branch"
                                    ));
                                    log.debug("[buildExplicitEdges]   ✓ BRANCH EDGE: {} -> {}", currentOp.getName(), candidateOp.getName());
                                } else if (candidate.getStageGroup() > nextStage) {
                                    break;
                                }
                            }
                        } else {
                            // Simple sequential
                            edges.add(new WorkflowEdge(
                                currentOp.getOperationId(),
                                nextOp.getOperationId(),
                                currentOp.getName(),
                                nextOp.getName(),
                                "sequential"
                            ));
                            log.debug("[buildExplicitEdges]   ✓ SEQUENTIAL EDGE: {} -> {}", currentOp.getName(), nextOp.getName());
                        }
                    }
                }
            } else if (currentOp.isParallelBranch()) {
                // Parallel branch: connect to corresponding merge
                String branchSuffix = currentOp.getName();
                if (branchSuffix.endsWith("_LINE")) {
                    branchSuffix = branchSuffix.substring(0, branchSuffix.length() - 5);
                }
                String expectedMergeName = "MERGE_" + branchSuffix;
                
                log.debug("[buildExplicitEdges] Processing parallel branch: {} -> looking for {}", currentOp.getName(), expectedMergeName);
                
                // Find matching merge
                boolean foundMerge = false;
                for (int j = i + 1; j < steps.size(); j++) {
                    RoutingStep candidate = steps.get(j);
                    Operation candidateOp = opMap.get(candidate.getOperationId());
                    if (candidateOp != null && candidateOp.isMerge()) {
                        log.debug("[buildExplicitEdges]   Checking merge candidate: {}", candidateOp.getName());
                        if (candidateOp.getName().equals(expectedMergeName)) {
                            edges.add(new WorkflowEdge(
                                currentOp.getOperationId(),
                                candidateOp.getOperationId(),
                                currentOp.getName(),
                                candidateOp.getName(),
                                "merge"
                            ));
                            log.debug("[buildExplicitEdges]   ✓ EDGE ADDED: {} -> {}", currentOp.getName(), candidateOp.getName());
                            foundMerge = true;
                            break;
                        }
                    }
                }
                if (!foundMerge) {
                    log.warn("[buildExplicitEdges] ⚠ No merge found for {}", currentOp.getName());
                }
            } else if (currentOp.isMerge()) {
                // Merge: connect to next non-merge operation
                for (int j = i + 1; j < steps.size(); j++) {
                    RoutingStep candidate = steps.get(j);
                    Operation candidateOp = opMap.get(candidate.getOperationId());
                    if (candidateOp != null && !candidateOp.isMerge()) {
                        edges.add(new WorkflowEdge(
                            currentOp.getOperationId(),
                            candidateOp.getOperationId(),
                            currentOp.getName(),
                            candidateOp.getName(),
                            "merge_convergence"
                        ));
                        break;
                    }
                }
            }
        }
        
        // Hardcoded edges for missing branch->merge mappings
        // These ensure COLLAR_CUFF_LINE and POCKET_PLACKET_LINE connect to their corresponding merges
        addHardcodedEdges(edges, opMap, steps);
        
        log.info("[buildExplicitEdges] Total edges built: {}", edges.size());
        for (WorkflowEdge edge : edges) {
            log.info("[buildExplicitEdges]   {} -> {} ({})", edge.getFromName(), edge.getToName(), edge.getEdgeType());
        }
        
        return edges;
    }
    
    /**
     * Add hardcoded edges for branch->merge mappings that may not be detected by the main logic.
     * This ensures all parallel branches connect to their corresponding merge points.
     */
    private void addHardcodedEdges(List<WorkflowEdge> edges, Map<Long, Operation> opMap, List<RoutingStep> steps) {
        // Define explicit branch->merge mappings
        String[][] branchMergePairs = {
            {"COLLAR_CUFF_LINE", "MERGE_COLLAR"},
            {"POCKET_PLACKET_LINE", "MERGE_POCKET"},
            {"SLEEVE_LINE", "MERGE_SLEEVE"},
            {"BODY_LINE", "MERGE_BODY"}
        };
        
        for (String[] pair : branchMergePairs) {
            final String branchName = pair[0];
            final String mergeName = pair[1];
            
            // Find branch and merge operations
            Operation branchOp = null;
            Operation mergeOp = null;
            
            for (Operation op : opMap.values()) {
                if (op.getName().equals(branchName)) {
                    branchOp = op;
                }
                if (op.getName().equals(mergeName)) {
                    mergeOp = op;
                }
            }
            
            // If both exist and edge doesn't already exist, add it
            if (branchOp != null && mergeOp != null) {
                final Operation finalBranchOp = branchOp;
                final Operation finalMergeOp = mergeOp;
                
                boolean edgeExists = edges.stream()
                    .anyMatch(e -> e.getFromOperationId().equals(finalBranchOp.getOperationId()) 
                        && e.getToOperationId().equals(finalMergeOp.getOperationId()));
                
                if (!edgeExists) {
                    edges.add(new WorkflowEdge(
                        branchOp.getOperationId(),
                        mergeOp.getOperationId(),
                        branchName,
                        mergeName,
                        "merge"
                    ));
                    log.debug("[addHardcodedEdges] ✓ HARDCODED EDGE ADDED: {} -> {}", branchName, mergeName);
                }
            }
        }
    }

    public List<ProcessPlanResponse> getProcessPlansByProduct(Long productId) {
        List<Routing> routings = routingRepository.findByProductIdOrderByRoutingIdDesc(productId);
        
        List<ProcessPlanResponse> responses = new ArrayList<>();
        for (Routing routing : routings) {
            responses.add(getProcessPlan(routing.getRoutingId()));
        }
        return responses;
    }

    public List<ProcessPlanResponse> getPendingProcessPlans() {
        List<Routing> pendingRoutings = routingRepository.findByApprovalStatusOrderByRoutingIdDesc(STATUS_UNDER_REVIEW);
        
        List<ProcessPlanResponse> responses = new ArrayList<>();
        for (Routing routing : pendingRoutings) {
            responses.add(getProcessPlan(routing.getRoutingId()));
        }
        return responses;
    }

    public List<ProcessPlanResponse> getApprovedProcessPlans() {
        List<Routing> approved = routingRepository.findByStatusOrderByRoutingIdDesc(STATUS_APPROVED);
        List<ProcessPlanResponse> responses = new ArrayList<>();
        for (Routing routing : approved) {
            responses.add(getProcessPlan(routing.getRoutingId()));
        }
        return responses;
    }

    @Transactional
    public ProcessPlanResponse approveProcessPlan(Long routingId, Long approvedBy) {
        Routing draftRouting = routingRepository.findById(routingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Draft routing not found"));

        if (!STATUS_UNDER_REVIEW.equalsIgnoreCase(draftRouting.getApprovalStatus())
                && !STATUS_DRAFT.equalsIgnoreCase(draftRouting.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only draft/pending plans can be approved");
        }
        if (approvedBy == null || approvedBy <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "approvedBy must be a positive number");
        }

        List<RoutingStep> draftSteps = routingStepRepository.findByRoutingIdOrderByRoutingStepIdAsc(routingId);
        if (draftSteps.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Draft plan has no steps");
        }

        Set<Long> operationIds = draftSteps.stream().map(RoutingStep::getOperationId).collect(Collectors.toSet());
        Map<Long, Operation> draftOpsById = operationRepository.findAllById(operationIds).stream()
                .collect(Collectors.toMap(Operation::getOperationId, o -> o));

        Long newRoutingId = routingRepository.findMaxRoutingId() + 1;
        Routing activeRouting = new Routing();
        activeRouting.setRoutingId(newRoutingId);
        activeRouting.setProductId(draftRouting.getProductId());
        activeRouting.setVersion((draftRouting.getVersion() == null ? 0 : draftRouting.getVersion()) + 1);
        activeRouting.setStatus(STATUS_APPROVED);
        activeRouting.setApprovalStatus(STATUS_APPROVED);
        activeRouting.setApprovedBy(approvedBy);
        activeRouting.setApprovedAt(LocalDateTime.now());
        activeRouting.setPreviousRoutingId(draftRouting.getRoutingId());
        activeRouting = routingRepository.save(activeRouting);

        Long nextOperationId = operationRepository.findMaxOperationId() + 1;
        Long nextRoutingStepId = routingStepRepository.findMaxRoutingStepId() + 1;
        for (RoutingStep draftStep : draftSteps) {
            Operation sourceOp = draftOpsById.get(draftStep.getOperationId());
            if (sourceOp == null) {
                continue;
            }
            Operation newOp = new Operation();
            newOp.setOperationId(nextOperationId++);
            newOp.setName(sourceOp.getName());
            newOp.setDescription(sourceOp.getDescription());
            newOp.setSequence(sourceOp.getSequence());
            newOp.setOperationType(sourceOp.getOperationType());
            newOp.setStageGroup(sourceOp.getStageGroup());
            newOp.setStandardTime(sourceOp.getStandardTime());
            newOp = operationRepository.save(newOp);

            RoutingStep newStep = new RoutingStep();
            newStep.setRoutingStepId(nextRoutingStepId++);
            newStep.setRoutingId(activeRouting.getRoutingId());
            newStep.setOperationId(newOp.getOperationId());
            newStep.setStageGroup(newOp.getStageGroup());
            routingStepRepository.save(newStep);
        }

        draftRouting.setApprovalStatus(STATUS_APPROVED);
        draftRouting.setApprovedBy(approvedBy);
        draftRouting.setApprovedAt(LocalDateTime.now());
        draftRouting.setStatus(STATUS_APPROVED);
        routingRepository.save(draftRouting);

        return getProcessPlan(activeRouting.getRoutingId());
    }

    @Transactional
    public ProcessPlanResponse rejectProcessPlan(Long routingId, Long approvedBy) {
        Routing draftRouting = routingRepository.findById(routingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Draft routing not found"));

        if (approvedBy == null || approvedBy <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "approvedBy must be a positive number");
        }
        if (!STATUS_UNDER_REVIEW.equalsIgnoreCase(draftRouting.getApprovalStatus())
                && !STATUS_DRAFT.equalsIgnoreCase(draftRouting.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only draft/pending plans can be rejected");
        }

        draftRouting.setApprovalStatus(STATUS_REJECTED);
        draftRouting.setApprovedBy(approvedBy);
        draftRouting.setApprovedAt(LocalDateTime.now());
        draftRouting.setStatus(STATUS_REJECTED);
        routingRepository.save(draftRouting);
        return getProcessPlan(draftRouting.getRoutingId());
    }

    private void createOperationsAndSteps(Long routingId, List<ProcessPlanStepRequest> steps) {
        Long nextOperationId = operationRepository.findMaxOperationId() + 1;
        Long nextRoutingStepId = routingStepRepository.findMaxRoutingStepId() + 1;

        for (ProcessPlanStepRequest step : steps) {
            Operation operation = new Operation();
            operation.setOperationId(nextOperationId++);
            operation.setName(step.getName().trim());
            operation.setDescription(step.getDescription().trim());
            operation.setSequence(step.getSequence());
            operation.setOperationType(step.getOperationType());
            operation.setStageGroup(step.getStageGroup());
            operation.setStandardTime(step.getStandardTime());
            operation = operationRepository.save(operation);

            RoutingStep routingStep = new RoutingStep();
            routingStep.setRoutingStepId(nextRoutingStepId++);
            routingStep.setRoutingId(routingId);
            routingStep.setOperationId(operation.getOperationId());
            routingStep.setStageGroup(operation.getStageGroup());
            routingStepRepository.save(routingStep);
        }
    }

    private List<ProcessPlanStepRequest> parseAndValidateStrictSteps(List<Map<String, Object>> stepsRaw) {
        if (stepsRaw == null || stepsRaw.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body must be a non-empty JSON array");
        }

        Set<String> allowedKeys = Set.of("name", "description", "sequence", "operation_type", "stage_group", "standard_time");
        List<ProcessPlanStepRequest> steps = new ArrayList<>();
        int index = 0;
        for (Map<String, Object> row : stepsRaw) {
            index++;
            if (row == null || row.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Step " + index + " is empty");
            }
            Set<String> keys = new HashSet<>(row.keySet());
            keys.removeAll(allowedKeys);
            if (!keys.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Step " + index + " has unsupported fields: " + keys);
            }
            if (!row.containsKey("name")
                    || !row.containsKey("description")
                    || !row.containsKey("sequence")
                    || !row.containsKey("operation_type")
                    || !row.containsKey("stage_group")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Step " + index + " must contain name, description, sequence, operation_type, stage_group");
            }

            ProcessPlanStepRequest step;
            try {
                step = objectMapper.convertValue(row, ProcessPlanStepRequest.class);
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Step " + index + " has invalid field types");
            }
            validateStep(step, index);
            steps.add(step);
        }
        return steps;
    }

    private void validateStep(ProcessPlanStepRequest step, int index) {
        if (step.getName() == null || step.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Step " + index + ": name is required");
        }
        if (step.getDescription() == null || step.getDescription().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Step " + index + ": description is required");
        }
        if (step.getSequence() == null || step.getSequence() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Step " + index + ": sequence must be > 0");
        }
        if (step.getOperationType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Step " + index + ": operation_type is required");
        }
        if (step.getStageGroup() == null || step.getStageGroup() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Step " + index + ": stage_group must be > 0");
        }
    }

    private ProcessPlanResponse.OperationResponse toOperationResponse(Operation op) {
        ProcessPlanResponse.OperationResponse opResp = new ProcessPlanResponse.OperationResponse();
        opResp.setOperationId(op.getOperationId());
        opResp.setName(op.getName());
        opResp.setDescription(op.getDescription());
        opResp.setSequence(op.getSequence());
        opResp.setOperationType(op.getOperationType());
        opResp.setStageGroup(op.getStageGroup());
        opResp.setStandardTime(op.getStandardTime());
        return opResp;
    }
}
