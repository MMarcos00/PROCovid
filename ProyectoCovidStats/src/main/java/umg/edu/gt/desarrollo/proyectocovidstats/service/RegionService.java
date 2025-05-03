package umg.edu.gt.desarrollo.proyectocovidstats.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Region;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.RegionRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.util.ApiClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class RegionService {

    private static final Logger logger = LogManager.getLogger(RegionService.class);
    private final ApiClient apiClient;
    private final RegionRepository regionRepository;
    private final ObjectMapper objectMapper;

    public RegionService(ApiClient apiClient, RegionRepository regionRepository) {
        this.apiClient = apiClient;
        this.regionRepository = regionRepository;
        this.objectMapper = new ObjectMapper();
    }

    public void fetchAndSaveRegions(String iso) {
        try {
            // Fetch the regions from the API
            String regionsJson = apiClient.getRegions(iso);
            logger.info("Received regions data: {}", regionsJson);

            // Process the regions and save them to the database
            saveRegions(regionsJson, iso);
        } catch (Exception e) {
            logger.error("Error fetching and saving regions: {}", e.getMessage());
        }
    }

    public void saveRegions(String json, String countryIso) throws Exception {
        JsonNode root = objectMapper.readTree(json).get("data");
        List<Region> regionsToSave = new ArrayList<>();

        for (JsonNode node : root) {
            try {
                String regionName = node.get("name").asText();
                String regionIso = node.get("iso").asText();

                // Create a new region and add it to the list
                Region region = new Region(regionName, regionIso);
                regionsToSave.add(region);

            } catch (Exception e) {
                logger.error("Error processing region: {}", e.getMessage());
            }
        }

        if (!regionsToSave.isEmpty()) {
            // Save the list of regions to the database
            regionRepository.saveAll(regionsToSave);
            logger.info("Saved {} regions", regionsToSave.size());
        } else {
            logger.info("No regions found to save");
        }
    }
}
