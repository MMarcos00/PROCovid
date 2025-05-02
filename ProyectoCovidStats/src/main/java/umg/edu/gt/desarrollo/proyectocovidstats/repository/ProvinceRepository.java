package umg.edu.gt.desarrollo.proyectocovidstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Province;

import java.util.List;

public interface ProvinceRepository extends JpaRepository<Province, Long> {
    List<Province> findByNameContainingIgnoreCase(String name);
}
