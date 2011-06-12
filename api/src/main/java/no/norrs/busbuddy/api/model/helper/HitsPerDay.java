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

package no.norrs.busbuddy.api.model.helper;

import org.joda.time.Instant;

import java.sql.Timestamp;

/**
 * @author Roy Sindre Norangshol
 */
public class HitsPerDay {
    private Instant timeStamp;
    private int hitsData;
    private Integer resultCode;

    public HitsPerDay(int hitsData, int resultCode) {
        this.hitsData = hitsData;
        this.resultCode = resultCode;
    }

    public HitsPerDay(Timestamp timeStamp, int hitsData) {
        this.timeStamp = new Instant(timeStamp);
        this.hitsData = hitsData;
    }

    public HitsPerDay(Instant timeStamp, int hitsData, int resultCode) {
        this.timeStamp = timeStamp;
        this.hitsData = hitsData;
        this.resultCode = resultCode;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Instant timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getHitsData() {
        return hitsData;
    }

    public void setHitsData(int hitsData) {
        this.hitsData = hitsData;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String toString() {
        return "HitsPerDay{" +
                "timeStamp=" + timeStamp +
                ", hitsData=" + hitsData +
                ", resultCode=" + resultCode +
                '}';
    }
}
