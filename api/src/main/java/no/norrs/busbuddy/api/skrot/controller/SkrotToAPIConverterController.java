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

package no.norrs.busbuddy.api.skrot.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.norrs.busbuddy.api.skrot.model.BusStopContainerSkrot;
import no.norrs.busbuddy.pub.api.CharSetAdapter;
import no.norrs.busbuddy.pub.api.HttpUtil;

import java.io.IOException;

/**
 * @author Roy Sindre Norangshol
 */
public class SkrotToAPIConverterController {

    private BusStopContainerSkrot data;
    private Gson gson;

    public SkrotToAPIConverterController() {
        GsonBuilder gsonBuilder = new GsonBuilder();


        gson = gsonBuilder
                .serializeNulls()
                .registerTypeAdapter(String.class, new CharSetAdapter())
                .create();

        data = getSkrotToAPI();
    }


    public BusStopContainerSkrot getData() {
        return data;
    }


    private BusStopContainerSkrot getSkrotToAPI() {
        try {
            return gson.fromJson(HttpUtil.readString(HttpUtil.GET("http://dl.dropbox.com/u/6140800/atb_current.txt").getEntity().getContent()), BusStopContainerSkrot.class);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }


}
