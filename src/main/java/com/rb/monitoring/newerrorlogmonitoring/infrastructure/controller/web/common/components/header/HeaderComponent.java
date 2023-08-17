package com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.common.components.header;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class HeaderComponent extends Div {

    public HeaderComponent(Image redLightImage, Image redLightCloudImage) {
        String backgroundStyle = "background-repeat: no-repeat; ";
        getElement().setAttribute("style", backgroundStyle);

        HorizontalLayout header = new HorizontalLayout();
        header.setHeight("20%");

        HorizontalLayout menuButtons = new HorizontalLayout();

        Button button1 = new Button("Dashboard"); //,
//                (event) ->  {
//                        if(redLightImage.isVisible()) {
//                            redLightImage.setClassName("");
//                            redLightImage.setVisible(false);
//                            redLightCloudImage.setClassName("");
//                            redLightCloudImage.setVisible(false);
//                        } else {
//                            redLightImage.setClassName("red-light-image");
//                            redLightImage.setVisible(true);
//                            redLightCloudImage.setClassName("red-light-cloud-image");
//                            redLightCloudImage.setVisible(true);
//                        }
//                }
//        );
//        Button button2 = new Button("Test -> AppClignote dans 5s",
//                event -> UI.getCurrent().getPage().executeJs("$.titleAlert(\"/!\\ Alert /!\\\", {\n" +
//                        "    requireBlur:true,\n" +
//                        "    stopOnFocus:true,\n" +
//                        "    duration:10000,\n" +
//                        "    interval:500\n" +
//                        "});")
//        );
//        Button button3 = new Button("Paramètres");

        menuButtons.add(button1);

        // Ajouter la liste de boutons de menu à l'en-tête
        header.add(menuButtons);

        // Définir l'alignement vertical des composants dans l'en-tête
//        header.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, logoImage, menuButtons);
        add(header);
    }
}
