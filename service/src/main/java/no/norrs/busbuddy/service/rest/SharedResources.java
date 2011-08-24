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

package no.norrs.busbuddy.service.rest;

import no.norrs.busbuddy.api.AtbControllerFactory;
import no.norrs.busbuddy.api.dao.BusBuddyApiKeyDAO;
import no.norrs.busbuddy.api.model.BusBuddyApiKey;
import no.norrs.busbuddy.service.CacheServiceLocator;
import no.norrs.busbuddy.service.DepartureCache;
import org.joda.time.LocalDateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.ws.rs.core.HttpHeaders;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Roy Sindre Norangshol
 */
public class SharedResources {
    // appName, appKey
    private static final SimpleDateFormat logDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    static final String BUSBUDDY_HTML_APP_APIKEY = "82NV49lmavKaljw2";
    ApplicationContext context;
    static List<BusBuddyApiKey> apiKeys;
    protected static Map<Integer, DepartureCache> departureCache = null;


    private BusBuddyApiKeyDAO apikeyDAO;
    protected AtbControllerFactory controllerFactory;

    public SharedResources() {
        controllerFactory = new AtbControllerFactory();
        context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        apikeyDAO = (BusBuddyApiKeyDAO) context.getBean("busbuddyapikeyDAO");
        apiKeys = apikeyDAO.findAll();

        departureCache = CacheServiceLocator.getInstance();

    }



    protected Timestamp getTimeStampForHitcounterLogging() {
        return getTimeStampForHitcounterLogging(new Date());
    }

    protected Timestamp getTimeStampForHitcounterLogging(Date date) {
        LocalDateTime dateFormat = new LocalDateTime(logDateFormatter.format(date));
        return new Timestamp(dateFormat.toDateTime().getMillis());
    }

    protected String getApiKeyFromHeader(HttpHeaders headers) {
        List<String> headerApiKey = headers.getRequestHeader("X-norrs-busbuddy-apikey");
        if (headerApiKey != null && headerApiKey.size() == 1)
            return headerApiKey.get(0);
        return null;
    }

    protected boolean apiKeyNotNull(String apikey) {
        return apikey != null;
    }

    protected boolean isValidKey(String apiKey) {
        return apiKeys.contains(new BusBuddyApiKey(apiKey, null));
    }

/*    protected String getAppNameByApiKey(String apiKey) {

        if (isValidKey(apiKey))
            return apiKeys.get(new BusBuddyApiKey(apiKey, null));
        return null;
    }*/

    protected String getApiKeyFromRequest(String apiKeyQueryParam, HttpHeaders headers) {
        String apiKey = null;
        if (apiKeyNotNull(apiKeyQueryParam))
            apiKey = apiKeyQueryParam;
        String headerApiKey = getApiKeyFromHeader(headers);
        if (headerApiKey != null)
            apiKey = headerApiKey;
        return apiKey;
    }

    protected boolean validateIfLocalRequest(HttpHeaders headers) {
        List<String> referrer = headers.getRequestHeader("Referer");
        if (referrer != null && referrer.size() > 0) {
            String reffie = referrer.get(0);
            if (reffie != null && (reffie.startsWith("http://busbuddy.norrs.no") || reffie.startsWith("http://busbuddy.no")))
                return true;
        }
        return false;
    }
}
