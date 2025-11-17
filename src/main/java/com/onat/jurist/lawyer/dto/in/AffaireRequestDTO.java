package com.onat.jurist.lawyer.dto.in;

import com.onat.jurist.lawyer.entity.TypeAffaire;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AffaireRequestDTO {
    @NotBlank
    private String numero;

    @NotBlank
    private String titre;

    @NotNull
    private TypeAffaire type;

    @NotBlank
    private String nomAccuse;

    @NotNull
    private LocalDateTime dateTribunal;
}