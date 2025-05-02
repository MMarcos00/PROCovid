package umg.edu.gt.desarrollo.proyectocovidstats.model;



import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "executed_reports", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"execution_date", "country_iso"})
})
public class ExecutedReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "execution_date", nullable = false)
    private LocalDate executionDate;

    @Column(name = "country_iso", nullable = false, length = 3)
    private String countryIso;

    // Constructors
    public ExecutedReport() {}

    public ExecutedReport(LocalDate executionDate, String countryIso) {
        this.executionDate = executionDate;
        this.countryIso = countryIso;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public LocalDate getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDate executionDate) {
        this.executionDate = executionDate;
    }

    public String getCountryIso() {
        return countryIso;
    }

    public void setCountryIso(String countryIso) {
        this.countryIso = countryIso;
    }
}
