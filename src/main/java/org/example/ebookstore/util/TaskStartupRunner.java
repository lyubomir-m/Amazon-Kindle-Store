package org.example.ebookstore.util;

import org.example.ebookstore.services.interfaces.ScheduledTasksService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Order(20)
public class TaskStartupRunner implements ApplicationRunner {
    private final ScheduledTasksService scheduledTasksService;

    public TaskStartupRunner(ScheduledTasksService scheduledTasksService) {
        this.scheduledTasksService = scheduledTasksService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LocalDate lastFxUpdate = scheduledTasksService.getLastFxRatesUpdateDate();
        if (lastFxUpdate.isBefore(LocalDate.now())) {
            scheduledTasksService.getLatestFxRatesAndUpdateBookPrices();
        }

        LocalDate lastBackupDate = scheduledTasksService.getLastBackupDate();
        if (lastBackupDate.isBefore(LocalDate.now())) {
            scheduledTasksService.backupDatabase();
        }
    }
}
