package com.rb.monitoring.newerrorlogmonitoring.domain.status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {

    OK("Aucune nouvelle erreur détectée"),
    ERROR_DETECTED ("Nouvelle erreur détectée"),
    ERROR_CHECKED ("Erreur checkée par un utilisateur, en attente d'absence de nouvelle détection de cette erreur pendant un certain temps");

    private String explanation;
}
