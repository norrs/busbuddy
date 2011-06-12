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

package no.norrs.busbuddy.service;

import no.norrs.busbuddy.api.atb.model.BusStopForecastContainer;
import no.norrs.busbuddy.pub.api.model.DepartureContainer;
import org.joda.time.LocalDateTime;

/**
 * @author Roy Sindre Norangshol
 */
public class DepartureCache {
    private LocalDateTime requstTimestamp;
    private DepartureContainer departureContainer;
    private BusStopForecastContainer originalContainerFromAtb;

    public DepartureCache(LocalDateTime requstTimestamp, DepartureContainer departureContainer, BusStopForecastContainer originalContainerFromAtb) {
        this.requstTimestamp = requstTimestamp;
        this.departureContainer = departureContainer;
        this.originalContainerFromAtb = originalContainerFromAtb;
    }

    public DepartureCache(LocalDateTime requstTimestamp, DepartureContainer departureContainer) {
        this.requstTimestamp = requstTimestamp;
        this.departureContainer = departureContainer;
    }

    public LocalDateTime getRequstTimestamp() {
        return requstTimestamp;
    }

    public void setRequstTimestamp(LocalDateTime requstTimestamp) {
        this.requstTimestamp = requstTimestamp;
    }

    public DepartureContainer getDepartureContainer() {
        return departureContainer;
    }

    public void setDepartureContainer(DepartureContainer departureContainer) {
        this.departureContainer = departureContainer;
    }

    public BusStopForecastContainer getOriginalContainerFromAtb() {
        return originalContainerFromAtb;
    }

    public void setOriginalContainerFromAtb(BusStopForecastContainer originalContainerFromAtb) {
        this.originalContainerFromAtb = originalContainerFromAtb;
    }

    @Override
    public String toString() {
        return "DepartureCache{" +
                "requstTimestamp=" + requstTimestamp +
                ", departureContainer=" + departureContainer +
                ", originalContainerFromAtb=" + originalContainerFromAtb +
                '}';
    }
}
