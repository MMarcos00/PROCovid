package umg.edu.gt.desarrollo.proyectocovidstats.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umg.edu.gt.desarrollo.proyectocovidstats.model.ExecutedReport;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ExecutedReportRepository extends JpaRepository<ExecutedReport, Long> {

    Optional<ExecutedReport> findByExecutionDateAndCountryIso(LocalDate executionDate, String countryIso);

    boolean existsByExecutionDateAndCountryIso(LocalDate executionDate, String countryIso);
}
