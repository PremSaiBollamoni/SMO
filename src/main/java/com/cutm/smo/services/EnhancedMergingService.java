package com.cutm.smo.services;

import com.cutm.smo.dto.MergingRequest;
import com.cutm.smo.models.*;
import com.cutm.smo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EnhancedMergingService {

    @Autowired
    private BinRepository binRepository;

    @Autowired
    private TempBinMergeRepository tempBinMergeRepository;

    @Autowired
    private BinMergeHistoryRepository binMergeHistoryRepository;

    /**
     * Enhanced merging with compatibility validation and multi-table updates
     */
    @Transactional
    public Map<String, Object> processEnhancedMerging(MergingRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Step 1: Basic validation
            Map<String, Object> validationResult = validateBasicRequirements(request);
            if (!(Boolean) validationResult.get("success")) {
                return validationResult;
            }

            // Step 2: Find bins by QR codes
            Optional<Bin> bin1Opt = binRepository.findByQrCode(request.getTub1Qr());
            Optional<Bin> bin2Opt = binRepository.findByQrCode(request.getTub2Qr());

            if (!bin1Opt.isPresent()) {
                response.put("success", false);
                response.put("message", "Tub 1 not found in system");
                response.put("errorType", "BIN_NOT_FOUND");
                return response;
            }

            if (!bin2Opt.isPresent()) {
                response.put("success", false);
                response.put("message", "Tub 2 not found in system");
                response.put("errorType", "BIN_NOT_FOUND");
                return response;
            }

            Bin targetBin = bin1Opt.get(); // Tub 1 (target)
            Bin sourceBin = bin2Opt.get(); // Tub 2 (source)

            // Step 3: Compatibility validation
            Map<String, Object> compatibilityResult = validateCompatibility(targetBin, sourceBin);
            if (!(Boolean) compatibilityResult.get("success")) {
                return compatibilityResult;
            }

            // Step 4: Execute multi-table merge transaction
            return executeMergeTransaction(request, targetBin, sourceBin);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error processing merge: " + e.getMessage());
            response.put("errorType", "SYSTEM_ERROR");
            return response;
        }
    }

    /**
     * Validate basic requirements
     */
    private Map<String, Object> validateBasicRequirements(MergingRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (request.getTub1Qr() == null || request.getTub1Qr().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Tub 1 QR is required");
            response.put("errorType", "VALIDATION_ERROR");
            return response;
        }

        if (request.getTub2Qr() == null || request.getTub2Qr().trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Tub 2 QR is required");
            response.put("errorType", "VALIDATION_ERROR");
            return response;
        }

        if (request.getTub1Qr().equals(request.getTub2Qr())) {
            response.put("success", false);
            response.put("message", "Cannot merge the same tub");
            response.put("errorType", "VALIDATION_ERROR");
            return response;
        }

        response.put("success", true);
        return response;
    }

    /**
     * Validate compatibility between bins
     */
    private Map<String, Object> validateCompatibility(Bin targetBin, Bin sourceBin) {
        Map<String, Object> response = new HashMap<>();

        // Check if both bins are ACTIVE
        if (!"ACTIVE".equalsIgnoreCase(targetBin.getStatus())) {
            response.put("success", false);
            response.put("message", "Tub 1 is not active (Status: " + targetBin.getStatus() + ")");
            response.put("errorType", "STATUS_ERROR");
            return response;
        }

        if (!"ACTIVE".equalsIgnoreCase(sourceBin.getStatus())) {
            response.put("success", false);
            response.put("message", "Tub 2 is not active (Status: " + sourceBin.getStatus() + ")");
            response.put("errorType", "STATUS_ERROR");
            return response;
        }

        // Check style variant compatibility
        if (targetBin.getStyleVariantId() == null || sourceBin.getStyleVariantId() == null) {
            response.put("success", false);
            response.put("message", "One or both tubs missing style variant information");
            response.put("errorType", "COMPATIBILITY_ERROR");
            return response;
        }

        if (!targetBin.getStyleVariantId().equals(sourceBin.getStyleVariantId())) {
            response.put("success", false);
            response.put("message", "Incompatible bins: Different style/size/color variants");
            response.put("errorType", "COMPATIBILITY_ERROR");
            response.put("targetVariantId", targetBin.getStyleVariantId());
            response.put("sourceVariantId", sourceBin.getStyleVariantId());
            return response;
        }

        // Check quantities are valid
        Integer targetQty = targetBin.getQty() != null ? targetBin.getQty() : 0;
        Integer sourceQty = sourceBin.getQty() != null ? sourceBin.getQty() : 0;

        if (sourceQty <= 0) {
            response.put("success", false);
            response.put("message", "Source tub has no quantity to transfer");
            response.put("errorType", "QUANTITY_ERROR");
            return response;
        }

        response.put("success", true);
        response.put("targetQty", targetQty);
        response.put("sourceQty", sourceQty);
        return response;
    }

    /**
     * Execute the multi-table merge transaction
     */
    private Map<String, Object> executeMergeTransaction(MergingRequest request, Bin targetBin, Bin sourceBin) {
        Map<String, Object> response = new HashMap<>();

        Integer targetQty = targetBin.getQty() != null ? targetBin.getQty() : 0;
        Integer sourceQty = sourceBin.getQty() != null ? sourceBin.getQty() : 0;
        Integer totalQty = targetQty + sourceQty;
        Long supervisorId = request.getSupervisorId() != null ? request.getSupervisorId() : 1004L; // Default supervisor

        // Step 1: Insert into temp_bin_merges
        TempBinMerge tempMerge = new TempBinMerge();
        tempMerge.setSourceBinQr(request.getTub2Qr());
        tempMerge.setTargetBinQr(request.getTub1Qr());
        tempMerge.setSourceBinId(sourceBin.getBinId());
        tempMerge.setTargetBinId(targetBin.getBinId());
        tempMerge.setQtyTransferred(sourceQty);
        tempMerge.setMergedBy(supervisorId);
        tempMerge.setNotes(buildMergeNotes(request, targetBin, sourceBin));
        
        TempBinMerge savedTempMerge = tempBinMergeRepository.save(tempMerge);

        // Step 2: Update target bin quantity
        targetBin.setQty(totalQty);
        targetBin.setStatus("MERGED"); // Update status to indicate it's been merged
        binRepository.save(targetBin);

        // Step 3: Update source bin status
        sourceBin.setStatus("MERGED_INTO_" + targetBin.getBinId());
        sourceBin.setQty(0); // Set quantity to 0 as it's been transferred
        binRepository.save(sourceBin);

        // Step 4: Insert into bin_merge_history
        BinMergeHistory mergeHistory = new BinMergeHistory();
        Long nextMergeId = binMergeHistoryRepository.getNextMergeId();
        mergeHistory.setMergeId(nextMergeId);
        mergeHistory.setSourceBinId(sourceBin.getBinId());
        mergeHistory.setTargetBinId(targetBin.getBinId());
        mergeHistory.setQtyTransferred(sourceQty);
        mergeHistory.setMergedByEmpId(supervisorId);
        mergeHistory.setMergedAt(LocalDateTime.now());
        mergeHistory.setNotes(tempMerge.getNotes());
        
        BinMergeHistory savedHistory = binMergeHistoryRepository.save(mergeHistory);

        // Step 5: Build success response
        response.put("success", true);
        response.put("message", "Tub 2 merged into Tub 1 successfully");
        response.put("mergedBinId", targetBin.getBinId());
        response.put("totalQuantity", totalQty);
        response.put("qtyTransferred", sourceQty);
        response.put("tempMergeId", savedTempMerge.getMergeTempId());
        response.put("historyMergeId", savedHistory.getMergeId());
        response.put("mergedAt", savedTempMerge.getMergedAt());
        response.put("mergedBy", supervisorId);

        return response;
    }

    /**
     * Build merge notes for audit trail
     */
    private String buildMergeNotes(MergingRequest request, Bin targetBin, Bin sourceBin) {
        StringBuilder notes = new StringBuilder();
        notes.append("Enhanced Merge Operation:\n");
        notes.append("Target: ").append(request.getTub1Qr()).append(" (").append(request.getTub1Description()).append(")\n");
        notes.append("Source: ").append(request.getTub2Qr()).append(" (").append(request.getTub2Description()).append(")\n");
        notes.append("Style Variant ID: ").append(targetBin.getStyleVariantId()).append("\n");
        notes.append("Target Qty Before: ").append(targetBin.getQty()).append("\n");
        notes.append("Source Qty Transferred: ").append(sourceBin.getQty()).append("\n");
        notes.append("Target Qty After: ").append((targetBin.getQty() != null ? targetBin.getQty() : 0) + (sourceBin.getQty() != null ? sourceBin.getQty() : 0));
        
        if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
            notes.append("\nAdditional Notes: ").append(request.getNotes());
        }
        
        return notes.toString();
    }
}