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

package no.norrs.busbuddy.api.model;

/**
 * Roy Sindre Norangshol
 * Date: 8/22/11
 * Time: 12:05 AM
 */
public class Trip {
    private int tripId;
    private String line;
    private String lineName;

    public Trip() {}

    public Trip(int tripId, String line, String lineName) {
        this.tripId = tripId;
        this.line = line;
        this.lineName = lineName;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "tripId=" + tripId +
                ", line='" + line + '\'' +
                ", lineName='" + lineName + '\'' +
                '}';
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }
}
