package com.cutm.smo.repositories;

import com.cutm.smo.models.Threads;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreadsRepository extends JpaRepository<Threads, Long> {
}