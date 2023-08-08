package com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.common.components.header;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class HeaderComponent extends Div {

    public HeaderComponent() {
//        String backgroundImage = "url('image-test.jpg')";
//        String backgroundStyle = "background-image: " + backgroundImage + "; " +
        String backgroundStyle =
//                "background-size: cover; " +
                "background-repeat: no-repeat; ";

        getElement().setAttribute("style", backgroundStyle);

        HorizontalLayout header = new HorizontalLayout();
        header.setHeight("20%");

//        Image logoImage = new Image("logo.png", "Logo");
//        header.add(logoImage);

        HorizontalLayout menuButtons = new HorizontalLayout();

        Button button1 = new Button("Dashboard");
        Button button2 = new Button("Profil");
        Button button3 = new Button("Paramètres");

        menuButtons.add(button1, button2, button3);

        // Ajouter la liste de boutons de menu à l'en-tête
        header.add(menuButtons);

        // Définir l'alignement vertical des composants dans l'en-tête
//        header.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, logoImage, menuButtons);
        add(header);
    }
}
