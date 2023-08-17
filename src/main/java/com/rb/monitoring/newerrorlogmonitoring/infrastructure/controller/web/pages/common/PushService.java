package com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.common;

import com.rb.monitoring.newerrorlogmonitoring.infrastructure.controller.web.pages.home.HomePage;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.Command;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PushService {

    private List<HomePushWrapper> uis = new ArrayList<>();

    public void addUI(UI ui, HomePage layout) {
        cleanUnavailableUis();
        uis.add(HomePushWrapper.builder()
                .ui(ui)
                .layout(layout)
                .build());
    }

    public int getUiCount() {
        return uis.size();
    }

    public void updateDashboard() {
        getAvailableUis().forEach(wrapper -> callUiMethod(wrapper.getLayout()::updateDashboard, wrapper));
    }


    public void pushCustomNotification(String message) {
        getAvailableUis().forEach(wrapper -> callUiMethod(() -> wrapper.getLayout().displayCustomNotification(message), wrapper));

    }

    /*
        PRIVATES
     */

    private void callUiMethod(Command command, HomePushWrapper wrapper) {
        try {
            wrapper.getUi().getSession().lock();
            wrapper.getUi().access(command);
            wrapper.getUi().push();
        }finally {
            wrapper.getUi().getSession().unlock();
        }
    }

//    private void callUiMethod(Command command) {
//        var availableUis = getAvailableUis();
//
//        try {
//            availableUis.stream()
//                    .peek(wrapper -> wrapper.getUi().getSession().lock())
//                    .forEach(wrapper ->
//                                    wrapper.getUi().access(command)
////                        layout.getUI().ifPresent(ui -> ui.access(layout::updateDashboard))
//
//                    );
//
//            availableUis.stream()
//                    .map(HomePushWrapper::getUi)
//                    .forEach(UI::push);
//        }finally {
//            availableUis.stream()
//                    .map(HomePushWrapper::getUi)
//                    .map(UI::getSession)
//                    .filter(session -> session.hasLock())
//                    .forEach(session -> session.unlock());
//        }
//    }

    private synchronized void cleanUnavailableUis() {
        var uisToRemove = uis.stream()
                .filter(ui -> checkUiDisconnected(ui.getUi()))
                .toList();

        uis.removeAll(uisToRemove);
    }

    private List<HomePushWrapper> getAvailableUis() {
        cleanUnavailableUis();
        return uis;
    }

    private boolean checkUiDisconnected(UI ui) {
        return ui == null || ui.isClosing() || !ui.isAttached();
    }
}
