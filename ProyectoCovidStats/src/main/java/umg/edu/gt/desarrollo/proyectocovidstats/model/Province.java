package umg.edu.gt.desarrollo.proyectocovidstats.model;

import javax.persistence.*;

@Entity
public class Province {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "region_iso_code") // Aquí debes asegurarte que 'region_iso_code' está en la tabla 'province'
    private Region region;

    // Puedes agregar el campo `regionIsoCode` para que sea más claro en tus consultas
    @Column(name = "region_iso_code", insertable = false, updatable = false)
    private String regionIsoCode; // Este campo puede ser útil si lo necesitas para otras consultas

    public Province() {}

    public Province(String name, Region region) {
        this.name = name;
        this.region = region;
        if (region != null) {
            this.regionIsoCode = region.getIsoCode(); // Esto es solo si deseas asignar el código ISO directamente
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
