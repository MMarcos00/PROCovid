package umg.edu.gt.desarrollo.proyectocovidstats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import umg.edu.gt.desarrollo.proyectocovidstats.config.AppConfig;
import umg.edu.gt.desarrollo.proyectocovidstats.service.ApiService;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(AppConfig.class)
public class Covid19TrackerApplication {

    private static final Logger logger = LogManager.getLogger(Covid19TrackerApplication.class);

    private final ApiService apiService;

    @Value("${app.initialDelay}")
    private long initialDelay;

    public Covid19TrackerApplication(ApiService apiService) {
        this.apiService = apiService;
    }

    // Ejecutar una vez después del delay inicial
    @Scheduled(initialDelayString = "${app.initialDelay}", fixedDelay = Long.MAX_VALUE)
    public void fetchData() {
        int retries = 3;
        while (retries > 0) {
            try {
                logger.info("Iniciando carga de datos desde la API...");
                apiService.fetchCovidData();
                logger.info("Carga de datos completada exitosamente.");
                return;
            } catch (Exception e) {
                retries--;
                logger.error("Error ejecutando fetchData, intentos restantes: {}: {}", retries, e.getMessage(), e);
                if (retries == 0) {
                    logger.error("Se agotaron los intentos para cargar los datos.");
                }
            }
        }
    }


    public static void main(String[] args) {
        SpringApplication.run(Covid19TrackerApplication.class, args);
    }
}
