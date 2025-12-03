package com.onat.jurist.lawyer.dto.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AvocatRequest {

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères")
    private String prenom;

    @NotBlank(message = "L'identifiant est obligatoire")
    @Size(max = 50, message = "L'identifiant ne peut pas dépasser 50 caractères")
    private String identifiant;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères")
    private String nom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    private String email;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "\\+?[0-9]{7,15}", message = "Numéro de téléphone invalide")
    private String telephone;

    @NotBlank(message = "La région est obligatoire")
    @Size(max = 100, message = "La région ne peut pas dépasser 100 caractères")
    private String region;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 200, message = "L'adresse ne peut pas dépasser 200 caractères")
    private String adresse;

    private LocalDate dateInscription;
}
