package com.onat.jurist.lawyer.dto.out;

import com.onat.jurist.lawyer.entity.SousTypeAffaire;
import com.onat.jurist.lawyer.entity.StatutAffaire;
import com.onat.jurist.lawyer.entity.TypeAffaire;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AffaireResponseDTO {
    private Long id;
    private String numero;
    private String titre;
    private TypeAffaire type;
    private String nomAccuse;
    private LocalDateTime dateCreation;
    private LocalDateTime dateTribunal;
    private StatutAffaire statut;
    private Long avocatId;
    private String avocatNom; // <-- add this
    private SousTypeAffaire sousType;


}