package com.cutm.smo.services;

import com.cutm.smo.dto.QrAssignmentRequest;
import com.cutm.smo.dto.TrackingRequest;
import com.cutm.smo.dto.MergingRequest;
import com.cutm.smo.models.*;
import com.cutm.smo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupervisorService {

    @Autowired
    private RoutingRepository routingRepository;

    @Autowired
    private StyleRepository styleRepository;

    @Autowired
    private StyleVariantRepository styleVariantRepository;

    @Autowired
    private ButtonsRepository buttonsRepository;

    @Autowired
    private GarmentRepository garmentRepository;
    
    @Autowired
    private WipTrackingRepository wipTrackingRepository;
    
    @Autowired
    private BinRepository binRepository;

    /**
     * Get all approved process plan numbers (routing IDs)
     */
    public List<String> getProcessPlans() {
        List<Routing> routings = routingRepository.findAll();
        return routings.stream()
                .filter(r -> "APPROVED".equalsIgnoreCase(r.getApprovalStatus()))
                .map(r -> String.valueOf(r.getRoutingId()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get all style numbers
     */
    public List<String> getStyles() {
        List<Style> styles = styleRepository.findAll();
        return styles.stream()
                .filter(s -> s.getStyleNo() != null && !s.getStyleNo().trim().isEmpty())
                .map(Style::getStyleNo)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get all sizes from style_variant table
     */
    public List<String> getSizes() {
        List<StyleVariant> variants = styleVariantRepository.findAll();
        return variants.stream()
                .filter(v -> v.getSize() != null && !v.getSize().trim().isEmpty())
                .map(StyleVariant::getSize)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get all GTG numbers from style_variant table
     */
    public List<String> getGtgNumbers() {
        List<StyleVariant> variants = styleVariantRepository.findAll();
        return variants.stream()
                .filter(v -> v.getGtgId() != null && !v.getGtgId().trim().isEmpty())
                .map(StyleVariant::getGtgId)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get all button numbers from buttons table
     */
    public List<String> getBtnNumbers() {
        List<Buttons> buttons = buttonsRepository.findAll();
        return buttons.stream()
                .filter(b -> b.getButtonCode() != null && !b.getButtonCode().trim().isEmpty())
                .map(Buttons::getButtonCode)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get all labels from style table (main_label and branding_label)
     */
    public List<String> getLabels() {
        List<Style> styles = styleRepository.findAll();
        Set<String> labels = new HashSet<>();
        
        for (Style style : styles) {
            if (style.getMainLabel() != null && !style.getMainLabel().trim().isEmpty()) {
                labels.add(style.getMainLabel());
            }
            if (style.getBrandingLabel() != null && !style.getBrandingLabel().trim().isEmpty()) {
                labels.add(style.getBrandingLabel());
            }
        }
        
        return labels.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Submit QR assignment
     * Creates a new garment record with the QR code and associated metadata
     */
    @Transactional
    public Map<String, Object> submitQrAssignment(QrAssignmentRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate required fields
            if (request.getProcessPlanNumber() == null || request.getProcessPlanNumber().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Process Plan Number is required");
                return response;
            }
            
            if (request.getQrCode() == null || request.getQrCode().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "QR Code is required");
                return response;
            }
            
            // Check if QR code already exists
            Optional<Garment> existingGarment = garmentRepository.findByQrCode(request.getQrCode());
            if (existingGarment.isPresent()) {
                response.put("success", false);
                response.put("message", "QR Code already assigned to another garment");
                return response;
            }
            
            // Find style variant ID based on provided data
            Long styleVariantId = findStyleVariantId(request);
            
            // Create new garment record
            Garment garment = new Garment();
            garment.setQrCode(request.getQrCode());
            garment.setStyleVariantId(styleVariantId);
            garment.setStatus("ASSIGNED");
            
            // Generate garment ID (you may want to use a sequence or auto-increment)
            Long maxId = garmentRepository.findMaxGarmentId();
            garment.setGarmentId(maxId != null ? maxId + 1 : 1L);
            
            garmentRepository.save(garment);
            
            response.put("success", true);
            response.put("message", "QR Code assigned successfully");
            response.put("garmentId", garment.getGarmentId());
            response.put("qrCode", garment.getQrCode());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error assigning QR code: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Find style variant ID based on the provided data
     * Returns null if no matching variant found
     */
    private Long findStyleVariantId(QrAssignmentRequest request) {
        List<StyleVariant> variants = styleVariantRepository.findAll();
        
        for (StyleVariant variant : variants) {
            boolean matches = true;
            
            // Match size if provided
            if (request.getSize() != null && !request.getSize().trim().isEmpty()) {
                if (variant.getSize() == null || !variant.getSize().equalsIgnoreCase(request.getSize())) {
                    matches = false;
                }
            }
            
            // Match GTG number if provided
            if (request.getGtgNumber() != null && !request.getGtgNumber().trim().isEmpty()) {
                if (variant.getGtgId() == null || !variant.getGtgId().equalsIgnoreCase(request.getGtgNumber())) {
                    matches = false;
                }
            }
            
            if (matches) {
                return variant.getStyleVariantId();
            }
        }
        
        return null;
    }
    
    /**
     * Submit tracking data
     * Creates a WIP tracking record
     */
    @Transactional
    public Map<String, Object> submitTracking(TrackingRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate required fields
            if (request.getMachineQr() == null || request.getMachineQr().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Machine QR is required");
                return response;
            }
            
            if (request.getEmployeeQr() == null || request.getEmployeeQr().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Employee QR is required");
                return response;
            }
            
            if (request.getTrayQr() == null || request.getTrayQr().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Tray QR is required");
                return response;
            }
            
            if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Status is required");
                return response;
            }
            
            // Create WIP tracking record
            // Note: This is a simplified implementation. In production, you would:
            // 1. Parse QR codes to get actual IDs
            // 2. Validate that machine, employee, and bin exist
            // 3. Link to actual operation being tracked
            
            WipTracking tracking = new WipTracking();
            tracking.setStatus(request.getStatus());
            tracking.setStartTime(LocalDateTime.now());
            
            // Generate tracking ID
            Long maxId = wipTrackingRepository.findMaxWipTrackingId();
            tracking.setWipId(maxId != null ? maxId + 1 : 1L);
            
            wipTrackingRepository.save(tracking);
            
            response.put("success", true);
            response.put("message", "Tracking submitted successfully");
            response.put("trackingId", tracking.getWipId());
            response.put("machineQr", request.getMachineQr());
            response.put("employeeQr", request.getEmployeeQr());
            response.put("trayQr", request.getTrayQr());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error submitting tracking: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Submit merging data
     * Merges two bins/tubs into one
     */
    @Transactional
    public Map<String, Object> submitMerging(MergingRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate required fields
            if (request.getTub1Qr() == null || request.getTub1Qr().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Tub 1 QR is required");
                return response;
            }
            
            if (request.getTub1Description() == null || request.getTub1Description().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Tub 1 Description is required");
                return response;
            }
            
            if (request.getTub2Qr() == null || request.getTub2Qr().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Tub 2 QR is required");
                return response;
            }
            
            if (request.getTub2Description() == null || request.getTub2Description().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Tub 2 Description is required");
                return response;
            }
            
            // Check if trying to merge same tub
            if (request.getTub1Qr().equals(request.getTub2Qr())) {
                response.put("success", false);
                response.put("message", "Cannot merge the same tub");
                return response;
            }
            
            // Find bins by QR codes
            Optional<Bin> bin1Opt = binRepository.findByQrCode(request.getTub1Qr());
            Optional<Bin> bin2Opt = binRepository.findByQrCode(request.getTub2Qr());
            
            if (!bin1Opt.isPresent()) {
                response.put("success", false);
                response.put("message", "Tub 1 not found");
                return response;
            }
            
            if (!bin2Opt.isPresent()) {
                response.put("success", false);
                response.put("message", "Tub 2 not found");
                return response;
            }
            
            Bin bin1 = bin1Opt.get();
            Bin bin2 = bin2Opt.get();
            
            // Merge logic: Update bin1 with combined quantity, mark bin2 as merged
            Integer totalQuantity = (bin1.getQty() != null ? bin1.getQty() : 0) + 
                                   (bin2.getQty() != null ? bin2.getQty() : 0);
            
            bin1.setQty(totalQuantity);
            bin1.setStatus("MERGED");
            
            bin2.setStatus("MERGED_INTO_" + bin1.getBinId());
            
            binRepository.save(bin1);
            binRepository.save(bin2);
            
            response.put("success", true);
            response.put("message", "Tubs merged successfully");
            response.put("mergedBinId", bin1.getBinId());
            response.put("totalQuantity", totalQuantity);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error merging tubs: " + e.getMessage());
        }
        
        return response;
    }
}
