package no.norrs.busbuddy.service.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.spi.resource.Singleton;
import no.norrs.busbuddy.api.model.ApiKeyLog;
import no.norrs.busbuddy.api.ping.BusbuddyPing;
import no.norrs.busbuddy.pub.api.CharSetAdapter;
import no.norrs.busbuddy.pub.api.InstantTypeConverter;
import no.norrs.busbuddy.pub.api.LocalDateTimeTypeConverter;
import no.norrs.busbuddy.pub.api.model.Departure;
import no.norrs.busbuddy.pub.api.model.DepartureContainer;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Roy Sindre Norangshol
 */
@Component
@Path("/1.2")
@Singleton
public class AtbUpgradeResource {

    private Gson gson;
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    @Context
    HttpHeaders headers;

    public AtbUpgradeResource() {
        GsonBuilder builder = new GsonBuilder();
        gson = builder
                .registerTypeAdapter(String.class, new CharSetAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeConverter())
                .registerTypeAdapter(Instant.class, new InstantTypeConverter())
                .setPrettyPrinting()
                .create();
    }

    /**
     * Ugly, but best method to get app owners aware when users start nagging that they need to update their apps :)
     * @param callbackQueryParam
     * @return
     */
    @GET
    @Path("/departures/{locationId}")
    @Produces({"application/json; charset=UTF-8"})
    public Response getDepartures(@QueryParam("callback") String callbackQueryParam) {
        DepartureContainer dummyContainer = new DepartureContainer();
        dummyContainer.setGoingTowardsCentrum(1001);
        Departure dummyDepartureTellingUserToNagOwnerOfApp = new Departure();
        dummyDepartureTellingUserToNagOwnerOfApp.setLine("500");
        dummyDepartureTellingUserToNagOwnerOfApp.setDestination("AppAuthor");
        dummyDepartureTellingUserToNagOwnerOfApp.setRegisteredDepartureTime(new LocalDateTime().plusMinutes(5));
        Departure dummyDepartureTellingUserToNagOwnerOfApp2 = new Departure();
        dummyDepartureTellingUserToNagOwnerOfApp2.setLine("501");
        dummyDepartureTellingUserToNagOwnerOfApp2.setDestination("Must");
        dummyDepartureTellingUserToNagOwnerOfApp2.setRegisteredDepartureTime(new LocalDateTime().plusMinutes(5));
        Departure dummyDepartureTellingUserToNagOwnerOfApp3 = new Departure();
        dummyDepartureTellingUserToNagOwnerOfApp3.setLine("502");
        dummyDepartureTellingUserToNagOwnerOfApp3.setDestination("UPGRADE");
        dummyDepartureTellingUserToNagOwnerOfApp3.setRegisteredDepartureTime(new LocalDateTime().plusMinutes(5));
        Departure dummyDepartureTellingUserToNagOwnerOfApp4 = new Departure();
        dummyDepartureTellingUserToNagOwnerOfApp4.setLine("503");
        dummyDepartureTellingUserToNagOwnerOfApp4.setDestination("TO API 1.3 ASAP");
        dummyDepartureTellingUserToNagOwnerOfApp4.setRegisteredDepartureTime(new LocalDateTime().plusMinutes(5));

        dummyContainer.setDepartures(new ArrayList<Departure>(Arrays.asList(new Departure[]{
                dummyDepartureTellingUserToNagOwnerOfApp,
                dummyDepartureTellingUserToNagOwnerOfApp2,
                dummyDepartureTellingUserToNagOwnerOfApp3,
                dummyDepartureTellingUserToNagOwnerOfApp4})));


        String data = null;
        if (callbackQueryParam != null) {
            data = String.format("busbuddyResponse%s(%s)", callbackQueryParam, gson.toJson(dummyContainer));
        } else {
            data = gson.toJson(dummyContainer);
        }


        try {

            URI uri = uriInfo != null ? uriInfo.getAbsolutePath()
                    : new URI(String.format("%s/%s", "/buss/incoming", "666"));
            return Response.created(uri)
                    .entity(data)
                    .status(Response.Status.OK)
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to create uri", e);
        }
    }
}
