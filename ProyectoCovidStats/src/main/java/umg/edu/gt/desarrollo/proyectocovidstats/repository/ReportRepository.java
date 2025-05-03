package umg.edu.gt.desarrollo.proyectocovidstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Report;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Province;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // Buscar por provincia (entidad completa) y fecha
    List<Report> findByProvinceAndDate(Province province, LocalDate date);

    // ✅ CORREGIDO: Buscar por regionIsoCode (no isoCode) de la provincia
    List<Report> findByProvince_RegionIsoCodeAndDate(String regionIsoCode, LocalDate date);
}
