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

import au.com.bytecode.opencsv.CSVReader;
import no.norrs.busbuddy.api.AtbWebController;
import no.norrs.busbuddy.api.dao.BusStopDAO;
import no.norrs.busbuddy.api.dao.TripsDAO;
import no.norrs.busbuddy.api.model.Trip;
import no.norrs.busbuddy.pub.api.HttpUtil;
import no.norrs.busbuddy.pub.api.model.BusStop;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roy Sindre Norangshol
 */
public class CsvUpdaterAndImporter {
    private BusStopDAO busstopDAO;
    private ApplicationContext context;
    private TripsDAO tripsDAO;
    private AtbWebController atbService;

    public CsvUpdaterAndImporter() throws IOException {
        context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        busstopDAO = (BusStopDAO) context.getBean("busstopDAO");
        tripsDAO = (TripsDAO) context.getBean("tripsDAO");

        Properties atbWebProperties = new Properties();
        atbWebProperties.load(getClass().getResourceAsStream("/atbweb.properties"));
        atbService = new AtbWebController(atbWebProperties.getProperty("endpoint"), atbWebProperties.getProperty("payload"));

    }

    public void importBusStops() {
        CSVReader reader = null;
        try {

            reader = new CSVReader(new FileReader("/home/rockj/tmp/busstops.csv"));
            List<String[]> myEntries = reader.readAll();
            // busstopid, fullname, locationid, shortname
            for (String[] busStop : myEntries) {

                BusStop originalDb = busstopDAO.findBusStopById(Integer.parseInt(busStop[0]));
                BusStop update = originalDb;
                if (update != null && update.getBusStopId() == Integer.parseInt(busStop[0])) {
                    update.setName(busStop[1]);
                    if (!busStop[3].equalsIgnoreCase("")) {
                        update.setNameWithAbbreviations(busStop[3]);
                    }
                    busstopDAO.update(update);
                }


            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public void updateBusStopIdsFromNewDataset() {
        CSVReader reader = null;
        try {

            reader = new CSVReader(new FileReader("/home/rockj/tmp/stops29aug2011.txt"), '\t');
            Pattern name = Pattern.compile("(.*) (\\(\\d{0,4}\\))");

            int updatedCounter = 0;
            int newCounter = 0;
            ArrayList<Integer> busStopsToPullForUpdatingCoordinates = new ArrayList<Integer>();

            List<String[]> myEntries = reader.readAll();
            // busstopid, fullname, locationid, shortname
            for (String[] bus : myEntries) {
                Matcher match = name.matcher(bus[2]);
                if (match.matches()) {
                    String stopName = match.group(1).trim();
                    //System.out.println(stopName);

                    BusStop busStop = null;
                    busStop = busstopDAO.findBusStopByLocationId(Integer.parseInt(bus[1].trim()));

                    if (busStop != null) {
                        busStop.setBusStopId(Integer.parseInt(bus[0]));
                        busstopDAO.update(busStop);
                        System.out.println(String.format("UPDATED  id %s , loc %s   ==> %s ", bus[0], bus[1], bus[2]));
                        updatedCounter++;
                    } else {
                        busStop = new BusStop();
                        busStop.setBusStopId(Integer.parseInt(bus[0]));
                        busStop.setBusStopMaintainer(null);
                        busStop.setLatitude(0.0f);
                        busStop.setLongitude(0.0f);
                        busStop.setLocationId(bus[1].trim());
                        busStop.setName(stopName);
                        busStop.setNameWithAbbreviations(null);
                        busStopsToPullForUpdatingCoordinates.add(busStop.getBusStopId());
                        System.out.println(String.format("NEW ENTRY  id %s , loc %s   ==> %s ", bus[0], bus[1], busStop.toString()));
                        busstopDAO.insertOrUpdate(busStop);
                        newCounter++;
                    }

                } else {
                    System.out.println(String.format("NOT FOUND  id %s , loc %s   ==> %s ", bus[0], bus[1], bus[2]));
                }
            }
            System.out.println(String.format(" First phase done , updating %s records for coordinates etc", busStopsToPullForUpdatingCoordinates.size()));
            final ArrayList<Integer> busLoop = busStopsToPullForUpdatingCoordinates;
            final int finalUpdatedCounter = updatedCounter;
            final int finalNewCounter = newCounter;
            System.out.println(String.format("updatedCounter = %s, newCounter = %s", finalUpdatedCounter, finalNewCounter));
            Runnable thread = new Runnable() {
                @Override
                public void run() {
                    for (Integer busStopId : busLoop) {
                        atbService.getUserRealTimeForecast(busStopId);
                        System.out.println(String.format("Polled %s", busStopId));
                        try {
                            Thread.sleep(7000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }

                }
            };
            thread.run();


        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public void importTripsAndNames() {
        CSVReader reader = null;
        try {

            reader = new CSVReader(new FileReader("/home/rockj/tmp/gistfile1.txt"), '\t');

            List<String[]> myEntries = reader.readAll();
            // busstopid, fullname, locationid, shortname
            for (String[] trip : myEntries) {
                System.out.println(String.format("Trip %s , line %s   ==> %s ", trip[0], trip[1], trip[2]));
                Trip originalDb = tripsDAO.findTripsByTripIdAndLine(Integer.parseInt(trip[0]), trip[1]);
                Trip update = originalDb;
                if (update != null && update.getTripId() == Integer.parseInt(trip[0]) && update.getLine().equals(trip[1])) {
                    update.setLineName(trip[1]);
                    tripsDAO.update(update);
                } else if (update == null) {
                    update = new Trip(
                            Integer.parseInt(trip[0]), trip[1], trip[2]
                    );
                    tripsDAO.insertOrUpdate(update);

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void main(String[] args) {
        CsvUpdaterAndImporter csv = null;
        try {
            csv = new CsvUpdaterAndImporter();
            csv.updateBusStopIdsFromNewDataset();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private void tmpDeleteDuplicates() {
        CSVReader reader = null;
        try {

            reader = new CSVReader(new FileReader("/home/rockj/tmp/norrs-sql.txt"), '\t');
            Pattern name = Pattern.compile(".*\\{(.*)\\}");

            ArrayList<Integer> busStopsToPullForUpdatingCoordinates = new ArrayList<Integer>();

            List<String[]> myEntries = reader.readAll();
            // busstopid, fullname, locationid, shortname
            for (String[] bus : myEntries) {
                Matcher matcher = name.matcher(bus[0]);
                if (matcher.matches()) {
                    String[] ids = matcher.group(1).split(",");

                    busstopDAO.delete(Integer.parseInt(ids[1]));
                    System.out.println("delete " + ids[1]);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void updateBusstosWithMissingData() {

        final List<BusStop> busStops = busstopDAO.findAll();
        Collections.sort(busStops);
        Runnable thread = new Runnable() {
            @Override
            public void run() {
                for (BusStop busStop : busStops) {

                    String maintainer = null;
                    maintainer = busStop.getBusStopMaintainer();
                    if (maintainer == null || busStop.getLatitude() == 0.0 || busStop.getLongitude() == 0.0) {
                        //atbService.getUserRealTimeForecast(busStop.getBusStopId());
                        try {
                            HttpUtil.GET("http://luna.home.norrs.no:8080/busbuddy/1.3/departures/" + busStop.getBusStopId() + "?apiKey=troll");
                            System.out.println(String.format("Polled %s", busStop.getBusStopId()));
                            try {
                                Thread.sleep(7000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }

                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }

                    }
                }
            }
        };
        thread.run();
    }
}
