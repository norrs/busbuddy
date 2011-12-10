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
import no.norrs.busbuddy.api.AtbController;
import no.norrs.busbuddy.api.AtbRpController;
import no.norrs.busbuddy.api.AtbRpControllerImpl;
import no.norrs.busbuddy.api.atb.model.BusStopForecastContainer;
import no.norrs.busbuddy.api.atb.model.BusStopNodeInfo;
import no.norrs.busbuddy.api.dao.*;
import no.norrs.busbuddy.api.model.ApiKeyLog;
import no.norrs.busbuddy.api.ping.BusbuddyPing;
import no.norrs.busbuddy.api.ping.CedlindBusmapPing;
import no.norrs.busbuddy.pub.api.CharSetAdapter;
import no.norrs.busbuddy.pub.api.InstantTypeConverter;
import no.norrs.busbuddy.pub.api.LocalDateTimeTypeConverter;
import no.norrs.busbuddy.pub.api.OracleServiceController;
import no.norrs.busbuddy.pub.api.model.*;
import no.norrs.busbuddy.service.DepartureCache;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.soap.SOAPException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Roy Sindre Norangshol
 * Date: 8/20/11
 * Time: 4:17 PM
 */
@Component
@Path("/")
@Singleton

public class AtbServiceVersionResource extends SharedResources {
    private Gson gson;

    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    @Context
    HttpHeaders headers;
    private ApiKeyLogDAO loggerDAO;
    private BusStopDAO busstopDAO;
    private TripsDAO tripsDAO;
    private OracleServiceController oracleService;
    private AtbController atbService;
    private AtbRpController rpController;
    private List<BusbuddyPing> pingList;
    private LocalDateTime busStopContainerCacheTimestamp;
    private List<BusStop> busStopContainerCache;


    @Autowired
    public AtbServiceVersionResource(ApiKeyLogDAO apiKeyLogDAO, ApplicationTypeDAO applicationTypeDAO, BusBuddyApiKeyDAO busBuddyApiKeyDAO, BusStopDAO busStopDAO, TripsDAO tripsDAO) throws IOException {
        super(busBuddyApiKeyDAO);
        this.loggerDAO = apiKeyLogDAO;
        this.busstopDAO = busStopDAO;
        this.tripsDAO = tripsDAO;

/*
        Properties atbProperties = new Properties();
        atbProperties.load(getClass().getResourceAsStream("/atbapikey.properties"));
        soapService = new AtbSoapController(atbProperties.getProperty("username"), atbProperties.getProperty("password"));
*/
        atbService = controllerFactory.createRealtimeSoapController();

        oracleService = new OracleServiceController();
        rpController = new AtbRpControllerImpl(busStopDAO, tripsDAO);

        GsonBuilder builder = new GsonBuilder();
        gson = builder
                .registerTypeAdapter(String.class, new CharSetAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeConverter())
                .registerTypeAdapter(Instant.class, new InstantTypeConverter())
                .setPrettyPrinting()
                .create();

        this.pingList = new ArrayList<BusbuddyPing>(Arrays.asList(new BusbuddyPing[]{new CedlindBusmapPing()}));

        /*loggerDAO = (ApiKeyLogDAO) context.getBean("apikeylogDAO");
        busstopDAO = (BusStopDAO) context.getBean("busstopDAO");*/
    }


    @GET
    @Path("/busstops")
    @Produces({"application/json; charset=UTF-8"})
    public Response getBusStops(@QueryParam("apiKey") String apiKeyQueryParam, @QueryParam("callback") String callbackQueryParam) {

        String apiKey = getApiKeyFromRequest(apiKeyQueryParam, headers);

        if (apiKeyNotNull(apiKey) && apiKey.equalsIgnoreCase(BUSBUDDY_HTML_APP_APIKEY) && !validateIfLocalRequest(headers)) {
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
    @Path("/departures/{locationId}")
    @Produces({"application/json; charset=UTF-8"})
    public Response getDepartures(@PathParam("locationId") String locationId, @QueryParam("apiKey") String apiKeyQueryParam, @QueryParam("callback") String callbackQueryParam) {
        String apiKey = getApiKeyFromRequest(apiKeyQueryParam, headers);
        if (apiKeyNotNull(apiKey) && apiKey.equalsIgnoreCase(BUSBUDDY_HTML_APP_APIKEY) && !validateIfLocalRequest(headers)) {
            loggerDAO.incrementHitcounterFor(new ApiKeyLog(BUSBUDDY_HTML_APP_APIKEY, getTimeStampForHitcounterLogging(), 450));
            return Response.status(450).entity("Blocked by Windows Parental Controls").build();
        } else if (apiKeyNotNull(apiKey) && isValidKey(apiKey)) {


            DepartureContainer container = null;
            BusStopForecastContainer originalContainer = null;
            LocalDateTime now = new LocalDateTime();
            if (departureCache.containsKey(locationId)) {
                if (now.isBefore(departureCache.get(locationId).getRequstTimestamp().plusMinutes(1))) {
                    container = departureCache.get(locationId).getDepartureContainer();
                    originalContainer = departureCache.get(locationId).getOriginalContainerFromAtb();
                    //Logger.getLogger(AtbServiceVersion1Resource.class.getName(), String.format("Cache hit on %s", locationId));
                }
            }
            if (container == null) {
                originalContainer = atbService.getUserRealTimeForecast(locationId);
                container = originalContainer.convertToApi();
                departureCache.put(locationId, new DepartureCache(now, container, originalContainer));
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

            BusStop busStopFromDB = busstopDAO.findBusStopByLocationId(locationId);

            String nodeDescription = atbBusStopNodeInfo.getFixedNodeDescriptionOrFallBackToNormalNodeDescription();
            BusStop updateOrInsertIfNeeded = new BusStop(
                    null,
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

            for (BusbuddyPing ping : pingList) {
                ping.ping(locationId, container);
            }

            try {
                loggerDAO.incrementHitcounterFor(new ApiKeyLog(apiKey, getTimeStampForHitcounterLogging(), Response.Status.OK.getStatusCode()));
                URI uri = uriInfo != null ? uriInfo.getAbsolutePath()
                        : new URI(String.format("%s/%s", "/buss/incoming", locationId));
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
    @Path("/search")
    @Produces({"application/json; charset=UTF-8"})
    public Response getClosestBusStopsFromSearchWord(@QueryParam("q") String searchWord, @QueryParam("apiKey") String apiKeyQueryParam, @QueryParam("callback") String callbackQueryParam) {
        String apiKey = getApiKeyFromRequest(apiKeyQueryParam, headers);
        if (apiKeyNotNull(apiKey) && apiKey.equalsIgnoreCase(BUSBUDDY_HTML_APP_APIKEY) && !validateIfLocalRequest(headers)) {
            loggerDAO.incrementHitcounterFor(new ApiKeyLog(BUSBUDDY_HTML_APP_APIKEY, getTimeStampForHitcounterLogging(), 450));
            return Response.status(450).entity("Blocked by Windows Parental Controls").build();
        } else if (apiKeyNotNull(apiKey) && isValidKey(apiKey)) {

            // Simple busstops cache
            LocalDateTime now = new LocalDateTime();
            if (busStopContainerCacheTimestamp == null || now.isAfter(busStopContainerCacheTimestamp.plusDays(1))) {
                busStopContainerCache = busstopDAO.findAll();
                busStopContainerCacheTimestamp = new LocalDateTime();
            }

            String data = null;
            if (callbackQueryParam != null) {
                data = String.format("busbuddyResponse%s(%s)", callbackQueryParam, gson.toJson(new BusStopContainer(busstopDAO.findAll()), BusStopContainer.class));
            } else {
                data = gson.toJson(new BusStopContainer(getCloestsBusStopFromSearchword(searchWord)), BusStopContainer.class);
            }

            if (data != null && data.trim().length() > 0) {
                try {
                    loggerDAO.incrementHitcounterFor(new ApiKeyLog(apiKey, getTimeStampForHitcounterLogging(), Response.Status.OK.getStatusCode()));
                    URI uri = uriInfo != null ? uriInfo.getAbsolutePath()
                            : new URI("/search");
                    return Response.created(uri)
                            .entity(data)
                            .status(Response.Status.OK)
                            .build();
                } catch (URISyntaxException e) {
                    throw new RuntimeException("Failed to create uri", e);
                }

            }
            return Response.noContent().build();
        } else {
            loggerDAO.incrementHitcounterFor(new ApiKeyLog(apiKey, getTimeStampForHitcounterLogging(), Response.Status.FORBIDDEN.getStatusCode()));
            return Response.status(Response.Status.FORBIDDEN).entity("Missing api-key, contact busbuddy 'at' norrs.no requesting api-key. Include application name, link to application and contact information and set subject with 'Request BusBuddy API key - appname'").build();
        }
    }

    @GET
    @Path("/schedules/{locationId}")
    @Produces({"application/json; charset=UTF-8"})
    public Response getBusStopsFromTrip(@PathParam("locationId") String locationId, @QueryParam("apiKey") String apiKeyQueryParam, @QueryParam("callback") String callbackQueryParam) {
        /*  String apiKey = getApiKeyFromRequest(apiKeyQueryParam, headers);
  if (apiKey.equalsIgnoreCase(BUSBUDDY_HTML_APP_APIKEY) && !validateIfLocalRequest(headers)) {
      loggerDAO.incrementHitcounterFor(new ApiKeyLog(BUSBUDDY_HTML_APP_APIKEY, getTimeStampForHitcounterLogging(), 450));
      return Response.status(450).entity("Blocked by Windows Parental Controls").build();
  } else if (apiKeyNotNull(apiKey) && isValidKey(apiKey)) {*/


        try {
            ScheduleContainer schedules = rpController.getSchedulesForecast(locationId);
            return Response.ok(gson.toJson(schedules)).build();
        } catch (IOException e) {

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return Response.serverError().build();
        }
        /*} else {
            loggerDAO.incrementHitcounterFor(new ApiKeyLog(apiKey, getTimeStampForHitcounterLogging(), Response.Status.FORBIDDEN.getStatusCode()));
            return Response.status(Response.Status.FORBIDDEN).entity("Missing api-key, contact busbuddy 'at' norrs.no requesting api-key. Include application name, link to application and contact information and set subject with 'Request BusBuddy API key - appname'").build();
        } */
    }

    @GET
    @Path("/trip/{tripId}")
    @Produces({"application/json; charset=UTF-8"})
    public Response getTripDepartures(@PathParam("tripId") int tripId, @QueryParam("apiKey") String apiKeyQueryParam, @QueryParam("callback") String callbackQueryParam) {
        /*  String apiKey = getApiKeyFromRequest(apiKeyQueryParam, headers);
  if (apiKey.equalsIgnoreCase(BUSBUDDY_HTML_APP_APIKEY) && !validateIfLocalRequest(headers)) {
      loggerDAO.incrementHitcounterFor(new ApiKeyLog(BUSBUDDY_HTML_APP_APIKEY, getTimeStampForHitcounterLogging(), 450));
      return Response.status(450).entity("Blocked by Windows Parental Controls").build();
  } else if (apiKeyNotNull(apiKey) && isValidKey(apiKey)) {*/


        try {
            StopsContainer departures = rpController.getBusStopsFor(tripId);
            return Response.ok(gson.toJson(departures)).build();
        } catch (IOException e) {

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return Response.serverError().build();
        }
        /*} else {
            loggerDAO.incrementHitcounterFor(new ApiKeyLog(apiKey, getTimeStampForHitcounterLogging(), Response.Status.FORBIDDEN.getStatusCode()));
            return Response.status(Response.Status.FORBIDDEN).entity("Missing api-key, contact busbuddy 'at' norrs.no requesting api-key. Include application name, link to application and contact information and set subject with 'Request BusBuddy API key - appname'").build();
        } */
    }


    @GET
    @Path("/oracle/{question}")
    @Produces({"application/json; charset=UTF-8"})
    public Response getBusStops(@PathParam("question") String question, @QueryParam("apiKey") String apiKeyQueryParam, @QueryParam("callback") String callbackQueryParam) {
        String apiKey = getApiKeyFromRequest(apiKeyQueryParam, headers);
        if (apiKeyNotNull(apiKey) && apiKey.equalsIgnoreCase(BUSBUDDY_HTML_APP_APIKEY) && !validateIfLocalRequest(headers)) {
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

    private List<BusStop> getCloestsBusStopFromSearchword(String searchWord) {
        searchWord = searchWord.toLowerCase();
        double[] scoreClosestBusStops = {Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE};
        BusStop[] closestBusStops = new BusStop[6];

        for (BusStop busStop : busStopContainerCache) {
            if (busStop.getName().contains("("))
                continue;
            double score = compareStrings(searchWord, busStop.getName());

            System.out.println(String.format("%s got %s", busStop.getName(), score));

            int lowestIndexSoFar = -1;
            for (int i = scoreClosestBusStops.length - 1; i >= 0; i--) {
                if (score < scoreClosestBusStops[i])
                    break;
                else if (score > scoreClosestBusStops[i])
                    lowestIndexSoFar = i;
            }

            if (lowestIndexSoFar != -1) {
                double tmpMove;
                double tmpInsert = score;
                BusStop tmpBusMove;
                BusStop tmpBusInsert = busStop;

                for (int j = lowestIndexSoFar; j < scoreClosestBusStops.length; j++) {
                    tmpMove = scoreClosestBusStops[j];
                    tmpBusMove = closestBusStops[j];

                    scoreClosestBusStops[j] = tmpInsert;
                    closestBusStops[j] = tmpBusInsert;

                    tmpInsert = tmpMove;
                    tmpBusInsert = tmpBusMove;
                }
            }
        }
        return Arrays.asList(closestBusStops);
    }


    //     http://www.catalysoft.com/articles/StrikeAMatch.html
    private static double compareStrings(String str1, String str2) {
        ArrayList pairs1 = wordLetterPairs(str1.toUpperCase());
        ArrayList pairs2 = wordLetterPairs(str2.toUpperCase());
        int intersection = 0;
        int union = pairs1.size() + pairs2.size();
        for (int i = 0; i < pairs1.size(); i++) {
            Object pair1 = pairs1.get(i);
            for (int j = 0; j < pairs2.size(); j++) {
                Object pair2 = pairs2.get(j);
                if (pair1.equals(pair2)) {
                    intersection++;
                    pairs2.remove(j);
                    break;
                }
            }
        }
        return (2.0 * intersection) / union;
    }

    /**
     * @return an ArrayList of 2-character Strings.
     */
    private static ArrayList wordLetterPairs(String str) {
        ArrayList allPairs = new ArrayList();
        // Tokenize the string and put the tokens/words into an array
        String[] words = str.split("\\s");
        // For each word
        for (int w = 0; w < words.length; w++) {
            // Find the pairs of characters
            String[] pairsInWord = letterPairs(words[w]);
            for (int p = 0; p < pairsInWord.length; p++) {
                allPairs.add(pairsInWord[p]);
            }
        }
        return allPairs;
    }

    /*
       @return an array of adjacent letter pairs contained in the input string
    */
    private static String[] letterPairs(String str) {
        int numPairs = str.length() - 1;
        String[] pairs = new String[numPairs];
        for (int i = 0; i < numPairs; i++) {
            pairs[i] = str.substring(i, i + 2);
        }
        return pairs;
    }

}
