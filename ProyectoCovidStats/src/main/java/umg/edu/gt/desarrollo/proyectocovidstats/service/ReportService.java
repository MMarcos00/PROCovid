package umg.edu.gt.desarrollo.proyectocovidstats.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Report;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.ReportRepository;

import java.time.LocalDate;
import java.util.*;

@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final ReportRepository reportRepository;

    // Constructor de la clase
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // Método para obtener y guardar los informes de una API (lógica no implementada en este ejemplo)
    public void fetchAndSaveReports(String iso) {
        // Aquí deberías tener implementado el consumo de la API para obtener datos de informes
        // Posteriormente, puedes guardar esos informes usando reportRepository.saveAll(reports);
    }

    // Método para obtener los informes agrupados por provincia y fecha
    public Map<String, Report> getReportsGroupedByProvince(String iso, LocalDate date) {
        // Utilizamos el método del repositorio para obtener los informes filtrados por código ISO de la provincia y fecha
        List<Report> rawReports = reportRepository.findByProvince_IsoCodeAndDate(iso, date);

        // Usamos un TreeMap para garantizar que las provincias estén ordenadas alfabéticamente
        TreeMap<String, Report> groupedReports = new TreeMap<>();

        // Procesamos los informes para agruparlos por provincia
        for (Report report : rawReports) {
            String provinceKey = report.getProvince() != null ? report.getProvince().getName() : "Unknown";

            // Solo agregamos el primer informe por provincia si hay duplicados
            groupedReports.putIfAbsent(provinceKey, report);
        }

        // Registramos la información de los informes agrupados
        logger.info("Grouped reports for country {} on {}:", iso, date);
        for (Map.Entry<String, Report> entry : groupedReports.entrySet()) {
            logger.info("Province: {}, Report: {}", entry.getKey(), entry.getValue());
        }

        // Retornamos el mapa con los informes agrupados por provincia
        return groupedReports;
    }

    // Método para guardar informes (se asume que ya los informes se obtienen de alguna fuente y se procesan)
    public void saveReports(List<Report> reports) {
        if (reports != null && !reports.isEmpty()) {
            reportRepository.saveAll(reports);
            logger.info("Successfully saved {} reports.", reports.size());
        } else {
            logger.info("No reports to save.");
        }
    }

    // Método para actualizar un informe existente
    public void updateReport(Report report) {
        if (report != null && report.getId() != null) {
            Optional<Report> existingReport = reportRepository.findById(report.getId());
            if (existingReport.isPresent()) {
                Report updatedReport = existingReport.get();
                updatedReport.setConfirmed(report.getConfirmed());
                updatedReport.setDeaths(report.getDeaths());
                updatedReport.setRecovered(report.getRecovered());
                updatedReport.setActive(report.getActive());

                reportRepository.save(updatedReport);
                logger.info("Report updated for province: {}", updatedReport.getProvince().getName());
            } else {
                logger.info("Report not found for ID: {}", report.getId());
            }
        } else {
            logger.info("Invalid report provided for update.");
        }
    }
}
