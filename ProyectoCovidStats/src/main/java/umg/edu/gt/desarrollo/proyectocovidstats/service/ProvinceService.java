package umg.edu.gt.desarrollo.proyectocovidstats.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Province;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Region;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.ProvinceRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.RegionRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.util.ApiClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProvinceService {

    private static final Logger logger = LogManager.getLogger(ProvinceService.class);
    private final ApiClient apiClient;
    private final RegionRepository regionRepository;
    private final ProvinceRepository provinceRepository;
    private final ObjectMapper objectMapper;

    public ProvinceService(ApiClient apiClient, RegionRepository regionRepository, ProvinceRepository provinceRepository) {
        this.apiClient = apiClient;
        this.regionRepository = regionRepository;
        this.provinceRepository = provinceRepository;
        this.objectMapper = new ObjectMapper();
    }

    public void fetchAndSaveProvinces(String iso) {
        try {
            // Fetch the provinces from the API
            String provincesJson = apiClient.getProvinces(iso);
            logger.info("Received provinces data: {}", provincesJson);

            // Process the provinces and save them to the database
            saveProvinces(provincesJson, iso);
        } catch (Exception e) {
            logger.error("Error fetching and saving provinces: {}", e.getMessage());
        }
    }

    public void saveProvinces(String json, String countryIso) throws Exception {
        JsonNode root = objectMapper.readTree(json).get("data");
        List<Province> provincesToSave = new ArrayList<>();

        for (JsonNode node : root) {
            try {
                String provinceName = node.get("name").asText();
                String regionIso = node.get("region_iso").asText();

                // Find the region by ISO code
                Region region = regionRepository.findByIsoCode(regionIso);

                if (region != null) {
                    // Create a new province and add it to the list
                    Province province = new Province(provinceName, region);
                    provincesToSave.add(province);
                } else {
                    logger.info("Region not found for province: {}", provinceName);
                }
            } catch (Exception e) {
                logger.error("Error processing province: {}", e.getMessage());
            }
        }

        if (!provincesToSave.isEmpty()) {
            // Save the list of provinces to the database
            provinceRepository.saveAll(provincesToSave);
            logger.info("Saved {} provinces", provincesToSave.size());
        } else {
            logger.info("No provinces found to save");
        }
    }
}
