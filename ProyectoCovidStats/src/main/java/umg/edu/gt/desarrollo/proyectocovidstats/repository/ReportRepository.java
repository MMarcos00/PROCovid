package umg.edu.gt.desarrollo.proyectocovidstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Report;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Province;

import java.time.LocalDate;
import java.util.List;
//primer
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Método para buscar reportes por provincia y fecha
    List<Report> findByProvinceAndDate(Province province, LocalDate date);
}
