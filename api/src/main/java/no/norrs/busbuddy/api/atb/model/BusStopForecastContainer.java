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

package no.norrs.busbuddy.api.atb.model;


import com.google.gson.annotations.SerializedName;
import no.norrs.busbuddy.pub.api.model.Departure;
import no.norrs.busbuddy.pub.api.model.DepartureContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roy Sindre Norangshol
 */
public class BusStopForecastContainer {
    @SerializedName("InfoNodo")
    public List<BusStopNodeInfo> busStopNodeInfos;
    @SerializedName("Orari")
    public List<BusStopForecast> busStopForecasts;
    public String total;

    @Override
    public String toString() {
        return "BusStopForecastContainer{" +
                "busStopNodeInfos=" + busStopNodeInfos +
                ", busStopForecasts=" + busStopForecasts +
                ", total='" + total + '\'' +
                '}';
    }

    public DepartureContainer convertToApi() {
        DepartureContainer api = new DepartureContainer();

        api.isValid = false;
        if (busStopNodeInfos != null) {
            if (busStopNodeInfos.size()>0)   {
                api.setGoingTowardsCentrum(Integer.parseInt(busStopNodeInfos.get(0).nodeId));
                api.setDepartures(convertBusStopForecastsToApi());
                api.isValid = true;
            }
        } else {
            api.isValid = false;
        }
        return api;
    }



    private List<Departure> convertBusStopForecastsToApi() {
        if (busStopForecasts != null) {
            List<Departure> api = new ArrayList<Departure>();
            for (BusStopForecast forecast : busStopForecasts) {
                api.add(forecast.convertToApi());
            }
            return api;
        }
        return null;

    }


}
