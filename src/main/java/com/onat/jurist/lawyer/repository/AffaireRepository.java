package com.onat.jurist.lawyer.repository;

import com.onat.jurist.lawyer.entity.Affaire;
import com.onat.jurist.lawyer.entity.StatutAffaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AffaireRepository extends JpaRepository<Affaire, Long> {

    boolean existsByNumero(String numero);
    Optional<Affaire> findFirstByNomAccuse(String nomAccuse);
    @Query("SELECT a FROM Affaire a WHERE a.avocatAssigne.id = :avocatId")
    List<Affaire> findAllByAvocatId(@Param("avocatId") Long avocatId);
    List<Affaire> findAllByStatut(StatutAffaire statutAffaire);

}
