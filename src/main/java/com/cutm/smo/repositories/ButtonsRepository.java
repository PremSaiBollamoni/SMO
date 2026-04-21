package com.cutm.smo.repositories;

import com.cutm.smo.models.Buttons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ButtonsRepository extends JpaRepository<Buttons, Long> {
}