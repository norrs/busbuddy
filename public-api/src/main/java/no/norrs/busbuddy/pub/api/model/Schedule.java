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
 * Roy Sindre Norangshol
 * Date: 8/20/11
 * Time: 4:09 PM
 */
public class Schedule {
    private String line;
    private String destination;
    private List<Departure> departures;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Schedule schedule = (Schedule) o;

        if (destination != null ? !destination.equals(schedule.destination) : schedule.destination != null)
            return false;
        if (line != null ? !line.equals(schedule.line) : schedule.line != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = line != null ? line.hashCode() : 0;
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        return result;
    }

    public int compareTo(Schedule s) {
        int lastCmp = line.compareTo(s.line);
        return (lastCmp != 0 ? lastCmp :
                destination.compareTo(s.destination));
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "line='" + line + '\'' +
                ", destination='" + destination + '\'' +
                ", departures=" + departures +
                '}';
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

    public List<Departure> getDepartures() {
        return departures;
    }

    public void setDepartures(List<Departure> departures) {
        this.departures = departures;
    }
}

