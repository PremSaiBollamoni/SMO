package com.cutm.smo.services;

import com.cutm.smo.dto.ProcessPlanResponse;
import com.cutm.smo.dto.ProcessPlanStepRequest;
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
    private static final String STATUS_UNDER_REVIEW = "UNDER REVIEW";
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
    public ProcessPlanResponse createDraftProcessPlan(Long routingId, Long productId, List<Map<String, Object>> stepsRaw) {
        if (routingId == null || routingId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "routingId must be a positive number");
        }
        if (productId == null || productId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId must be a positive number");
        }
        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId does not exist");
        }
        if (routingRepository.existsById(routingId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "routingId already exists");
        }

        List<ProcessPlanStepRequest> steps = parseAndValidateStrictSteps(stepsRaw);

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
    public ProcessPlanResponse cloneDraftFromExisting(Long sourceRoutingId, Long newRoutingId, Long productId) {
        Routing source = routingRepository.findById(sourceRoutingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source routing not found"));
        if (newRoutingId == null || newRoutingId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "newRoutingId must be a positive number");
        }
        if (routingRepository.existsById(newRoutingId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "newRoutingId already exists");
        }

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
            clonedOperation.setIsParallel(sourceOp.getIsParallel());
            clonedOperation.setMergePoint(sourceOp.getMergePoint());
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
        return response;
    }

    public List<ProcessPlanResponse> getProcessPlansByProduct(Long productId) {
        List<Routing> routings = routingRepository.findByProductIdOrderByRoutingIdDesc(productId);
        
        List<ProcessPlanResponse> responses = new ArrayList<>();
        for (Routing routing : routings) {
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
            newOp.setIsParallel(sourceOp.getIsParallel());
            newOp.setMergePoint(sourceOp.getMergePoint());
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
            operation.setIsParallel(step.getIsParallel());
            operation.setMergePoint(step.getMergePoint());
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

        Set<String> allowedKeys = Set.of("name", "description", "sequence", "is_parallel", "merge_point", "stage_group", "standard_time");
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
                    || !row.containsKey("is_parallel")
                    || !row.containsKey("merge_point")
                    || !row.containsKey("stage_group")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Step " + index + " must contain name, description, sequence, is_parallel, merge_point, stage_group");
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
        if (step.getIsParallel() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Step " + index + ": is_parallel is required");
        }
        if (step.getMergePoint() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Step " + index + ": merge_point is required");
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
        opResp.setIsParallel(op.getIsParallel());
        opResp.setMergePoint(op.getMergePoint());
        opResp.setStageGroup(op.getStageGroup());
        opResp.setStandardTime(op.getStandardTime());
        return opResp;
    }
}
