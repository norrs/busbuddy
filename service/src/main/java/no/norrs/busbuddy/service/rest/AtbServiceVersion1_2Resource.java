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
import no.norrs.busbuddy.api.atb.model.BusStopForecastContainer;
import no.norrs.busbuddy.api.atb.model.BusStopNodeInfo;
import no.norrs.busbuddy.api.dao.ApiKeyLogDAO;
import no.norrs.busbuddy.api.dao.BusStopDAO;
import no.norrs.busbuddy.api.model.ApiKeyLog;
import no.norrs.busbuddy.pub.api.CharSetAdapter;
import no.norrs.busbuddy.pub.api.InstantTypeConverter;
import no.norrs.busbuddy.pub.api.LocalDateTimeTypeConverter;
import no.norrs.busbuddy.pub.api.OracleServiceController;
import no.norrs.busbuddy.pub.api.model.BusStop;
import no.norrs.busbuddy.pub.api.model.BusStopContainer;
import no.norrs.busbuddy.pub.api.model.DepartureContainer;
import no.norrs.busbuddy.pub.api.model.Oracle;
import no.norrs.busbuddy.service.DepartureCache;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Roy Sindre Norangshol
 * Date: 5/27/11
 * Time: 4:26 PM
 */


@Path("/1.2")
@Singleton
public class AtbServiceVersion1_2Resource extends SharedResources {

    private AtbSoapController soapService;
    private Gson gson;


    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    @Context
    HttpHeaders headers;
    private ApiKeyLogDAO loggerDAO;
    private BusStopDAO busstopDAO;
    private OracleServiceController oracleService;


    public AtbServiceVersion1_2Resource() throws IOException {
        super();

        Properties atbProperties = new Properties();
        atbProperties.load(getClass().getResourceAsStream("/atbapikey.properties"));
        soapService = new AtbSoapController(atbProperties.getProperty("username"), atbProperties.getProperty("password"));

        oracleService = new OracleServiceController();

        GsonBuilder builder = new GsonBuilder();
        gson = builder.serializeNulls()
                .registerTypeAdapter(String.class, new CharSetAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeConverter())
                .registerTypeAdapter(Instant.class, new InstantTypeConverter())
                .create();

        loggerDAO = (ApiKeyLogDAO) context.getBean("apikeylogDAO");
        busstopDAO = (BusStopDAO) context.getBean("busstopDAO");
    }


    @GET
    @Path("/busstops")
    @Produces({"application/json; charset=UTF-8"})
    public Response getBusStops(@QueryParam("apiKey") String apiKeyQueryParam, @QueryParam("callback") String callbackQueryParam) {

        String apiKey = getApiKeyFromRequest(apiKeyQueryParam, headers);

        if (apiKey.equalsIgnoreCase(BUSBUDDY_HTML_APP_APIKEY) && !validateIfLocalRequest(headers)) {
            loggerDAO.incrementHitcounterFor(new ApiKeyLog(BUSBUDDY_HTML_APP_APIKEY, getTimeStampForHitcounterLogging(), 450));
            return Response.status(450).entity("Blocked by Windows Parental Controls, stop being a scriptkiddie and just email me for an api key ;-)").build();
        } else if (apiKeyNotNull(apiKey) && isValidKey(apiKey)) {
            String data = null;

            if (callbackQueryParam != null) {
                data = String.format("busbuddyResponse%s(%s)", callbackQueryParam, gson.toJson(new BusStopContainer(busstopDAO.findAll()), BusStopContainer.class));
            } else {
                data = gson.toJson(new BusStopContainer(busstopDAO.findAll()), BusStopContainer.class);
            }

            if (data != null && data.trim().length() > 0) {
                try {
                    loggerDAO.incrementHitcounterFor(new ApiKeyLog(apiKey, getTimeStampForHitcounterLogging(), Response.Status.OK.getStatusCode()));
                    URI uri = uriInfo != null ? uriInfo.getAbsolutePath()
                            : new URI("/busstops");
                    return Response.created(uri)
                            .entity(data)
                            .status(Response.Status.OK)
                            .build();
                } catch (URISyntaxException e) {
                    throw new RuntimeException("Failed to create uri", e);
                }
            }
            loggerDAO.incrementHitcounterFor(new ApiKeyLog(apiKey, getTimeStampForHitcounterLogging(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Missing busstops.json in local server storage, contact system admin").build();

        } else {
            loggerDAO.incrementHitcounterFor(new ApiKeyLog(apiKey, getTimeStampForHitcounterLogging(), Response.Status.FORBIDDEN.getStatusCode()));
            return Response.status(Response.Status.FORBIDDEN).entity("Missing api-key, contact busbuddy 'at' norrs.no requesting api-key. Include application name, link to application and contact information and set subject with 'Request BusBuddy API key - appname'").build();
        }


    }


    @GET
    @Path("/departures/{busStopId}")
    @Produces({"application/json; charset=UTF-8"})
    public Response getBusStops(@PathParam("busStopId") int busStopId, @QueryParam("apiKey") String apiKeyQueryParam, @QueryParam("callback") String callbackQueryParam) {
        String apiKey = getApiKeyFromRequest(apiKeyQueryParam, headers);
        if (apiKey.equalsIgnoreCase(BUSBUDDY_HTML_APP_APIKEY) && !validateIfLocalRequest(headers)) {
            loggerDAO.incrementHitcounterFor(new ApiKeyLog(BUSBUDDY_HTML_APP_APIKEY, getTimeStampForHitcounterLogging(), 450));
            return Response.status(450).entity("Blocked by Windows Parental Controls").build();
        } else if (apiKeyNotNull(apiKey) && isValidKey(apiKey)) {


            DepartureContainer container = null;
            BusStopForecastContainer originalContainer = null;
            LocalDateTime now = new LocalDateTime();
            if (departureCache.containsKey(busStopId)) {
                if (now.isBefore(departureCache.get(busStopId).getRequstTimestamp().plusMinutes(1))) {
                    container = departureCache.get(busStopId).getDepartureContainer();
                    Logger.getLogger(AtbServiceVersion1Resource.class.getName(), String.format("Cache hit on %s", busStopId));
                }
            }
            if (container == null) {
                originalContainer = soapService.getUserRealTimeForecast(busStopId);
                container = originalContainer.convertToApi();
                departureCache.put(busStopId, new DepartureCache(now, container, originalContainer));
            }


            if (container.isValid && (container.getDepartures() == null || (container.getDepartures() != null && container.getDepartures().size() < 1))) {
                loggerDAO.incrementHitcounterFor(new ApiKeyLog(apiKey, getTimeStampForHitcounterLogging(), Response.Status.NO_CONTENT.getStatusCode()));
                return Response.status(Response.Status.NO_CONTENT).entity(null).build();
            }

            if (!container.isValid) {
                loggerDAO.incrementHitcounterFor(new ApiKeyLog(apiKey, getTimeStampForHitcounterLogging(), Response.Status.NOT_FOUND.getStatusCode()));
                return Response.status(Response.Status.NOT_FOUND).entity("No valid bus stop identificator").build();
            }

            BusStopNodeInfo atbBusStopNodeInfo = originalContainer.busStopNodeInfos.get(0);

            BusStop busStopFromDB = busstopDAO.findBusStopById(busStopId);

            String nodeDescription = atbBusStopNodeInfo.getFixedNodeDescriptionOrFallBackToNormalNodeDescription();
            BusStop updateOrInsertIfNeeded = new BusStop(
                    busStopId,
                    nodeDescription,
                    nodeDescription,
                    atbBusStopNodeInfo.name,
                    String.valueOf(atbBusStopNodeInfo.nodeId),
                    atbBusStopNodeInfo.longitude,
                    atbBusStopNodeInfo.latitude

            );

            if (busStopFromDB != null) {
                updateOrInsertIfNeeded.setName(busStopFromDB.getName()); // uses converted fixed name if manually fixed in db.
            }
            busstopDAO.insertOrUpdate(updateOrInsertIfNeeded);


            String data = null;
            if (callbackQueryParam != null) {
                data = String.format("busbuddyResponse%s(%s)", callbackQueryParam, gson.toJson(container));
            } else {
                data = gson.toJson(container);
            }


            try {
                loggerDAO.incrementHitcounterFor(new ApiKeyLog(apiKey, getTimeStampForHitcounterLogging(), Response.Status.OK.getStatusCode()));
                URI uri = uriInfo != null ? uriInfo.getAbsolutePath()
                        : new URI(String.format("%s/%s", "/buss/incoming", busStopId));
                return Response.created(uri)
                        .entity(data)
                        .status(Response.Status.OK)
                        .build();
            } catch (URISyntaxException e) {
                throw new RuntimeException("Failed to create uri", e);
            }
        } else {
            loggerDAO.incrementHitcounterFor(new ApiKeyLog(apiKey, getTimeStampForHitcounterLogging(), Response.Status.FORBIDDEN.getStatusCode()));
            return Response.status(Response.Status.FORBIDDEN).entity("Missing api-key, contact busbuddy 'at' norrs.no requesting api-key. Include application name, link to application and contact information and set subject with 'Request BusBuddy API key - appname'").build();
        }
    }

    @GET
    @Path("/oracle/{question}")
    @Produces({"application/json; charset=UTF-8"})
    public Response getBusStops(@PathParam("question") String question, @QueryParam("apiKey") String apiKeyQueryParam, @QueryParam("callback") String callbackQueryParam) {
        String apiKey = getApiKeyFromRequest(apiKeyQueryParam, headers);
        if (apiKey.equalsIgnoreCase(BUSBUDDY_HTML_APP_APIKEY) && !validateIfLocalRequest(headers)) {
            loggerDAO.incrementHitcounterFor(new ApiKeyLog(BUSBUDDY_HTML_APP_APIKEY, getTimeStampForHitcounterLogging(), 450));
            return Response.status(450).entity("Blocked by Windows Parental Controls").build();
        } else if (apiKeyNotNull(apiKey) && isValidKey(apiKey)) {
            try {
                Oracle answer = oracleService.askQuestion(question);
                String oracleAnswerJson = gson.toJson(answer);
                loggerDAO.incrementHitcounterFor(new ApiKeyLog(apiKey, getTimeStampForHitcounterLogging(), Response.Status.OK.getStatusCode()));
                return Response.status(Response.Status.OK).entity(oracleAnswerJson).build();
            } catch (IOException e) {


                loggerDAO.incrementHitcounterFor(new ApiKeyLog(apiKey, getTimeStampForHitcounterLogging(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()));
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }

        } else {
            loggerDAO.incrementHitcounterFor(new ApiKeyLog(apiKey, getTimeStampForHitcounterLogging(), Response.Status.FORBIDDEN.getStatusCode()));
            return Response.status(Response.Status.FORBIDDEN).entity("Missing api-key, contact busbuddy 'at' norrs.no requesting api-key. Include application name, link to application and contact information and set subject with 'Request BusBuddy API key - appname'").build();
        }
    }


}
