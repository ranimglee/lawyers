package com.onat.jurist.lawyer.dto.out;


public record AffaireDTO(
        Long id,
        String titre,
        String status,
        Long avocatId,
        String message
) {}