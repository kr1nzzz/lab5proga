import java.time.LocalDateTime;
import java.util.Objects;

/**
 * City model.
 * Default sorting: by name, then by id.
 */
public class City implements Comparable<City> {
    private long id; // >0, unique, auto
    private String name; // not null, not empty
    private Coordinates coordinates; // not null
    private LocalDateTime creationDate; // not null, auto
    private Double area; // >0, not null
    private Long population; // >0, not null
    private long metersAboveSeaLevel;
    private Climate climate; // not null
    private Government government; // not null
    private StandardOfLiving standardOfLiving; // may be null
    private Human governor; // may be null

    @Override
    public int compareTo(City o) {
        int c = this.name.compareToIgnoreCase(o.name);
        if (c != 0) return c;
        return Long.compare(this.id, o.id);
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", area=" + area +
                ", population=" + population +
                ", metersAboveSeaLevel=" + metersAboveSeaLevel +
                ", climate=" + climate +
                ", government=" + government +
                ", standardOfLiving=" + standardOfLiving +
                ", governor=" + governor +
                '}';
    }

    // equals/hashCode for governor comparisons etc.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;
        City city = (City) o;
        return id == city.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // getters/setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Coordinates getCoordinates() { return coordinates; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public Double getArea() { return area; }
    public void setArea(double area) { this.area = area; }

    public Long getPopulation() { return population; }
    public void setPopulation(long population) { this.population = population; }

    public long getMetersAboveSeaLevel() { return metersAboveSeaLevel; }
    public void setMetersAboveSeaLevel(long metersAboveSeaLevel) { this.metersAboveSeaLevel = metersAboveSeaLevel; }

    public Climate getClimate() { return climate; }
    public void setClimate(Climate climate) { this.climate = climate; }

    public Government getGovernment() { return government; }
    public void setGovernment(Government government) { this.government = government; }

    public StandardOfLiving getStandardOfLiving() { return standardOfLiving; }
    public void setStandardOfLiving(StandardOfLiving standardOfLiving) { this.standardOfLiving = standardOfLiving; }

    public Human getGovernor() { return governor; }
    public void setGovernor(Human governor) { this.governor = governor; }
}