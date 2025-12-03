package org.example.ebookstore.services.interfaces;

import java.time.LocalDate;

public interface ScheduledTasksService {
    void getLatestFxRatesAndUpdateBookPrices();
    void backupDatabase();
    LocalDate getLastFxRatesUpdateDate();
    LocalDate getLastBackupDate();
}
