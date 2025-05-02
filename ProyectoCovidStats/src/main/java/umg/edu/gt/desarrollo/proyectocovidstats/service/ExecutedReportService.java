package umg.edu.gt.desarrollo.proyectocovidstats.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import umg.edu.gt.desarrollo.proyectocovidstats.model.ExecutedReport;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.ExecutedReportRepository;

import java.time.LocalDate;

@Service
public class ExecutedReportService {

    private final ExecutedReportRepository executedReportRepository;

    @Value("${covid.report.date}")
    private String reportDate;

    public ExecutedReportService(ExecutedReportRepository executedReportRepository) {
        this.executedReportRepository = executedReportRepository;
    }

    public boolean hasAlreadyExecuted(String countryIso) {
        LocalDate date = LocalDate.parse(reportDate);
        return executedReportRepository.existsByExecutionDateAndCountryIso(date, countryIso);
    }

    public void saveExecution(String countryIso) {
        LocalDate date = LocalDate.parse(reportDate);
        ExecutedReport executedReport = new ExecutedReport(date, countryIso);
        executedReportRepository.save(executedReport);
    }

    public LocalDate getReportDate() {
        return LocalDate.parse(reportDate);
    }
}
