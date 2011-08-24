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

import java.util.regex.Pattern;

/**
 * @author Roy Sindre Norangshol
 */
public class Departure {
    private static final String FORMAT_NODE_DESCRIPTION = "\\d{0,4} \\((.*)\\)";
    private static final Pattern patternNodeDescription = Pattern.compile(FORMAT_NODE_DESCRIPTION);

    private Integer tripId;

    private String line;
    private String destination;
    private String operator;
    private LocalDateTime registeredDepartureTime; // sanntidsverdi
    private LocalDateTime scheduledDepartureTime; // rutetabell
    private boolean isRealtimeData;

    public Departure() {

    }

    @Override
    public String toString() {
        return "Departure{" +
                "tripId=" + tripId +
                ", line='" + line + '\'' +
                ", destination='" + destination + '\'' +
                ", operator='" + operator + '\'' +
                ", registeredDepartureTime=" + registeredDepartureTime +
                ", scheduledDepartureTime=" + scheduledDepartureTime +
                ", isRealtimeData=" + isRealtimeData +
                '}';
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
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

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getRegisteredDepartureTime() {
        return registeredDepartureTime;
    }

    public void setRegisteredDepartureTime(LocalDateTime registeredDepartureTime) {
        this.registeredDepartureTime = registeredDepartureTime;
    }

    public LocalDateTime getScheduledDepartureTime() {
        return scheduledDepartureTime;
    }

    public void setScheduledDepartureTime(LocalDateTime scheduledDepartureTime) {
        this.scheduledDepartureTime = scheduledDepartureTime;
    }

    public boolean isRealtimeData() {
        return isRealtimeData;
    }

    public void setRealtimeData(boolean realtimeData) {
        isRealtimeData = realtimeData;
    }
}
