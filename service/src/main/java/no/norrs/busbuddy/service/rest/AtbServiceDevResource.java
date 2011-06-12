/*
 * Copyright 2011 BusBuddy (Roy Sindre Norangshol)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.norrs.busbuddy.service.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.spi.resource.Singleton;
import no.norrs.busbuddy.api.AtbSoapController;
import no.norrs.busbuddy.api.atb.model.BusListsContainer;
import no.norrs.busbuddy.api.atb.model.BusStopForecastContainer;
import no.norrs.busbuddy.api.dao.BusStopDAO;
import no.norrs.busbuddy.api.skrot.controller.SkrotToAPIConverterController;
import no.norrs.busbuddy.pub.api.CharSetAdapter;
import no.norrs.busbuddy.pub.api.InstantTypeConverter;
import no.norrs.busbuddy.pub.api.LocalDateTimeTypeConverter;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * @author Roy Sindre Norangshol
 */
@Path("/dev")
@Singleton
public class AtbServiceDevResource extends SharedResources {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    @Context

    HttpHeaders headers;
    private AtbSoapController soapService;
    private BusStopDAO busStopDAO;
    private SkrotToAPIConverterController skrotController;
    private Gson gson;


    public AtbServiceDevResource() throws IOException {
        super();
        Properties atbProperties = new Properties();
        atbProperties.load(getClass().getResourceAsStream("/atbapikey.properties"));
        soapService = new AtbSoapController(atbProperties.getProperty("username"), atbProperties.getProperty("password"));
       // skrotController = new SkrotToAPIConverterController();
        busStopDAO = (BusStopDAO) context.getBean("busstopDAO");

        GsonBuilder builder = new GsonBuilder();
        gson = builder.serializeNulls()
                .registerTypeAdapter(String.class, new CharSetAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeConverter())
                .registerTypeAdapter(Instant.class, new InstantTypeConverter())
                .create();
        System.out.println("init!");
    }

    @GET
    @Path("/atb/busstops")
    @Produces({"application/json; charset=UTF-8"})
    public Response getOriginalBusStops(@QueryParam("apiKey") String apiKeyQueryParam) {
        String apiKey = getApiKeyFromRequest(apiKeyQueryParam, headers);

        if (apiKeyNotNull(apiKey) && isValidKey(apiKey)) {
            BusListsContainer container = soapService.getAllBusStopsListInTrondheim();
            if (Integer.parseInt(container.total) < 1) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            String data = gson.toJson(container.convertToApi());
            try {
                URI uri = uriInfo != null ? uriInfo.getAbsolutePath()
                        : new URI("/busstops");
                return Response.created(uri)
                        .entity(data)
                        .status(Response.Status.OK)
                        .build();
            } catch (URISyntaxException e) {
                throw new RuntimeException("Failed to create uri", e);
            }
        } else {
            return Response.status(Response.Status.FORBIDDEN).entity("Missing api-key, contact busbuddy 'at' norrs.no requesting api-key. Include application name, link to application and contact information and set subject with 'Request BusBuddy API key - appname'").build();
        }

    }

    @GET
    @Path("/atb/departures/{busStopId}")
    @Produces({"application/json; charset=UTF-8"})
    public Response getBusStopsInAtbFormat(@PathParam("busStopId") int busStopId, @QueryParam("apiKey") String apiKeyQueryParam) {
        System.out.println("getBusStops request");

        String apiKey = getApiKeyFromRequest(apiKeyQueryParam, headers);
        if (apiKeyNotNull(apiKey) && isValidKey(apiKey)) {

            BusStopForecastContainer originalContainer = soapService.getUserRealTimeForecast(busStopId);
            System.out.println(originalContainer);

            if (originalContainer.busStopNodeInfos == null && Integer.valueOf(originalContainer.total) < 1) {
                return Response.status(Response.Status.NOT_FOUND).entity("No valid bus stop identificator").build();
            }

            if (originalContainer.busStopNodeInfos != null && originalContainer.busStopNodeInfos.size() > 0 &&
                    (originalContainer.busStopForecasts == null || (originalContainer.busStopForecasts != null && originalContainer.busStopForecasts.size() < 1)) && Integer.parseInt(originalContainer.total) < 1) {
                return Response.status(Response.Status.NO_CONTENT).entity(null).build();
            }

            String data = gson.toJson(originalContainer, BusStopForecastContainer.class);
            System.out.println(data);


            try {
                URI uri = uriInfo != null ? uriInfo.getAbsolutePath()
                        : new URI(String.format("%s/%s", "/atb/departures", busStopId));
                return Response.created(uri)
                        .entity(data)
                        .status(Response.Status.OK)
                        .build();
            } catch (URISyntaxException e) {
                throw new RuntimeException("Failed to create uri", e);
            }
        } else {
            return Response.status(Response.Status.FORBIDDEN).entity("Missing api-key, contact busbuddy 'at' norrs.no requesting api-key. Include application name, link to application and contact information and set subject with 'Request BusBuddy API key - appname'").build();
        }

        //return Response.noContent().build();
    }

   /* @GET
    @Path("/skrot/convert/busstops")
    @Produces({"application/json; charset=UTF-8"})
    public Response convertSkrotBusStops(@QueryParam("apiKey") String apiKeyQueryParam) {
        String apiKey = getApiKeyFromRequest(apiKeyQueryParam, headers);
        if (apiKeyNotNull(apiKey) && isValidKey(apiKey)) {

            BusStopContainerSkrot dataToConvert = skrotController.getData();

            for (BusStopSkrot busStop : dataToConvert.stops) {
                busStopDAO.insertOrUpdateSkrot(busStop);
            }

            try {
                URI uri = uriInfo != null ? uriInfo.getAbsolutePath()
                        : new URI("/skrot/convert/busstops");
                return Response.created(uri)
                        .entity("OK")
                        .status(Response.Status.OK)
                        .build();
            } catch (URISyntaxException e) {
                throw new RuntimeException("Failed to create uri", e);
            }
        } else {
            return Response.status(Response.Status.FORBIDDEN).entity("Missing api-key, contact busbuddy 'at' norrs.no requesting api-key. Include application name, link to application and contact information and set subject with 'Request BusBuddy API key - appname'").build();
        }
    }*/


}
