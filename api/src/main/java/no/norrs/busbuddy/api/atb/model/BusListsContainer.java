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

import java.util.List;

/**
 * @author Roy Sindre Norangshol
 */
public class BusListsContainer {


    @SerializedName(value = "Fermate")
    public List<BusStop> busStops;
    public String total;


    public BusListsContainer() {

    }

    public List<BusStop> convertToApi() {


        /*  api.busStops = convertBusStopsToApi();
       api.total = total;
       return api;*/
        return null;
    }


    @Override
    public String toString() {
        return "BusListsContainer{" +
                "busStops=" + busStops +
                ", total='" + total + '\'' +
                '}';
    }
}
