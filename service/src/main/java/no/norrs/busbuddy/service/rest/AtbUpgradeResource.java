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
        return Response.serverError().entity("client need to upgrade api").build();
    }
}
