package umg.edu.gt.desarrollo.proyectocovidstats.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Province;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Region;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Report;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.ProvinceRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.RegionRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.ReportRepository;

import java.time.LocalDate;
import java.util.*;

@Service
public class CovidDataService {
    private static final Logger logger = LogManager.getLogger(CovidDataService.class);

    private final RegionRepository regionRepository;
    private final ProvinceRepository provinceRepository;
    private final ReportRepository reportRepository;
    private final ObjectMapper objectMapper;

    // Internal HashMaps for temporary work
    private final Map<String, Region> isoRegionMap = new HashMap<>();
    private final Map<String, Province> nameProvinceMap = new HashMap<>();

    public CovidDataService(RegionRepository regionRepository,
                            ProvinceRepository provinceRepository,
                            ReportRepository reportRepository) {
        this.regionRepository = regionRepository;
        this.provinceRepository = provinceRepository;
        this.reportRepository = reportRepository;
        this.objectMapper = new ObjectMapper();
    }

    public void saveReports(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json).get("data");
        List<Report> reportsToSave = new ArrayList<>();

        for (JsonNode node : root) {
            // First we check if the JSON structure has the province correctly
            if (!node.has("region") || !node.get("region").has("province")) {
                logger.info("Node without province information: " + node.toString());
                continue;
            }

            String provinceName = node.get("region").get("province").asText();
            if (provinceName.isEmpty() || provinceName.equals("N/A")) {
                if (node.get("region").has("name")) {
                    provinceName = node.get("region").get("name").asText();
                } else {
                    provinceName = "N/A";
                }
            }

            Province province = nameProvinceMap.get(provinceName);

            if (province == null) {
                // If it's not on the map, we try to search the DB for a similar name
                logger.info("Province not found on the temporary map: " + provinceName);
                List<Province> similarProvinces = provinceRepository.findByNameContainingIgnoreCase(provinceName);
                if (!similarProvinces.isEmpty()) {
                    province = similarProvinces.get(0);
                    nameProvinceMap.put(provinceName, province);
                    logger.info("Similar province found: " + province.getName());
                } else {
                    logger.info("No province similar to: " + provinceName);
                    continue;
                }
            }

            try {
                LocalDate reportDate = LocalDate.parse(node.get("date").asText());
                List<Report> existingReports = reportRepository.findByProvinceAndDate(province, reportDate);

                if (!existingReports.isEmpty()) {
                    // We update the existing reports
                    for (Report existingReport : existingReports) {
                        existingReport.setConfirmed(node.get("confirmed").asInt());
                        existingReport.setDeaths(node.get("deaths").asInt());
                        existingReport.setRecovered(node.get("recovered").asInt());
                        reportsToSave.add(existingReport);
                    }
                } else {
                    // We create a new report
                    Report report = new Report(province, node.get("confirmed").asInt(), node.get("deaths").asInt(), node.get("recovered").asInt(), node.get("active").asInt(), reportDate);
                    reportsToSave.add(report);
                }
            } catch (Exception e) {
                logger.error("Error processing report: " + e.getMessage() + " - For province: " + provinceName);
            }
        }

        if (!reportsToSave.isEmpty()) {
            reportRepository.saveAll(reportsToSave);
            logger.info("Saved " + reportsToSave.size() + " reports");
        } else {
            logger.info("No reports found to save");
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
                Region region = isoRegionMap.get(regionIso);
                if (region == null) {
                    // If the region is not in the map, try to fetch it from the DB
                    region = regionRepository.findByIsoCode(regionIso);
                    if (region != null) {
                        isoRegionMap.put(regionIso, region);
                    }
                }

                if (region != null) {
                    Province province = new Province(provinceName, region);
                    provincesToSave.add(province);
                } else {
                    logger.info("Region not found for province: " + provinceName);
                }
            } catch (Exception e) {
                logger.error("Error processing province: " + e.getMessage());
            }
        }

        if (!provincesToSave.isEmpty()) {
            provinceRepository.saveAll(provincesToSave);
            logger.info("Saved " + provincesToSave.size() + " provinces");
        } else {
            logger.info("No provinces found to save");
        }
    }

    public void saveRegions(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json).get("data");
        List<Region> regionsToSave = new ArrayList<>();

        for (JsonNode node : root) {
            try {
                String regionName = node.get("name").asText();
                String isoCode = node.get("iso_code").asText();

                Region region = new Region(regionName, isoCode);
                regionsToSave.add(region);
                isoRegionMap.put(isoCode, region);

            } catch (Exception e) {
                logger.error("Error processing region: " + e.getMessage());
            }
        }

        if (!regionsToSave.isEmpty()) {
            regionRepository.saveAll(regionsToSave);
            logger.info("Saved " + regionsToSave.size() + " regions");
        } else {
            logger.info("No regions found to save");
        }
    }
}
