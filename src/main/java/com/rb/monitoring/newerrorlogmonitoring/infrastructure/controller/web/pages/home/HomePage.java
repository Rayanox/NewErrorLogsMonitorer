package com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.home;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.AppProperties;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.common.components.header.HeaderComponent;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.home.components.MainHpComponent;
import com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.home.components.StatusDashboard;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

@Route("/")
public class HomePage extends VerticalLayout {


    public HomePage(@Autowired AppProperties appProperties, @Autowired StatusDashboard statusDashboard) {

        add(
                new HeaderComponent(),
                new MainHpComponent(
                        new H1("BACKEND Dashboard")
                ),
                statusDashboard
        );
    }
}
