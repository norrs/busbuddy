package no.norrs.busbuddy.api.ping;

import no.norrs.busbuddy.pub.api.HttpUtil;
import no.norrs.busbuddy.pub.api.model.Departure;
import no.norrs.busbuddy.pub.api.model.DepartureContainer;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Roy Sindre Norangshol
 */
public class CedlindBusmapPing implements BusbuddyPing {
    Logger logger = Logger.getLogger(CedlindBusmapPing.class.getName());
    private String endpoint;

    public CedlindBusmapPing() {
        this.endpoint = "http://guybrush.sin-hq.org/busskart/data/delay.php?stop=%s&sann=%s&rute=%s&linje=%s&callback=callback";
    }
    public CedlindBusmapPing(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void ping(String locationid, DepartureContainer forecast) {
        for (Departure departure : forecast.getDepartures()) {
            if (departure.isRealtimeData()) {
                String fixedEntryPoint = String.format(endpoint, locationid, departure.getRegisteredDepartureTime().toString(), departure.getScheduledDepartureTime().toString(), departure.getLine());
                doPing(fixedEntryPoint);
            }
        }
    }

    public void doPing(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpResponse response = HttpUtil.GET(url);
                    if (response.getStatusLine().getStatusCode() != 200) {
                        logger.warning(String.format("Receiver failed to receive : %s" + url));
                    }
                } catch (IOException e) {
                    logger.warning(String.format("Receiver generated a IO exception: %s" + e));
                }

            }
        }).start();
    }
}
