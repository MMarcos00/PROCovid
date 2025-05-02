package umg.edu.gt.desarrollo.proyectocovidstats.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import umg.edu.gt.desarrollo.proyectocovidstats.service.ExecutedReportService;
import umg.edu.gt.desarrollo.proyectocovidstats.service.RegionService;
import umg.edu.gt.desarrollo.proyectocovidstats.service.ProvinceService;
import umg.edu.gt.desarrollo.proyectocovidstats.service.ReportService;

import java.util.List;

@Component
public class CovidReportRunner implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(CovidReportRunner.class);

    private final ExecutedReportService executedReportService;
    private final RegionService regionService;
    private final ProvinceService provinceService;
    private final ReportService reportService;

    private final List<String> countryIsoList;

    @Autowired
    public CovidReportRunner(ExecutedReportService executedReportService,
                             RegionService regionService,
                             ProvinceService provinceService,
                             ReportService reportService) {
        this.executedReportService = executedReportService;
        this.regionService = regionService;
        this.provinceService = provinceService;
        this.reportService = reportService;

        // Lista de países a procesar (puedes modificar según tus necesidades)
        this.countryIsoList = List.of("USA", "GTM", "CAN");
    }

    @Override
    public void run() {
        for (String iso : countryIsoList) {
            if (executedReportService.hasAlreadyExecuted(iso)) {
                logger.info("Country {} was already processed on {}. Skipping.", iso, executedReportService.getReportDate());
                continue;
            }

            try {
                logger.info("Processing country {} for date {}", iso, executedReportService.getReportDate());

                // Lógica del flujo completo aquí:
                // Primero procesamos las regiones para el país
                regionService.fetchAndSaveRegions(iso);

                // Luego procesamos las provincias
                provinceService.fetchAndSaveProvinces(iso);

                // Finalmente, procesamos los reportes
                reportService.fetchAndSaveReports(iso);

                // Guardar que la ejecución se realizó correctamente para el país
                executedReportService.saveExecution(iso);

                logger.info("Execution for country {} saved successfully.", iso);
            } catch (Exception e) {
                logger.error("Error processing country {}: {}", iso, e.getMessage(), e);
            }
        }
    }
}
