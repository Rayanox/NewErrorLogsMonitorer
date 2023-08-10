package com.rb.monitoring.newerrorlogmonitoring.domain.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {

    OK("Aucune nouvelle erreur détectée", 0),
    ERROR_DETECTED ("Nouvelle erreur détectée", 2),
    ERROR_CHECKED ("Erreur checkée par un utilisateur, en attente d'absence de nouvelle détection de cette erreur pendant un certain temps", 1),
    UNKNOWN("Status inconnu, il y à un bug à corriger dans l'application de Monitoring", 0);

    private String explanation;
    private int level;

}
