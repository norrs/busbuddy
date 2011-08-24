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

package no.norrs.busbuddy.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import no.norrs.busbuddy.api.AtbWebController;
import no.norrs.busbuddy.api.atb.model.BusStopForecastContainer;
import no.norrs.busbuddy.api.atb.model.BusStopNodeInfo;
import no.norrs.busbuddy.api.dao.BusStopDAO;
import no.norrs.busbuddy.pub.api.CharSetAdapter;
import no.norrs.busbuddy.pub.api.HttpUtil;
import no.norrs.busbuddy.pub.api.InstantTypeConverter;
import no.norrs.busbuddy.pub.api.LocalDateTimeTypeConverter;
import no.norrs.busbuddy.pub.api.model.BusStop;
import no.norrs.busbuddy.pub.api.model.DepartureContainer;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author Roy Sindre Norangshol
 */
public class PullDeparturesAndUpdateBusStops {
    private int startBusStopId = 100003;
    private int stopBusStopId = 101700;
    //private int stopBusStopId = 100002;

    private long waitingTime = 3000;
    private Runnable workerTask = new Runnable() {
        @Override
        public void run() {
            while (startBusStopId <= stopBusStopId) {

                try {
                    System.out.println(String.format("[%s] Polling %s ..", getTimestamp(), startBusStopId));
                    fetchResourceAndUpdate(startBusStopId);
                    Thread.sleep(waitingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                startBusStopId++;
            }
        }
    };
    private Runnable workerSelectedBusStops = new Runnable() {
        @Override
        public void run() {
            final List<BusStop> busStops = busstopDAO.findAll();
            Collections.sort(busStops);
            for (BusStop busStop : busStops) {


                if (busStop.getLatitude() == 0.0 || busStop.getLongitude() == 0.0) {
                    //atbService.getUserRealTimeForecast(busStop.getBusStopId());

                    System.out.println(String.format("[%s] Polling %s", getTimestamp(), busStop.getBusStopId()));
                    fetchResourceAndUpdate(busStop.getBusStopId());

                    try {
                        Thread.sleep(waitingTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }


                } else {
                    System.out.println(String.format("[%s] Not needed %s ..", getTimestamp(), busStop.getBusStopId()));
                }
            }
        }
    };

    private AtbWebController atbService;
    private Gson gson;
    private BusStopDAO busstopDAO;
    private ApplicationContext context;

    private String getTimestamp() {
        return new LocalDateTime().toString("HH:mm:ss");
    }

    private void fetchResourceAndUpdate(int busStopId) {
        DepartureContainer container = null;
        BusStopForecastContainer originalContainer = null;
        LocalDateTime now = new LocalDateTime();
        originalContainer = atbService.getUserRealTimeForecast(busStopId);
        container = originalContainer.convertToApi();
        boolean skip = true;


        if (originalContainer.busStopNodeInfos != null && originalContainer.busStopNodeInfos.size() > 0) {
            if (originalContainer.busStopNodeInfos.get(0).nodeId != null)
                skip = false;
        }

        if (!skip) {
            BusStopNodeInfo atbBusStopNodeInfo = originalContainer.busStopNodeInfos.get(0);

            BusStop busStopFromDB = busstopDAO.findBusStopById(busStopId);

            String nodeDescription = atbBusStopNodeInfo.getFixedNodeDescriptionOrFallBackToNormalNodeDescription();
            BusStop updateOrInsertIfNeeded = new BusStop(
                    busStopId,
                    nodeDescription,
                    nodeDescription,
                    atbBusStopNodeInfo.name,
                    String.valueOf(atbBusStopNodeInfo.nodeId),
                    atbBusStopNodeInfo.longitude,
                    atbBusStopNodeInfo.latitude

            );

            if (busStopFromDB != null) {
                updateOrInsertIfNeeded.setName(busStopFromDB.getName()); // uses converted fixed name if manually fixed in db.
            }

            busstopDAO.insertOrUpdate(updateOrInsertIfNeeded);
            System.out.println(String.format("[%s] Done/fetched %s ..", getTimestamp(), startBusStopId));
        } else {
            System.out.println(String.format("[%s] Skipped %s ..", getTimestamp(), startBusStopId));
        }


    }

    public PullDeparturesAndUpdateBusStops() {

        context = new ClassPathXmlApplicationContext("classpath:Spring-Module.xml");
        Properties atbWebProperties = new Properties();
        try {
            atbWebProperties.load(getClass().getResourceAsStream("/atbweb.properties"));
            atbService = new AtbWebController(atbWebProperties.getProperty("endpoint"), atbWebProperties.getProperty("payload"));
            GsonBuilder builder = new GsonBuilder();
            gson = builder.serializeNulls()
                    .registerTypeAdapter(String.class, new CharSetAdapter())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeConverter())
                    .registerTypeAdapter(Instant.class, new InstantTypeConverter())
                    .create();


            busstopDAO = (BusStopDAO) context.getBean("busstopDAO");
            /*Thread worker = new Thread(workerTask);
            worker.start();*/
            Thread worker = new Thread(workerSelectedBusStops);
            worker.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        new PullDeparturesAndUpdateBusStops();
    }
}
