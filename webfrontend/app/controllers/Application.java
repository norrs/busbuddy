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

public class Application extends Controller {
    private static String apiHost;

    static {

    }

    public static void index() throws IOException {
        String usage = loadAPIUsageData();
        render(usage);
    }

    private static String loadAPIUsageData() throws IOException {
        apiHost = (String) Play.configuration.get("apiHost");
        if (apiHost == null)
            return "/** Missing apiHost in Play Configuration file */";

        InputStream input = null;
        try {
            input = new URL(apiHost + "/api/1.3/stats/total/lastdays/30").openStream();
            return String.format("var last30DaysStats = %s", HttpUtil.readString(input));
        } finally {
            if (input != null)
                input.close();

        }

    }
}