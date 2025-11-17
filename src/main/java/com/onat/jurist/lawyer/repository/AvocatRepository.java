package com.onat.jurist.lawyer.repository;

import com.onat.jurist.lawyer.entity.Avocat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AvocatRepository extends JpaRepository<Avocat, Long> {

    @Query("select a from Avocat a where a.lastAssignedAt >= :since")
    List<Avocat> findAllAssignedSince(@Param("since") LocalDateTime since);
}
