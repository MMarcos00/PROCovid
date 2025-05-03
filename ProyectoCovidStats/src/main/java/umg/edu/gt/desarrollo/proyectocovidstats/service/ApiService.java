package umg.edu.gt.desarrollo.proyectocovidstats.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import umg.edu.gt.desarrollo.proyectocovidstats.config.AppConfig;
import umg.edu.gt.desarrollo.proyectocovidstats.util.ApiClient;
import org.springframework.stereotype.Service;

@Service
public class ApiService {

    private static final Logger logger = LogManager.getLogger(ApiService.class);
    private final ApiClient apiClient;
    private final CovidDataService covidDataService;
    private final AppConfig appConfig;

    public ApiService(ApiClient apiClient, CovidDataService covidDataService, AppConfig appConfig) {
        this.apiClient = apiClient;
        this.covidDataService = covidDataService;
        this.appConfig = appConfig;
    }

    public void fetchCovidData() {
        logger.info("Fetching COVID-19 data...");

        String countryIso = appConfig.getCountryIso();  // Asegúrate de que esto esté correctamente configurado
        String reportDate = appConfig.getReportDate();  // Asegúrate de que esto esté correctamente configurado

        logger.info("countryIso desde AppConfig: '{}'", countryIso);
        logger.info("reportDate desde AppConfig: '{}'", reportDate);

        try {
            // 🔹 Obtener regiones
            String regions = apiClient.getRegions(countryIso);  // Cambié 'iso' por 'countryIso'
            logger.info("Regions: " + regions);
            covidDataService.saveRegions(regions); // ✅ Guardar en la base de datos

            // 🔹 Obtener provincias
            String provinces = apiClient.getProvinces(countryIso);  // Cambié 'iso' por 'countryIso'
            logger.info("Provinces for {}: {}", countryIso, provinces);
            covidDataService.saveProvinces(provinces, countryIso);

            // 🔹 Obtener informes
            String report = apiClient.getReports(countryIso, reportDate);  // Asegúrate de que este método esté implementado correctamente en ApiClient
            logger.info("Report for {} on {}: {}", countryIso, reportDate, report);
            covidDataService.saveReports(report);

        } catch (Exception e) {
            logger.error("❌ Error consuming the API or saving to the DB: {}", e.getMessage());
        }
    }
}
