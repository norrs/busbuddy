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

/**
 * @author Roy Sindre Norangshol
 */
public class BusStop implements Comparable<BusStop> {
    private Integer busStopId;
    private String name;
    private String nameWithAbbreviations;
    private String busStopMaintainer;

    private String locationId;

    private float longitude;
    private float latitude;

    public BusStop() {
    }

    public BusStop(Integer busStopId, String name, String nameWithAbbreviations, String busStopMaintainer, String locationId, float longitude, float latitude) {
        this.busStopId = busStopId;
        this.name = name;
        this.nameWithAbbreviations = nameWithAbbreviations;
        this.busStopMaintainer = busStopMaintainer;
        this.locationId = locationId;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getBusStopId() {
        return busStopId;
    }

    public void setBusStopId(Integer busStopId) {
        this.busStopId = busStopId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameWithAbbreviations() {
        return nameWithAbbreviations;
    }

    public void setNameWithAbbreviations(String nameWithAbbreviations) {
        this.nameWithAbbreviations = nameWithAbbreviations;
    }

    public String getBusStopMaintainer() {
        return busStopMaintainer;
    }

    public void setBusStopMaintainer(String busStopMaintainer) {
        this.busStopMaintainer = busStopMaintainer;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public int getLatitudeE6() {
        return (int) (getLatitude() * 1E6);
    }

    public int getLongitudeE6() {
        return (int) (getLongitude() * 1E6);
    }

    public boolean isGoingTowardsCentrum() {
        return locationId.charAt(4) == '1' ? true : false;
    }

    public int compareTo(BusStop n) {
        return locationId.compareTo(n.locationId);
    }

    @Override
    public String toString() {
        return "BusStop{" +
                "busStopId=" + busStopId +
                ", name='" + name + '\'' +
                ", nameWithAbbreviations='" + nameWithAbbreviations + '\'' +
                ", busStopMaintainer='" + busStopMaintainer + '\'' +
                ", locationId='" + locationId + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
