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

package no.norrs.busbuddy.api;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.miz.infotransit.UserServices;
import it.miz.infotransit.UserServicesSoap;
import it.miz.infotransit.WsAuthentication;
import no.norrs.busbuddy.api.atb.model.BusListsContainer;
import no.norrs.busbuddy.api.atb.model.BusStopForecastContainer;
import no.norrs.busbuddy.pub.api.CharSetAdapter;
import no.norrs.busbuddy.pub.api.model.Schedule;
import no.norrs.busbuddy.pub.api.model.StopsContainer;

import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;

/**
 * @author Roy Sindre Norangshol
 */
public class AtbSoapController implements AtbController {
    WsAuthentication authentication = new WsAuthentication();
    private UserServicesSoap soap = null;
    private Gson gson;

    public AtbSoapController(String adminUsername, String adminPassword) throws MalformedURLException {
        authentication = new WsAuthentication();
        authentication.setUser(adminUsername);
        authentication.setPassword(adminPassword);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder
                .serializeNulls()
                .registerTypeAdapter(String.class, new CharSetAdapter())
                .create();


        //UserServices service = new UserServices(getClass().getResource("wsdl/atb.wsdl"));
        //UserServices service = new UserServices(getClass().getResource("wsdl/atb.wsdl"));
        UserServices service = new UserServices();

        soap  = service.getUserServicesSoap();
        //soap = service.getUserServicesSoap12();
        BindingProvider bindingProvider = (BindingProvider) soap;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://st.atb.no/InfoTransit/userservices.asmx?wsdl");

    }

    @Override
    public BusStopForecastContainer getUserRealTimeForecast(int busStopId) {
        return getUserRealTimeForecastFromJson(soap.getUserRealTimeForecast(authentication, null, String.valueOf(busStopId)));
    }

    @Override
    public BusListsContainer getAllBusStopsListInTrondheim() {
        return getBusStopsFromJson(soap.getBusStopsList(authentication));
    }

    @Override
    public StopsContainer getBusStopsFor(int tripId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Schedule getSchedulesForecast(int locationId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    private BusStopForecastContainer getUserRealTimeForecastFromJson(String json) {
        return gson.fromJson(json, BusStopForecastContainer.class);
    }

    private BusListsContainer getBusStopsFromJson(String json) {
        return gson.fromJson(json, BusListsContainer.class);
    }

}
