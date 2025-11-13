package com.onat.jurist.lawyer.repository;

import com.onat.jurist.lawyer.entity.Affaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AffaireRepository extends JpaRepository<Affaire, Long> {
}
