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

package no.norrs.busbuddy.pub.api.model;

import org.joda.time.LocalDateTime;

/**
 * Roy Sindre Norangshol
 * Date: 8/20/11
 * Time: 4:30 PM
 */
public class Stops {
    private LocalDateTime scheduledTime;
    private String destination;
    private String municipality;
    private String locationId;

    public Stops(LocalDateTime scheduledTime, String destination, String municipality, String locationId) {
        this.scheduledTime = scheduledTime;
        this.destination = destination;
        this.municipality = municipality;
        this.locationId = locationId;
    }

    public Stops() {}

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    @Override
    public String toString() {
        return "Stops{" +
                "scheduledTime=" + scheduledTime +
                ", destination='" + destination + '\'' +
                ", municipality='" + municipality + '\'' +
                ", locationId='" + locationId + '\'' +
                '}';
    }

    /*
    {"tripID":"7233","line":"52","destination":"Vestlia endeholdeplass","stops":[{"time":"12:25","destination":"Munkegata - M1","busstopID":"16010001"},{"time":"12:28","destination":"Prinsen Kinosenter","busstopID":"16010011"},{"time":"12:29","destination":"Studentersamfundet","busstopID":"16010477"},{"time":"12:30","destination":"Vollabakken","busstopID":"16010550"},{"time":"12:30","destination":"H\u00f8gskoleringen","busstopID":"16010197"},{"time":"12:32","destination":"Gl\u00f8shaugen Nord","busstopID":"16010333"},{"time":"12:32","destination":"Gl\u00f8shaugen Syd","busstopID":"16010265"},{"time":"12:33","destination":"Dybdahls veg","busstopID":"16010102"},{"time":"12:34","destination":"Fiolsvingen","busstopID":"16010131"},{"time":"12:35","destination":"Nardokrysset","busstopID":"16010320"},{"time":"12:36","destination":"Nardosenteret","busstopID":"16010341"},{"time":"12:37","destination":"Hoeggen skole","busstopID":"16010177"},{"time":"12:38","destination":"Vestlia","busstopID":"16010546"},{"time":"12:38","destination":"Arne Bergsg\u00e5rds veg","busstopID":"16010044"},{"time":"12:39","destination":"Vestlia endeholdeplass","busstopID":"16010632"}]}
    */
}
