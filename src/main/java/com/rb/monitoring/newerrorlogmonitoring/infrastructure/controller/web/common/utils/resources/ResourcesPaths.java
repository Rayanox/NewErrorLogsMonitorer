package com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.common.utils.resources;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResourcesPaths {

    HP_BACKGROUND("parking-deck-238450_1920.jpg", "images", "background"),
    HP_CLOUDS("clouds.jpeg", "images", "clouds"),
    HP_ALERT_LAMP("red-led-flasher.png", "images", "alert-lamp"),
    HP_ALERT_RED_LIGHT("rayons_lumiere.png", "images", "red-light"),
    HP_RIDEAU_DE_FER("rideau-de-fer-2.jpg", "images", "rideau-de-fer"),
    HP_ALERT_RED_LIGHT_CLOUD("Lumiere_rouge.png", "images", "red-light-cloud");

    private String resourceName;
    private String folder;
    private String alt;

    public String getPath() {
        return folder + "/" + resourceName;
    }

}
