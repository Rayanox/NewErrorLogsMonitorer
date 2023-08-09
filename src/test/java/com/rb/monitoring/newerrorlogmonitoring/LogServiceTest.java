package com.rb.monitoring.newerrorlogmonitoring;

import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.ServiceConfItem;
import com.rb.monitoring.newerrorlogmonitoring.application.configuration.services.environment.EnvironmentWrapperConfig;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.ExceptionEntry;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.dto.LogEntry;
import com.rb.monitoring.newerrorlogmonitoring.domain.logger.LogService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;

@Log4j2
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LogServiceTest {

    private static final String LOGFILE_TEST_PATH = "mediation.front-office.mobile.bus.log";
    private static final String LOGFILE_UPDATED_TEST_PATH = "mediation.front-office.mobile.bus-updated.log";
    private static final String LOGFILE_UPDATED_NEW_LOG_TEST_PATH = "mediation.front-office.mobile.bus-updated-newLog.log";
    private static final String LOGFILE_UPDATED_2DIFFERENT_DATES_TEST_PATH = "mediation.front-office.mobile.bus-updatedWith2DifferentDates.log";
    private static final String LOGFILE_TEST_MOVED_LINE_PATH = "mediation.front-office.mobile.bus-movedLine.log";
    private static final String LOGFILE_TEST_MOVED_LINE_AND_VERSION_CHANGED_PATH = "mediation.front-office.mobile.bus-movedLineAndVersionsChanged.log";

    @Autowired
    private List<ServiceConfItem> serviceConfItems;
    @Autowired
    private LogService logService;
    private List<String> logInput;
    private List<String> logInputUpdated;
    private List<String> logInputUpdatedNewLog;
    private List<String> logInputUpdated2DifferentDates;
    private List<String> logInputMovedLine;
    private List<String> logInputMovedLineAndVersionChanged;

    private ServiceConfItem of(String serviceName) {
        return serviceConfItems.stream()
                .filter(serviceConf -> serviceConf.getServiceName().contains(serviceName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Service '"+ serviceName +"'not found"));
    }

    @BeforeAll
    public void initLogInput() {
        try {
            logInput = Files.readAllLines(Path.of(ClassLoader.getSystemResource(LOGFILE_TEST_PATH).toURI()));
            logInputUpdated = Files.readAllLines(Path.of(ClassLoader.getSystemResource(LOGFILE_UPDATED_TEST_PATH).toURI()));
            logInputUpdatedNewLog = Files.readAllLines(Path.of(ClassLoader.getSystemResource(LOGFILE_UPDATED_NEW_LOG_TEST_PATH).toURI()));
            logInputUpdated2DifferentDates = Files.readAllLines(Path.of(ClassLoader.getSystemResource(LOGFILE_UPDATED_2DIFFERENT_DATES_TEST_PATH).toURI()));
            logInputMovedLine = Files.readAllLines(Path.of(ClassLoader.getSystemResource(LOGFILE_TEST_MOVED_LINE_PATH).toURI()));
            logInputMovedLineAndVersionChanged = Files.readAllLines(Path.of(ClassLoader.getSystemResource(LOGFILE_TEST_MOVED_LINE_AND_VERSION_CHANGED_PATH).toURI()));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();

            Assertions.fail("Cannot read the input file. Please fix the init of test Logs.");
        }
    }

    @Test
    public void testLogReader_allSameDatetime() {
        ServiceConfItem serviceConf = of("mediation.front-office.mobile.bus");
        EnvironmentWrapperConfig environment = serviceConf.getEnvironments().get(0);
        List<LogEntry> logs = logService.readAllLogs(logInput, serviceConf, environment);

        Assertions.assertEquals(45, logs.size());

        LogEntry firstLog = logs.get(0);
        Assertions.assertEquals("ResponseHelper",firstLog.getClassNameLog());
        Assertions.assertEquals(LogLevel.ERROR,firstLog.getLogLevel());
        Assertions.assertEquals("Une erreur s'est produite lors de l'appel d'un service en asynchrone",firstLog.getMessage());
        Assertions.assertEquals(LocalDateTime.of(2023, 7, 2, 8, 54),firstLog.getDate());
        Assertions.assertNotNull(firstLog.getException());
        Assertions.assertNotNull(firstLog.getException().getCompanyStacktrace());

        ExceptionEntry exception = firstLog.getException();
        Assertions.assertEquals("java.util.concurrent.ExecutionException: java.util.concurrent.ExecutionException: org.apache.camel.CamelExecutionException: Exception occurred during execution on the exchange: Exchange[Message: com.karavel.catalogue.sejour.in.referentielgeographique.CountryVOIn@5cf1d8]",exception.getMessage());
        Assertions.assertTrue(exception.getEndOfStacktrace().contains("at com.karavel.mediation.impl.Bus.rechercheV3(Bus.java:480) ~[mediation.front-office.mobile.bus.vm-5.68.0.jar:?]"));
        Assertions.assertFalse(StringUtils.isEmpty(exception.getMessage()));
        Assertions.assertEquals(5,exception.getDepth());

        LogEntry beforeLastLog = logs.get(logs.size()-2);
        Assertions.assertEquals("ReferentielGeographiqueDelegate",beforeLastLog.getClassNameLog());
        Assertions.assertEquals(LogLevel.ERROR,beforeLastLog.getLogLevel());
        Assertions.assertEquals("Erreur lors de la recuperation des donnees du pays en provencance du service referentiel geographique",beforeLastLog.getMessage());
        Assertions.assertEquals(LocalDateTime.of(2023, 7, 2, 8, 54),beforeLastLog.getDate());
        Assertions.assertNull(beforeLastLog.getException());
    }

    @Test
    public void testLogReaderUpdated_allSameDatetime() {
        ServiceConfItem serviceConf = of("mediation.front-office.mobile.bus");
        EnvironmentWrapperConfig environment = serviceConf.getEnvironments().get(0);
        List<LogEntry> logsAlreadySaved = logService.readAllLogs(logInput, serviceConf, environment);
        LogEntry lastLogAlreadySaved = logsAlreadySaved.get(logsAlreadySaved.size()-1);
        List<LogEntry> logs = logService.readAllLogs(logInputUpdated, lastLogAlreadySaved.getDate(), serviceConf, environment);

        Assertions.assertEquals(50, logs.size());
    }



    @Test
    public void testLogReaderUpdated_withTwoDifferentDates() {
        ServiceConfItem serviceConf = of("mediation.front-office.mobile.bus");
        EnvironmentWrapperConfig environment = serviceConf.getEnvironments().get(0);

        List<LogEntry> logsAlreadySaved = logService.readAllLogs(logInput, serviceConf, environment);
        LogEntry lastLogAlreadySaved = logsAlreadySaved.get(logsAlreadySaved.size()-1);
        List<LogEntry> logs = logService.readAllLogs(logInputUpdated2DifferentDates, lastLogAlreadySaved.getDate(), serviceConf, environment);

        Assertions.assertEquals(2, logs.size());
    }


    @Test
    public void testNewLogs_ZeroNewLog() {
        ServiceConfItem serviceConf = of("mediation.front-office.mobile.bus");
        EnvironmentWrapperConfig environment = serviceConf.getEnvironments().get(0);

        List<LogEntry> logsAlreadySaved = logService.readAllLogs(logInput, serviceConf, environment);
        LogEntry lastLogAlreadySaved = logsAlreadySaved.get(logsAlreadySaved.size()-1);
        List<LogEntry> newLogsArrived = logService.readAllLogs(logInputUpdated, lastLogAlreadySaved.getDate(), serviceConf, environment);

        var newLogsDetected = logService.getNewLogs(logsAlreadySaved, newLogsArrived);

        Assertions.assertEquals(0, newLogsDetected.size());
    }

    @Test
    public void testNewLogs_OneNewLog() {
        ServiceConfItem serviceConf = of("mediation.front-office.mobile.bus");
        EnvironmentWrapperConfig environment = serviceConf.getEnvironments().get(0);

        List<LogEntry> logsAlreadySaved = logService.readAllLogs(logInput, serviceConf, environment);
        LogEntry lastLogAlreadySaved = logsAlreadySaved.get(logsAlreadySaved.size()-1);
        List<LogEntry> newLogsArrived = logService.readAllLogs(logInputUpdatedNewLog, lastLogAlreadySaved.getDate(), serviceConf, environment);

        var newLogsDetected = logService.getNewLogs(logsAlreadySaved, newLogsArrived);

        Assertions.assertEquals(1, newLogsDetected.size());

        var singleNewLog = newLogsDetected.get(0);

        Assertions.assertEquals("Une nouvelle erreur de test a été detectée\nEt oui, c'est une nouvelle erreur",singleNewLog.getMessage());
        Assertions.assertEquals("TestClasse",singleNewLog.getClassNameLog());
        Assertions.assertNotNull(singleNewLog.getException());
        Assertions.assertEquals("L'erreur est située dans la classe : com.karavel.front.send.request.model.TestClasse",singleNewLog.getException().getMessage());
        Assertions.assertEquals(1,singleNewLog.getException().getDepth());
        Assertions.assertEquals("at coucou.test\nat etoui.test",singleNewLog.getException().getStacktrace());
    }

    @Test
    public void distinctReadTest() {
        ServiceConfItem serviceConf = of("mediation.front-office.mobile.bus");
        EnvironmentWrapperConfig environment = serviceConf.getEnvironments().get(0);

        List<LogEntry> logs = logService.readLogs(logInput, null, serviceConf, environment);
        List<LogEntry> newErrors = logService.getNewLogs(emptyList(), logs);

        Assertions.assertEquals(5, logs.size());
        Assertions.assertEquals(4, newErrors.size());
    }

    @Test
    public void distinctReadWithDateTest() {
        ServiceConfItem serviceConf = of("mediation.front-office.mobile.bus");
        EnvironmentWrapperConfig environment = serviceConf.getEnvironments().get(0);
        LocalDateTime afterDate = LocalDateTime.of(2023, 7, 2, 8, 54);

        List<LogEntry> logs = logService.readLogs(logInputUpdated2DifferentDates, afterDate, serviceConf, environment);

        Assertions.assertEquals(2, logs.size());
    }

    @Test
    public void lineMovedTest() {
        ServiceConfItem serviceConf = of("mediation.front-office.mobile.bus");
        EnvironmentWrapperConfig environment = serviceConf.getEnvironments().get(0);

        List<LogEntry> logs = logService.readLogs(logInput, null, serviceConf, environment);
        List<LogEntry> newLogsArrived = logService.readLogs(logInputMovedLine, null, serviceConf, environment);
        var entryUpdated = newLogsArrived.get(newLogsArrived.size()-1);

        List<LogEntry> newErrors = logService.getNewLogs(logs, newLogsArrived);

        Assertions.assertEquals(0, newErrors.size());
        Assertions.assertTrue(entryUpdated.getException().getCompanyStacktrace().contains("00000")); // test replaced line number (update test)
    }

    @Test
    public void movedLineAndVersionChangedTest(){
        ServiceConfItem serviceConf = of("mediation.front-office.mobile.bus");
        EnvironmentWrapperConfig environment = serviceConf.getEnvironments().get(0);

        List<LogEntry> logs = logService.readLogs(logInput, null, serviceConf, environment);
        List<LogEntry> newLogsArrived = logService.readLogs(logInputMovedLineAndVersionChanged, null, serviceConf, environment);

        List<LogEntry> newErrors = logService.getNewLogs(logs, newLogsArrived);

        Assertions.assertEquals(0, newErrors.size());
    }


}
