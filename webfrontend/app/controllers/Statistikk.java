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

package controllers;

import no.norrs.busbuddy.pub.api.HttpUtil;
import play.Play;
import play.mvc.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Roy Sindre Norangshol
 */
public class Statistikk extends Controller {

    public static void index() throws IOException {
        String last30DaysStats = loadAPIUsageData("/api/stats/total/lastdays/30", "last30DaysStats");
        String last30DaysStatsForEachApp = loadAPIUsageData("/api/stats/total/apikeys/lastdays/30", "last30DaysStatsForEachApp");
        String appDetailsName = "var appDetailsName = 'Busbuddy (HTML5)';";
        String last30DaysStatsForAppDetails = loadAPIUsageData("/api/stats/82NV49lmavKaljw2/lastdays/30", "last30DaysStatsForAppDetails");
        render(last30DaysStats,last30DaysStatsForEachApp, appDetailsName, last30DaysStatsForAppDetails);
    }

    private static String loadAPIUsageData(String apiStatsResource, String callBack) throws IOException {
        String apiHost = (String) Play.configuration.get("apiHost");
        InputStream input = null;
        try {
            input = new URL(String.format("%s%s",apiHost, apiStatsResource)).openStream();
            return String.format("var %s = %s", callBack, HttpUtil.readString(input));
        } finally {
            if (input != null)
                input.close();

        }
    }


}