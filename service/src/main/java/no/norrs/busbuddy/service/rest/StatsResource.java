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
import no.norrs.busbuddy.api.dao.ApiKeyLogDAO;
import no.norrs.busbuddy.api.dao.BusBuddyApiKeyDAO;
import no.norrs.busbuddy.api.model.BusBuddyApiKey;
import no.norrs.busbuddy.api.model.helper.HitsPerDay;
import no.norrs.busbuddy.pub.api.CharSetAdapter;
import no.norrs.busbuddy.pub.api.InstantTypeConverter;
import no.norrs.busbuddy.pub.api.LocalDateTimeTypeConverter;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Roy Sindre Norangshol
 */
@Path("/stats")
@Singleton
public class StatsResource extends SharedResources {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    @Context
    HttpHeaders headers;
    @Context
    javax.servlet.http.HttpServletRequest httpServletRequest;

    ApplicationContext context;
    private ApiKeyLogDAO loggerDAO;
    private BusBuddyApiKeyDAO apikeyDAO;
    private Gson gson;

    public StatsResource() {
        super();
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
        GsonBuilder builder = new GsonBuilder();
        gson = builder
                .registerTypeAdapter(String.class, new CharSetAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeConverter())
                .registerTypeAdapter(Instant.class, new InstantTypeConverter())
                .create();
        loggerDAO = (ApiKeyLogDAO) context.getBean("apikeylogDAO");
        apikeyDAO = (BusBuddyApiKeyDAO) context.getBean("busbuddyapikeyDAO");
    }

    @GET
    @Path("/total/lastdays/30")
    @Produces({"application/json; charset=UTF-8"})
    public Response getHitCountsPerDay(@QueryParam("callback") String callbackQueryParam) {

        if (requestFromLocalServer()) {


            List<HitsPerDay> hitsLast30Days = loggerDAO.getSummedHitCountFor30Days();
            String entity;
            if (callbackQueryParam != null) {
                entity = String.format("%s(%s)", callbackQueryParam, gson.toJson(hitsLast30Days));
            } else {
                entity = gson.toJson(hitsLast30Days);
            }
            URI uri = null;
            try {
                uri = uriInfo != null ? uriInfo.getAbsolutePath()
                        : new URI("/total/lastdays/30");
                return Response.created(uri)
                        .entity(entity)
                        .status(Response.Status.OK)
                        .build();
            } catch (URISyntaxException e) {
                return Response.serverError().entity("URI Syntax Exception :O").build();
            }


        }
        return Response.status(Response.Status.FORBIDDEN).entity("Internal resource only, if you want these data - please poke me at my contact email..").build();

    }

    @GET
    @Path("/total/apikeys/lastdays/30")
    @Produces({"application/json; charset=UTF-8"})
    public Response getHitCountsPerDayByApikey(@QueryParam("callback") String callbackQueryParam) throws Exception {
        //Object requestFoo = messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);

        if (requestFromLocalServer()) {

            Map<String, List<HitsPerDay>> hitsForAllApiKeys = new HashMap<String, List<HitsPerDay>>();
            for (BusBuddyApiKey apiKey : apiKeys) {
                hitsForAllApiKeys.put(String.format("%s (%s)", apiKey.getAppName(), apiKey.getApplicationType().getType()), loggerDAO.getHitCountByApiKeyFor30Days(apiKey.getApiKey()));
            }
            String entity;
            if (callbackQueryParam != null) {
                entity = String.format("%s(%s)", callbackQueryParam, gson.toJson(hitsForAllApiKeys));
            } else {
                entity = gson.toJson(hitsForAllApiKeys);
            }
            URI uri = null;
            try {
                uri = uriInfo != null ? uriInfo.getAbsolutePath()
                        : new URI("/apikeys/lastdays/30");
                return Response.created(uri)
                        .entity(entity)
                        .status(Response.Status.OK)
                        .build();
            } catch (URISyntaxException e) {
                return Response.serverError().entity("URI Syntax Exception :O").build();
            }

        }
        return Response.status(Response.Status.FORBIDDEN).entity("Internal resource only, if you want these data - please poke me at my contact email..").build();

    }

    @GET
    @Path("/{apiKey}/lastdays/30")
    @Produces({"application/json; charset=UTF-8"})
    public Response getAllTypesHitCountsPerDayByApikey(@PathParam("apiKey") String apiKeyPathParam, @QueryParam("callback") String callbackQueryParam) throws Exception {
        //Object requestFoo = messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);

        if (requestFromLocalServer()) {
            String apiKey = getApiKeyFromRequest(apiKeyPathParam, headers);
            if (apiKeyNotNull(apiKey) && isValidKey(apiKey)) {


                Map<Integer, List<HitsPerDay>> hitsForAllApiKeys = loggerDAO.getHitCountByApiKeyWithResultCodesLast30Days(apiKey);
                String entity;
                if (callbackQueryParam != null) {
                    entity = String.format("%s(%s)", callbackQueryParam, gson.toJson(hitsForAllApiKeys));
                } else {
                    entity = gson.toJson(hitsForAllApiKeys);
                }
                URI uri = null;
                try {
                    uri = uriInfo != null ? uriInfo.getAbsolutePath()
                            : new URI("/apikeys/lastdays/30");
                    return Response.created(uri)
                            .entity(entity)
                            .status(Response.Status.OK)
                            .build();
                } catch (URISyntaxException e) {
                    return Response.serverError().entity("URI Syntax Exception :O").build();
                }


            } else {
                return Response.status(Response.Status.FORBIDDEN).entity("Wrong api key").build();
            }
        }
        return Response.status(Response.Status.FORBIDDEN).entity("Internal resource only, if you want these data - please poke me at my contact email..").build();

    }

    private boolean requestFromLocalServer() {
        return httpServletRequest.getRemoteAddr().equalsIgnoreCase("0:0:0:0:0:0:0:1") || httpServletRequest.getRemoteAddr().equalsIgnoreCase("127.0.0.1") || httpServletRequest.getRemoteAddr().equalsIgnoreCase("129.241.105.225") || httpServletRequest.getRemoteAddr().equalsIgnoreCase("192.168.69.25");
    }

}
