package com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.common;

import com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.home.HomePage;
import com.vaadin.flow.component.UI;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HomePushWrapper {

    private UI ui;
    private HomePage layout;

}
