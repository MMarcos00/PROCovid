package umg.edu.gt.desarrollo.proyectocovidstats.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Region {
    @Id
    private String isoCode;
    private String name;

    public Region() {
    }

    public Region(String name, String isoCode) {
        this.name = name;
        this.isoCode = isoCode;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
