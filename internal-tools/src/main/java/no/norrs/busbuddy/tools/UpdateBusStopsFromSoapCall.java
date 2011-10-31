package no.norrs.busbuddy.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.norrs.busbuddy.api.AtbController;
import no.norrs.busbuddy.api.AtbControllerFactory;
import no.norrs.busbuddy.api.atb.model.BusListsContainer;
import no.norrs.busbuddy.api.atb.model.BusStop;
import no.norrs.busbuddy.api.dao.BusStopDAO;
import no.norrs.busbuddy.pub.api.CharSetAdapter;
import no.norrs.busbuddy.pub.api.InstantTypeConverter;
import no.norrs.busbuddy.pub.api.LocalDateTimeTypeConverter;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Properties;

/**
 * @author Roy Sindre Norangshol
 */
public class UpdateBusStopsFromSoapCall {
    private BusStopDAO busstopDAO;
    private ClassPathXmlApplicationContext context;
    private AtbController atbService;
    private Gson gson;

    public UpdateBusStopsFromSoapCall() {
        context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        Properties atbWebProperties = new Properties();

        //atbWebProperties.load(getClass().getResourceAsStream("/atbweb.properties"));

        atbService = new AtbControllerFactory().createRealtimeSoapController();
        //atbService = new AtbWebController(atbWebProperties.getProperty("endpoint"), atbWebProperties.getProperty("payload"));
        GsonBuilder builder = new GsonBuilder();
        gson = builder.serializeNulls()
                .registerTypeAdapter(String.class, new CharSetAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeConverter())
                .registerTypeAdapter(Instant.class, new InstantTypeConverter())
                .create();


        busstopDAO = (BusStopDAO) context.getBean("busstopDAO");

        //System.out.println(Arrays.toString(convertFromEPSG3785MercToEPSG4326WGS84(1141485.0, 9179826.0))); // Øien skole

        List<no.norrs.busbuddy.pub.api.model.BusStop> ourBusStops = busstopDAO.findAll();
        for (no.norrs.busbuddy.pub.api.model.BusStop ourBusStop : ourBusStops) {
            ourBusStop.setBusStopId(null);
            busstopDAO.update(ourBusStop);
        }

        BusListsContainer container = atbService.getAllBusStopsListInTrondheim();
        for (BusStop busStop : container.busStops) {
            no.norrs.busbuddy.pub.api.model.BusStop dbBusStop = busstopDAO.findBusStopByLocationId(busStop.nodeId.trim());
            if (dbBusStop != null) {
                dbBusStop.setBusStopId(busStop.stopId);
                busstopDAO.update(dbBusStop);
                System.out.println(String.format("Updated location id %s with busStopId %s", dbBusStop.getLocationId(), busStop.stopId));
            } else {
                double[] coords = convertFromEPSG3785MercToEPSG4326WGS84(busStop.longitude, busStop.latitude);
                busstopDAO.insertOrUpdate(new no.norrs.busbuddy.pub.api.model.BusStop(
                        busStop.stopId,
                        busStop.description,
                        busStop.description,
                        null,
                        busStop.nodeId.trim(),
                        (float)coords[1],
                        (float)coords[0]
                        ));
                System.out.println(String.format("Updated location id %s with busStopId %s  (%s)", busStop.nodeId.trim(), busStop.stopId, dbBusStop));
            }


        }

    }

    public double[] convertFromEPSG3785MercToEPSG4326WGS84(double x, double y) {
        double originShift = 2 * Math.PI * 6378137 / 2.0;

        double lon = (x / originShift) * 180.0;
        double lat = (y / originShift) * 180.0;

        lat = 180 / Math.PI * (2 * Math.atan(Math.exp(lat * Math.PI /
                180.0)) - Math.PI / 2.0);
        return new double[]{
                lat, lon
        };


    }

    public static void main(String[] args) {
        new UpdateBusStopsFromSoapCall();
    }
}
