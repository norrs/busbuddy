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

package no.norrs.busbuddy.pub.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.norrs.busbuddy.pub.api.model.BusStopContainer;
import no.norrs.busbuddy.pub.api.model.DepartureContainer;
import no.norrs.busbuddy.pub.api.model.Oracle;
import org.apache.http.HttpResponse;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;

import java.io.IOException;
import java.io.InputStream;

/**
 * Bus Buddy API Service Controller deals with communicating with Bus Buddy API
 * and is able to retreive bus stops and bus stop forecasts.
 *
 * Requires Google GSON and Joda-Time libraries.
 *
 * @author Roy Sindre Norangshol
 */
public class BusBuddyAPIServiceController {
    private final String END_POINT = "http://api.busbuddy.no:8080/api/1.2/";
    private final String GET_BUS_STOPS = "busstops/";
    private final String GET_BUS_STOP_FORECASTS = "departures/%s";

    private String apiKey;
    private Gson gson;


    public BusBuddyAPIServiceController() {
        try {
            InputStream inputSource = getClass().getResourceAsStream("/busbuddyapi.key");
            if (inputSource != null) {
                apiKey = HttpUtil.readString(inputSource);
            }   else throw new RuntimeException("Can't detect busbuddyapi.key file");

        } catch (IOException e) {
            throw new RuntimeException(String.format("Can't detect busbuddyapi.key file %s", e.getMessage(),e));
        }
        validateApiKeyInput(apiKey);
    }

    public BusBuddyAPIServiceController(String apiKey) {
        validateApiKeyInput(apiKey);
        this.apiKey = apiKey;
    }

    private void validateApiKeyInput(String apiKey) {
        if (apiKey != null && apiKey.trim().length() > 0) {

            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder
                    .serializeNulls()
                    .registerTypeAdapter(String.class, new CharSetAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeConverter())
                    .registerTypeAdapter(Instant.class, new InstantTypeConverter())
                    .create();
        } else {
            throw new RuntimeException("Missing API key for BusBuddy API, please add a busbuddyapi.key file and put it under resources. (available in classpath..");
        }
    }


    /**
     * Retreives bus stop forecasts encapsuled in an @see DepartureContainer .
     *
     * @param busStopId bus stop id you want bus stop forecasts for
     * @return DepartureContainer A container can have an empty list if it contains no departures for that bus stop. Return's null if http request is not 200 or 204.
     * @throws IOException Any IO error on the service side.
     */
    public DepartureContainer getBusStopForecasts(int busStopId) throws IOException {
        HttpResponse response = HttpUtil.GET(apiKey, String.format("%s%s", END_POINT, String.format(GET_BUS_STOP_FORECASTS, busStopId)));

        if (response.getStatusLine().getStatusCode() == 200) {
            return gson.fromJson(HttpUtil.readString(response.getEntity().getContent()), DepartureContainer.class);
        } else if (response.getStatusLine().getStatusCode() == 204) {
            DepartureContainer container = new DepartureContainer();
            return container;
        }

        return null;
    }

    /**
     * Retreives all recorded bus stops that is recorded in bus buddy's api database.
     * @return Lists over @see BusStop 's encapsulated in a @see BusStopContainer
     * @throws IOException
     */
    public BusStopContainer getBusStops() throws IOException {
        HttpResponse response = HttpUtil.GET(apiKey, String.format("%s%s", END_POINT, GET_BUS_STOPS));
        if (response.getStatusLine().getStatusCode() == 200) {
            return gson.fromJson(HttpUtil.readString(response.getEntity().getContent()), BusStopContainer.class);
        }
        return null;
    }

    public Oracle askOracle(Oracle question) throws IOException {
        if (question.getQuestion() != null && !question.getQuestion().trim().equalsIgnoreCase("")) {
            HttpResponse response = HttpUtil.GET(apiKey, question.getEndpointWithQuestion());
            if (response.getStatusLine().getStatusCode() == 200) {
                question.setAnswer(HttpUtil.readString(response.getEntity().getContent()));
                question.setTimestamp(new LocalDateTime());
                return question; // with answer
            }
        }
        return null;
    }
}


