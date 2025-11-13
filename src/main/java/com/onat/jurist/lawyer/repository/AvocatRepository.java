package com.onat.jurist.lawyer.repository;

import com.onat.jurist.lawyer.entity.Avocat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvocatRepository extends JpaRepository<Avocat, Long> {

    List<Avocat> findByRegion(String region);
}
