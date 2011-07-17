package models;

/**
 * @author Roy Sindre Norangshol
 */
@Entity
@Table(name = "busstop")
public class BusStop extends Model {
     @Column(name = "name")
    public String name;
    @Column(name = "name_abbreviation")
    public String abbreviationName;
    @Column(name = "maintainer")
    public String maintainer;
    @Column(name = "location_id")
    public String location_id;
    @Column(name = "longitude")
    public double longitude;
    @Column(name= "latitude")
    public double latitude;
    @Column(name="busstop_id")
    public int busStopId;

    public String toString() {
        return String.format("%s (%s) [%s,%s] %s", name, busStopId, longitude, latitude, location_id);
    }

}
