package com.onat.jurist.lawyer.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LawyerWorkloadDTO {
    private Long id;
    private String nom;
    private int totalAffaires;
    private int accepted;
    private int refused;
}