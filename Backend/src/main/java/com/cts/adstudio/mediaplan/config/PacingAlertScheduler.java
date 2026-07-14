package com.cts.adstudio.mediaplan.config;

import com.cts.adstudio.mediaplan.service.PacingAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component //generic spring bean
@RequiredArgsConstructor //genereates constructor for final and nonnull fields for constructor dep injection
@Slf4j //logger
public class PacingAlertScheduler {

    private final PacingAlertService pacingAlertService;

    // Runs every day at 1:00 AM. (cron = second minute hour day month weekday)
    @Scheduled(cron = "0 0 1 * * *")
    public void dailyPacingCheck() {
        log.info("Scheduled daily pacing check triggered.");
        int created = pacingAlertService.runPacingCheck();
        log.info("Scheduled pacing check created {} alert(s).", created);
    }
}