package org.example.ebookstore.services.implementations;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.transaction.Transactional;
import org.example.ebookstore.entities.ScheduledTaskAudit;
import org.example.ebookstore.repositories.ScheduledTaskAuditRepository;
import org.example.ebookstore.services.interfaces.BookService;
import org.example.ebookstore.services.interfaces.ExchangeRateService;
import org.example.ebookstore.services.interfaces.ScheduledTasksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ScheduledTasksServiceImpl implements ScheduledTasksService {
    private final ExchangeRateService exchangeRateService;
    private final BookService bookService;
    private final ScheduledTaskAuditRepository scheduledTaskAuditRepository;

    @Value("${backup.database.name}")
    private String dbName;

    @Value("${backup.database.user}")
    private String dbUser;

    @Value("${backup.database.password}")
    private String dbPass;

    @Value("${backup.file.path}")
    private String filePath;

    @Value("${backup.dump.path}")
    private String sqlDumpPath;

    @Value("${exchangerate.api.key}")
    private String apiKey;

    @Autowired
    public ScheduledTasksServiceImpl(ExchangeRateService exchangeRateService, BookService bookService, ScheduledTaskAuditRepository scheduledTaskAuditRepository) {
        this.exchangeRateService = exchangeRateService;
        this.bookService = bookService;
        this.scheduledTaskAuditRepository = scheduledTaskAuditRepository;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void getLatestFxRatesAndUpdateBookPrices() {
        try {
            Map<String, BigDecimal> rates = getLatestFxRates();
            this.exchangeRateService.addLatestExchangeRates(rates);
            this.bookService.updateFxPricesOfAllBooks();

            ScheduledTaskAudit task = this.scheduledTaskAuditRepository.findById(1L).get();
            task.setLastRunDate(LocalDate.now());
            this.scheduledTaskAuditRepository.save(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, BigDecimal> getLatestFxRates() throws IOException, URISyntaxException {
        String urlStr = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/EUR";

        URL url = new URI(urlStr).toURL();
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(new InputStreamReader((InputStream) request.getContent()), JsonObject.class);
        String result = jsonObject.get("result").getAsString();
        if (!"success".equals(result)) {
            throw new IOException("Failed to fetch FX rates: " + result);
        }

        JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rates");
        Map<String, BigDecimal> rates = new HashMap<>();

        for (String key : conversionRates.keySet()) {
            BigDecimal rate = conversionRates.get(key).getAsBigDecimal();
            rates.put(key, rate);
        }

        return rates;
    }

    @Override
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void backupDatabase() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateStr = formatter.format(new Date());
        String executeCmd = sqlDumpPath + " -u " + dbUser + " -p" + dbPass + " " + dbName + " -r " + filePath + "/"
                + dbName + "_" + dateStr + ".sql";

        try {
            Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
            int processComplete = runtimeProcess.waitFor();

            if (processComplete == 0) {
                System.out.println("Backup created successfully");
                ScheduledTaskAudit task = this.scheduledTaskAuditRepository.findById(2L).get();
                task.setLastRunDate(LocalDate.now());
                this.scheduledTaskAuditRepository.save(task);
            } else {
                System.out.println("Could not create the backup");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LocalDate getLastFxRatesUpdateDate() {
        return this.scheduledTaskAuditRepository.findById(1L)
                .map(ScheduledTaskAudit::getLastRunDate).orElse(LocalDate.MIN);
    }

    @Override
    public LocalDate getLastBackupDate() {
        return this.scheduledTaskAuditRepository.findById(2L)
                .map(ScheduledTaskAudit::getLastRunDate).orElse(LocalDate.MIN);
    }
}
