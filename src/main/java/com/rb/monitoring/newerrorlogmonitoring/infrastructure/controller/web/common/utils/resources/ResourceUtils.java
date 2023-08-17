package com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.common.utils.resources;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.spring.annotation.SpringComponent;

@SpringComponent
public class ResourceUtils {

    public Image getImage (ResourcesPaths resourcesPaths) {
        Image image = new Image(resourcesPaths.getPath(), resourcesPaths.getAlt());
        return image;
    }

//    public Image getImage (ResourcesPaths resourcesPaths) {
//        StreamResource imageResource = new StreamResource(resourcesPaths.getResourceName(),
//                () -> getClass().getResourceAsStream(resourcesPaths.getPath()));
//
//        Image image = new Image(imageResource, resourcesPaths.getAlt());
//        return image;
//    }

}
