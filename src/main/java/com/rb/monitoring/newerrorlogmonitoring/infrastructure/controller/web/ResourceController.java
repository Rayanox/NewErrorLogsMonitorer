package com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ResourceController {

    @GetMapping("images/{imageName:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String imageName) {
        Resource imageResource = new ClassPathResource("META-INF/resources/resources/images/" + imageName);

        // Ajoutez des en-têtes appropriés pour indiquer le type de contenu de la réponse
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // Remplacez par le type de contenu approprié

        return new ResponseEntity<>(imageResource, headers, HttpStatus.OK);
    }
}
