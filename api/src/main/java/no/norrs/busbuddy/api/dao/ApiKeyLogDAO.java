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

package no.norrs.busbuddy.api.dao;

import no.norrs.busbuddy.api.model.ApiKeyLog;
import no.norrs.busbuddy.api.model.helper.HitsPerDay;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * @author Roy Sindre Norangshol
 */
public interface ApiKeyLogDAO {
    public void incrementHitcounterFor(ApiKeyLog log);

    public Map<Integer, Integer> getHitCountByApiKeyAndTimestamp(String apiKey, Timestamp timeStamp);

    public List<HitsPerDay> getHitCountByApiKeyFor30Days(String apiKey);

    public List<HitsPerDay> getSummedHitCountFor30Days();

    public Map<Integer, List<HitsPerDay>> getHitCountByApiKeyWithResultCodesLast30Days(String apiKey);

    public int getHitCountByApiKeyAndTimestampAndResultCode(String apiKey, Timestamp timeStamp, int resultCode);

}
