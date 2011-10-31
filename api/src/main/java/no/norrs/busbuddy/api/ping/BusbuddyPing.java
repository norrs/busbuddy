package no.norrs.busbuddy.api.ping;

import no.norrs.busbuddy.pub.api.model.DepartureContainer;

/**
 * @author Roy Sindre Norangshol
 */
public interface BusbuddyPing {
    void ping(String locationId, DepartureContainer forecast);
}
