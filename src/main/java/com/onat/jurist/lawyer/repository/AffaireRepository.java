package com.onat.jurist.lawyer.repository;

import com.onat.jurist.lawyer.entity.Affaire;
import com.onat.jurist.lawyer.entity.AssignmentMode;
import com.onat.jurist.lawyer.entity.StatutAffaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AffaireRepository extends JpaRepository<Affaire, Long> {

    boolean existsByNumero(String numero);
    Optional<Affaire> findFirstByNomAccuse(String nomAccuse);
    @Query("SELECT a FROM Affaire a WHERE a.avocatAssigne.id = :avocatId")
    List<Affaire> findAllByAvocatId(@Param("avocatId") Long avocatId);
    @Query("SELECT a FROM Affaire a LEFT JOIN FETCH a.notifications WHERE a.statut = :statut")
    List<Affaire> findAllByStatutWithNotifications(@Param("statut") StatutAffaire statut);

    long countByAvocatAssigneIsNotNull();

    long countByAvocatAssigneIsNull();

    Long countByAssignmentMode(AssignmentMode assignmentMode);

    @Query("SELECT a.statut, COUNT(a) FROM Affaire a GROUP BY a.statut")
    List<Object[]> countAffairesByStatus();

    @Query("SELECT a.type, COUNT(a) FROM Affaire a GROUP BY a.type")
    List<Object[]> countAffairesByType();

    List<Affaire> findByDateTribunalBeforeAndStatutNot(LocalDateTime now, StatutAffaire statutAffaire);
}
