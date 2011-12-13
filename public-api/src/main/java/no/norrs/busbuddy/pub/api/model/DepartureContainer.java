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


import java.util.List;


/**
 * @author Roy Sindre Norangshol
 */
public class DepartureContainer {
    private boolean isGoingTowardsCentrum;
    private List<Departure> departures;
    public static boolean isValid;

    public DepartureContainer() {}

    public List<Departure> getDepartures() {
        return departures;
    }

    public void setDepartures(List<Departure> departures) {
        this.departures = departures;
    }

    public boolean isGoingTowardsCentrum() {
        return isGoingTowardsCentrum;
    }

    public void setGoingTowardsCentrum(int nodeIdAtb) {
        isGoingTowardsCentrum = (nodeIdAtb/1000)%2==1;
    }

    @Override
    public String toString() {
        return "DepartureContainer{" +
                "isGoingTowardsCentrum=" + isGoingTowardsCentrum +
                ", departures=" + departures +
                '}';
    }
}
