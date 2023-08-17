package com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.home.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;

public class MainHpComponent extends Div {

    public MainHpComponent(H1 h1) {
        h1.getStyle().setColor("white");
        getElement().setAttribute("style", "background-position: center;");
        add(h1);
    }
}
