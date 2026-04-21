package com.cutm.smo.repositories;

import com.cutm.smo.models.Style;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StyleRepository extends JpaRepository<Style, Long> {
}