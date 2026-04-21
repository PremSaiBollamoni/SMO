package com.cutm.smo.repositories;

import com.cutm.smo.models.WipTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WipTrackingRepository extends JpaRepository<WipTracking, Long> {
    
    /**
     * Find maximum WIP tracking ID for generating new IDs
     */
    @Query("SELECT MAX(w.wipId) FROM WipTracking w")
    Long findMaxWipTrackingId();
}