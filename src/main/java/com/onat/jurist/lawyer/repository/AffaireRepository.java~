package com.onat.jurist.lawyer.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.onat.jurist.lawyer.entity.Affaire;
import com.onat.jurist.lawyer.entity.StatutAffaire;
import com.onat.jurist.lawyer.entity.TypeAffaire;
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
    @Query("SELECT a FROM Affaire a LEFT JOIN FETCH a.notifications WHERE a.id = :id")
    Optional<Affaire> findByIdWithNotifications(@Param("id") Long id);

    Optional<Affaire> findFirstByNomAccuse(String nomAccuse);


    @Query("select a from Affaire a where a.type = :type and a.statut = :statut and a.dateCreation <= :before")
    List<Affaire> findWaitingByTypeAndStatutBefore(@Param("type") TypeAffaire type,
                                                   @Param("statut") StatutAffaire statut,
                                                   @Param("before") LocalDateTime before);

    @Query("SELECT a FROM Affaire a WHERE a.avocatAssigne.id = :avocatId")
    List<Affaire> findAllByAvocatId(@Param("avocatId") Long avocatId);

}
