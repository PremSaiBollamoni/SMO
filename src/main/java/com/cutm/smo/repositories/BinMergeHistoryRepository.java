package com.cutm.smo.repositories;

import com.cutm.smo.models.BinMergeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BinMergeHistoryRepository extends JpaRepository<BinMergeHistory, Long> {
}