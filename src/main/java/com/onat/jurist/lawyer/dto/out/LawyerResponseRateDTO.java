package com.onat.jurist.lawyer.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LawyerResponseRateDTO {
    private Long id;
    private String nom;
    private double acceptanceRate;
}