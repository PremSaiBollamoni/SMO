package com.cutm.smo.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bin_merge_history")
public class BinMergeHistory {
    @Id
    @Column(name = "merge_id")
    private Long mergeId;
    
    @Column(name = "source_bin_id")
    private Long sourceBinId;
    
    @Column(name = "target_bin_id")
    private Long targetBinId;
    
    @Column(name = "qty_transferred")
    private Integer qtyTransferred;
    
    @Column(name = "merged_by_emp_id")
    private Long mergedByEmpId;
    
    @Column(name = "merged_at")
    private LocalDateTime mergedAt;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public Long getMergeId() { return mergeId; }
    public void setMergeId(Long mergeId) { this.mergeId = mergeId; }
    public Long getSourceBinId() { return sourceBinId; }
    public void setSourceBinId(Long sourceBinId) { this.sourceBinId = sourceBinId; }
    public Long getTargetBinId() { return targetBinId; }
    public void setTargetBinId(Long targetBinId) { this.targetBinId = targetBinId; }
    public Integer getQtyTransferred() { return qtyTransferred; }
    public void setQtyTransferred(Integer qtyTransferred) { this.qtyTransferred = qtyTransferred; }
    public Long getMergedByEmpId() { return mergedByEmpId; }
    public void setMergedByEmpId(Long mergedByEmpId) { this.mergedByEmpId = mergedByEmpId; }
    public LocalDateTime getMergedAt() { return mergedAt; }
    public void setMergedAt(LocalDateTime mergedAt) { this.mergedAt = mergedAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}