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

package no.norrs.busbuddy.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.norrs.busbuddy.api.atb.model.BusListsContainer;
import no.norrs.busbuddy.api.atb.model.BusStopForecastContainer;
import no.norrs.busbuddy.pub.api.CharSetAdapter;
import no.norrs.busbuddy.pub.api.HttpUtil;
import no.norrs.busbuddy.pub.api.model.Schedule;
import no.norrs.busbuddy.pub.api.model.StopsContainer;
import org.apache.http.HttpResponse;
import org.joda.time.LocalDateTime;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Roy Sindre Norangshol
 */
public class AtbWebController implements AtbController {
    private Gson gson;
    private String endpoint;
    private String payload;

    public AtbWebController(String endpoint, String payload) {
        this.endpoint = endpoint;
        this.payload = payload;


        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder
                .serializeNulls()
                .registerTypeAdapter(String.class, new CharSetAdapter())
                .create();

    }

    /**
     * WebController can't query on locationId
     * @deprecated
     * @param busStopId busStopId (internal AtB index id)
     * @return BusStopForecastContainer with bus stop forecasts
     */
    public BusStopForecastContainer getUserRealTimeForecast(int busStopId) {
        return getUserRealTimeForecastFromJson(resolveEndpointWithPayload(busStopId));
    }
    
    @Override
    public BusStopForecastContainer getUserRealTimeForecast(String locationId) {
        throw new NoSuchMethodError("Not implemented due to not available to ask webcontroller for forecasts by using location id. sorry");
    }

    @Override
    public BusListsContainer getAllBusStopsListInTrondheim() {
        throw new NoSuchMethodError("Not implemented or maybe not even available with this resource..");
    }

    @Override
    public StopsContainer getBusStopsFor(int tripId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Schedule getSchedulesForecast(int locationId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    private BusStopForecastContainer getUserRealTimeForecastFromJson(String json) {
        return gson.fromJson(json, BusStopForecastContainer.class);
    }

    private BusListsContainer getBusStopsFromJson(String json) {
        return gson.fromJson(json, BusListsContainer.class);
    }

    /**
     * Implement this crap better maybe. *whisles*
     *
     * @param busStopId
     * @return
     */
    private String resolveEndpointWithPayload(int busStopId) {
        //return "{\"total\":5,\"InfoNodo\":[{\"nome_Az\":\"AtB\",\"codAzNodo\":\"16011404\",\"nomeNodo\":\"Solsi\",\"descrNodo\":\"1404 (Solsiden                       )\",\"bitMaskProprieta\":\"\",\"codeMobile\":\"1404 (Solsiden                       )\",\"coordLon\":\"10.412957\",\"coordLat\":\"63.434006\"}],\"Orari\":[{\"codAzLinea\":\"66\",\"descrizioneLinea\":\"66\",\"orario\":\"30/06/2011 22:30\",\"orarioSched\":\"30/06/2011 22:26\",\"statoPrevisione\":\"Prev\",\"capDest\":\"Dronningens gt.                \"},{\"codAzLinea\":\"9\",\"descrizioneLinea\":\"9\",\"orario\":\"30/06/2011 22:37\",\"orarioSched\":\"30/06/2011 22:33\",\"statoPrevisione\":\"Prev\",\"capDest\":\"Munkegata - M2                 \"},{\"codAzLinea\":\"7\",\"descrizioneLinea\":\"7\",\"orario\":\"30/06/2011 22:37\",\"orarioSched\":\"30/06/2011 22:28\",\"statoPrevisione\":\"Prev\",\"capDest\":\"Munkegata - M2                 \"},{\"codAzLinea\":\"60\",\"descrizioneLinea\":\"60\",\"orario\":\"30/06/2011 22:43\",\"orarioSched\":\"30/06/2011 22:37\",\"statoPrevisione\":\"Prev\",\"capDest\":\"Dronningens gt.                \"},{\"codAzLinea\":\"9\",\"descrizioneLinea\":\"9\",\"orario\":\"30/06/2011 23:10\",\"orarioSched\":\"30/06/2011 23:03\",\"statoPrevisione\":\"Prev\",\"capDest\":\"Munkegata - M2                 \"}]}";
        LocalDateTime currentTime = new LocalDateTime();
        try {
            System.out.println(endpoint);
            System.out.println(String.format(payload, busStopId, currentTime.toString("yyyy-MM-dd"), currentTime.toString("HH:mm")));
            HttpResponse response = HttpUtil.POST(new URL(endpoint), String.format(payload, busStopId, currentTime.toString("yyyy-MM-dd"), currentTime.toString("HH:mm")));
            String result = HttpUtil.readString(response.getEntity().getContent());
            result = removeFailedEscapingFromAtBWeb(result);
            return result;

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (URISyntaxException e) {
            return "";
        }
    }

    private String removeFailedEscapingFromAtBWeb(String json) {
        String tmp = json.replaceAll("\\\\", "").trim();
        return tmp.substring(1, tmp.length() - 1);
    }

}
