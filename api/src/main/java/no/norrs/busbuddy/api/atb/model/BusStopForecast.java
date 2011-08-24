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
import no.norrs.busbuddy.pub.api.LocalDateTimeTypeConverter;
import no.norrs.busbuddy.pub.api.model.Departure;

import java.text.SimpleDateFormat;

/**
 * @author Roy Sindre Norangshol
 */
public class BusStopForecast {
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    @SerializedName("codAzLinea")
    public String lineId;
    @SerializedName("descrizioneLinea")
    public String lineDescription;
    @SerializedName("orario")
    public String registeredDepartureTime; // @todo fixme joda timestamp!
    @SerializedName("orarioSched")
    public String scheduledDepartureTime;
    @SerializedName("statoPrevisione")
    public String stationForecast;
    @SerializedName("capDest")
    public String destination;

    @Override
    public String toString() {
        return "BusStopForecast{" +
                "lineId=" + lineId +
                ", lineDescription='" + lineDescription + '\'' +
                ", registeredDepartureTime='" + registeredDepartureTime + '\'' +
                ", scheduledDepartureTime='" + scheduledDepartureTime + '\'' +
                ", stationForecast='" + stationForecast + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }

    public Departure convertToApi() {
        Departure api = new Departure();
        api.setLine(lineId.trim());  // lineDescription -  descrizioneLinea
        api.setDestination(destination.trim());
        api.setRealtimeData((stationForecast.equalsIgnoreCase("prev") ? true : false));
        api.setRegisteredDepartureTime(LocalDateTimeTypeConverter.getLocalDateTimeFromString(registeredDepartureTime));
        api.setScheduledDepartureTime(LocalDateTimeTypeConverter.getLocalDateTimeFromString(scheduledDepartureTime));
        return api;
    }

}
