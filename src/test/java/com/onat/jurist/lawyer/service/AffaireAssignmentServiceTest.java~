package com.onat.jurist.lawyer.service;

import com.onat.jurist.lawyer.entity.Affaire;
import com.onat.jurist.lawyer.entity.Avocat;
import com.onat.jurist.lawyer.entity.TypeAffaire;
import com.onat.jurist.lawyer.repository.AffaireRepository;
import com.onat.jurist.lawyer.repository.AvocatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AffaireAssignmentServiceTest {

    @Mock
    private AvocatRepository avocatRepo;

    @Mock
    private AffaireRepository affaireRepo;

    @Mock
    private EmailNotificationService emailService;

    @InjectMocks
    private AffaireAssignmentService service;


    // -----------------------------
    // Helpers
    // -----------------------------
    private Avocat avocat(Long id, int load, LocalDateTime lastAssigned, LocalDate inscription) {
        Avocat a = new Avocat();
        a.setId(id);
        a.setAffairesEnCours(load);
        a.setLastAssignedAt(lastAssigned);
        a.setDateInscription(inscription);
        return a;
    }

    private Affaire affaire(Long id, String accuse) {
        Affaire a = new Affaire();
        a.setId(id);
        a.setNomAccuse(accuse);
        a.setType(TypeAffaire.CIVIL);
        return a;
    }


    // -------------------------------------------------------------------------
    // TEST 1 : Même accusé → doit retourner le même avocat
    // -------------------------------------------------------------------------
    @Test
    void shouldReturnSameLawyerWhenAccuseHadPreviousAffaire() {
        // GIVEN
        Avocat existingLawyer = avocat(3L, 2, null, LocalDate.now());

        Affaire previousAffaire = affaire(10L, "John Doe");
        previousAffaire.setAvocatAssigne(existingLawyer);

        Affaire newAffaire = affaire(99L, "John Doe");

        when(affaireRepo.findFirstByNomAccuse("John Doe"))
                .thenReturn(Optional.of(previousAffaire));

        Optional<Avocat> result = service.assignBestLawyer(newAffaire);

        assertThat(result).contains(existingLawyer);
        verify(emailService).sendAssignmentEmail(existingLawyer, newAffaire);
    }


    // -------------------------------------------------------------------------
    // TEST 2 : Tri par nombre d’affaires en cours
    // -------------------------------------------------------------------------
    @Test
    void shouldAssignLawyerWithLessLoad() {
        Affaire a = affaire(1L, "X");

        Avocat l1 = avocat(1L, 0, null, LocalDate.now());
        Avocat l2 = avocat(2L, 3, null, LocalDate.now());
        Avocat l3 = avocat(3L, 1, null, LocalDate.now());

        when(affaireRepo.findFirstByNomAccuse("X")).thenReturn(Optional.empty());
        when(avocatRepo.findAll()).thenReturn(List.of(l1, l2, l3));

        Optional<Avocat> result = service.assignBestLawyer(a);

        assertThat(result).contains(l1);
    }


    // -------------------------------------------------------------------------
    // TEST 3 : Priorité à ceux sans affaire ce mois-ci
    // -------------------------------------------------------------------------
    @Test
    void shouldPrioritizeLawyerWithoutThisMonthAssignment() {
        Affaire a = affaire(2L, "Y");

        Avocat lastMonth = avocat(1L, 1, LocalDateTime.now().minusMonths(2), LocalDate.now());
        Avocat thisMonth = avocat(2L, 1, LocalDateTime.now().minusDays(2), LocalDate.now());

        when(affaireRepo.findFirstByNomAccuse("Y")).thenReturn(Optional.empty());
        when(avocatRepo.findAll()).thenReturn(List.of(thisMonth, lastMonth));

        Optional<Avocat> result = service.assignBestLawyer(a);

        assertThat(result).contains(lastMonth);
    }


    // -------------------------------------------------------------------------
    // TEST 4 : Plus récent inscrit prioritaire
    // -------------------------------------------------------------------------
    @Test
    void shouldPickNewestRegisteredWhenEqualOnOtherCriteria() {
        Affaire a = affaire(3L, "Z");

        Avocat older = avocat(1L, 0, LocalDateTime.now().minusDays(10), LocalDate.of(2020, 1, 1));
        Avocat newer = avocat(2L, 0, LocalDateTime.now().minusDays(10), LocalDate.of(2024, 1, 1));

        when(affaireRepo.findFirstByNomAccuse("Z")).thenReturn(Optional.empty());
        when(avocatRepo.findAll()).thenReturn(List.of(older, newer));

        Optional<Avocat> result = service.assignBestLawyer(a);

        assertThat(result).contains(newer);
    }


    // -------------------------------------------------------------------------
    // TEST 5 : lastAssignedAt → moins utilisé en premier
    // -------------------------------------------------------------------------
    @Test
    void shouldPickLawyerUsedLongestTimeAgo() {
        Affaire a = affaire(4L, "A");

        Avocat usedRecently = avocat(1L, 0, LocalDateTime.now().minusHours(2), LocalDate.now());
        Avocat usedLongAgo = avocat(2L, 0, LocalDateTime.now().minusDays(15), LocalDate.now());

        when(affaireRepo.findFirstByNomAccuse("A")).thenReturn(Optional.empty());
        when(avocatRepo.findAll()).thenReturn(List.of(usedRecently, usedLongAgo));

        Optional<Avocat> result = service.assignBestLawyer(a);

        assertThat(result).contains(usedLongAgo);
    }


    // -------------------------------------------------------------------------
    // TEST 6 : Mise à jour du champ lastAssignedAt
    // -------------------------------------------------------------------------
    @Test
    void shouldUpdateLastAssignedAt() {
        Affaire a = affaire(5L, "K");

        Avocat lawyer = avocat(3L, 0, null, LocalDate.now());

        when(affaireRepo.findFirstByNomAccuse("K")).thenReturn(Optional.empty());
        when(avocatRepo.findAll()).thenReturn(List.of(lawyer));

        service.assignBestLawyer(a);

        verify(avocatRepo).save(argThat(av -> av.getLastAssignedAt() != null));
    }



}

