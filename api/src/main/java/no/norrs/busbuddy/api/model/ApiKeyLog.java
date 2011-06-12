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

import java.sql.Timestamp;

/**
 * @author Roy Sindre Norangshol
 */
public class ApiKeyLog {
    int logid;
    String apiKey;
    Timestamp timeStamp;
    int hitCounter;
    int resultCode;

    public ApiKeyLog() {}

    public ApiKeyLog(String apiKey, Timestamp timeStamp) {
        this.apiKey = apiKey;
        this.timeStamp = timeStamp;
    }

    public ApiKeyLog(String apiKey, Timestamp timeStamp, int resultCode) {
        this.apiKey = apiKey;
        this.timeStamp = timeStamp;
        this.resultCode = resultCode;
    }



    public int getLogid() {
        return logid;
    }

    public void setLogid(int logid) {
        this.logid = logid;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }


    public int getHitCounter() {
        return hitCounter;
    }

    public void setHitCounter(int hitCounter) {
        this.hitCounter = hitCounter;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
}
