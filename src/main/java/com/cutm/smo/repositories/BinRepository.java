package com.cutm.smo.repositories;

import com.cutm.smo.models.Bin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BinRepository extends JpaRepository<Bin, Long> {
    
    /**
     * Find bin by QR code
     */
    Optional<Bin> findByQrCode(String qrCode);
}