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

import no.norrs.busbuddy.api.dao.BusStopDAO;
import no.norrs.busbuddy.api.dao.TripsDAO;
import no.norrs.busbuddy.api.model.Trip;
import no.norrs.busbuddy.pub.api.HttpUtil;
import no.norrs.busbuddy.pub.api.model.*;
import org.apache.http.HttpResponse;
import org.joda.time.LocalDateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Roy Sindre Norangshol
 * Date: 8/20/11
 * Time: 5:57 PM
 */
@Component
public class AtbRpControllerImpl implements AtbRpController {
    //private static final String endpointTrip="http://rp.atb.no/scripts/TravelMagic/TravelMagicWE.dll/turinfo?trip='%s'&date='.$tripDate;"
    private static final String endpointTrip = "http://rp.atb.no/scripts/TravelMagic/TravelMagicWE.dll/turinfo?trip=%s";
    private AtbWebController atbService;
    private BusStopDAO busstopDAO;
    private ClassPathXmlApplicationContext context;
    private TripsDAO tripsDAO;

    @Autowired
    public AtbRpControllerImpl(BusStopDAO busStopDAO, TripsDAO tripsDAO) throws IOException {
/*
        Properties atbWebProperties = new Properties();
        atbWebProperties.load(getClass().getResourceAsStream("/atbweb.properties"));
        atbService = new AtbWebController(atbWebProperties.getProperty("endpoint"), atbWebProperties.getProperty("payload"));
*/
        //context = new ClassPathXmlApplicationContext("classpath:Spring-Module.xml");
        //busstopDAO = (BusStopDAO) context.getBean("busstopDAO");
        //tripsDAO = (TripsDAO) context.getBean("tripsDAO");
        this.busstopDAO = busStopDAO;
        this.tripsDAO = tripsDAO;
    }

    @Override
    public StopsContainer getBusStopsFor(int tripId) throws IOException {
        HttpResponse response = HttpUtil.GET(String.format(endpointTrip, tripId));
        String data = HttpUtil.readString(response.getEntity().getContent());

        org.jsoup.nodes.Document document = Jsoup.parse(data);

        Elements title = document.select("title");
        Elements table = document.select("table");

        Pattern locationIdPattern = Pattern.compile(".*hpl=(.*)&.*");
        Pattern dateStampPattern = Pattern.compile(".*date=(\\d{2})\\.(\\d{2})\\.(\\d{4})");
        Pattern municipalityPattern = Pattern.compile("(.*) \\((.*)\\)");

        ListIterator<Element> fromBusStop = (table.select("tr > td:eq(0)").listIterator());
        ListIterator<Element> timestamps = (table.select("tr > td:eq(1)").listIterator());
        ListIterator<Element> links = table.select("tr > td > a[href*=avgangsinfo]").listIterator();

        StopsContainer stopsContainer = new StopsContainer();


        Pattern pattern1 = Pattern.compile("Rute (.*) til (.*) -.*");
        Matcher m = pattern1.matcher(title.text());
        if (m.matches()) {
            stopsContainer.setLine(m.group(1).trim());
            stopsContainer.setDestination(m.group(2).trim());

        }

        ArrayList<Stops> stops = new ArrayList<Stops>();

        /* int busstopcounter=0,timestampcounter=0,linkscounter  = 0;
       while (fromBusStop.hasNext()) {
           System.out.println(fromBusStop.next());
           busstopcounter++;
       }
       while (timestamps.hasNext()) {
           System.out.println(timestamps.next());
           timestampcounter++;
       }
       while (links.hasNext()) {
           System.out.println(links.next());
           linkscounter++;
       }
       System.out.println(String.format("busstop %s, timestamps %s, links %s", busstopcounter, timestampcounter, linkscounter));

        */
        while (fromBusStop.hasNext() && timestamps.hasNext() && links.hasNext()) {
            Element from = fromBusStop.next();
            Element time = timestamps.next();
            Element link = links.next();

            String locationId = null;
            LocalDateTime dateStamp = null;

            Matcher locationIdMatcher = locationIdPattern.matcher(link.attr("href"));
            if (locationIdMatcher.matches()) {
                locationId = locationIdMatcher.group(1);
            }

            Matcher dateStampMatcher = dateStampPattern.matcher(link.attr("href"));
            if (dateStampMatcher.matches()) {
                dateStamp = new LocalDateTime(Integer.parseInt(dateStampMatcher.group(3)), Integer.parseInt(dateStampMatcher.group(2)), Integer.parseInt(dateStampMatcher.group(1)), 0, 0);
            }

            String[] hourAndMinute = time.text().split(":");
            if (dateStamp != null) {
                Matcher municipalityMatcher = municipalityPattern.matcher(from.text().trim());
                String originalRpFromName = null;
                String municipality = "N/A";
                if (municipalityMatcher.matches()) {
                    originalRpFromName = municipalityMatcher.group(1).trim();
                    municipality = municipalityMatcher.group(2).trim();
                } else {
                    originalRpFromName = from.text().trim();
                }

                BusStop dbBusStop = null;
                if (locationId != null)
                    dbBusStop = busstopDAO.findBusStopByLocationId(Integer.parseInt(locationId));

                if (dbBusStop != null)
                    originalRpFromName = dbBusStop.getName();
                stops.add(new Stops(new LocalDateTime(dateStamp.getYear(), dateStamp.getMonthOfYear(), dateStamp.getDayOfMonth(), Integer.parseInt(hourAndMinute[0]), Integer.parseInt(hourAndMinute[1])), originalRpFromName, municipality, locationId));
            }
        }
        stopsContainer.setStops(stops);

        Trip betterTripName = tripsDAO.findTripsByTripIdAndLine(tripId, stopsContainer.getLine());
        if (betterTripName != null)
            stopsContainer.setLineName(betterTripName.getLineName());
        else
            stopsContainer.setLineName("");

        return stopsContainer;
    }

    @Override
    public ScheduleContainer getSchedulesForecast(int locationId) throws IOException {
        String rpSchedulesEndpoint = "http://rp.atb.no/scripts/TravelMagic/TravelMagicWE.dll/avgangsinfo?hpl=%s&time='%s'&date='%s'";

        LocalDateTime now = new LocalDateTime();
        //System.out.println(String.format(rpSchedulesEndpoint, locationId, now.toString("HH:mm"), now.toString("dd.MM.Y")));
        HttpResponse response = HttpUtil.GET(String.format(rpSchedulesEndpoint, locationId, now.toString("HH:mm"), now.toString("dd.MM.Y")));
        String data = HttpUtil.readString(response.getEntity().getContent());


        Document schedulesDocument = Jsoup.parse(data);
        //Document schedulesDocument = Jsoup.parse(data);


        Pattern destinationInSchedule = Pattern.compile("^Avganger fra (.*)[\\(<].*");

        String destination = null;

        //System.out.println("orignal : " + schedulesDocument.select("h3 > a").text());    /*
        Matcher destinationMatch = destinationInSchedule.matcher(schedulesDocument.select("h3 > a").text());
        if (destinationMatch.matches())
            destination = destinationMatch.group(1).trim();
        BusStop busStop = busstopDAO.findBusStopByLocationId(locationId);
        if (busStop != null)
            destination = busStop.getName();

        if (destination == null)
            return null;

        Elements table = schedulesDocument.select("table");

        ListIterator<Element> timeStamps = (table.select("tr > td:eq(0)").listIterator());
        ListIterator<Element> lines = (table.select("tr > td:eq(1)").listIterator());
        ListIterator<Element> destinations = (table.select("tr > td:eq(3)").listIterator());
        ListIterator<Element> operators = (table.select("tr > td:eq(5)").listIterator());
        ListIterator<Element> links = (table.select("tr > td > a[href*=turinfo]")).listIterator();

        // http://rp.atb.no/scripts/TravelMagic/TravelMagisoucWE.dll/turinfo?trip=8063&starthpl=16010001&date=21.08.2011

        Pattern scheduleInfo = Pattern.compile(".*trip=(\\d{0,4})&starthpl=(\\d+)&date=(\\d{2})\\.(\\d{2})\\.(\\d{4})");

        ScheduleContainer container = new ScheduleContainer();
        container.setBusStopName(destination);
        container.setSchedules(new ArrayList<Schedule>());


        while (timeStamps.hasNext() && lines.hasNext() && destinations.hasNext() && operators.hasNext() && links.hasNext()) {
            Element timeStamp = timeStamps.next();
            Element line = lines.next();
            Element sdestination = destinations.next();
            Element operator = operators.next();
            Element link = links.next();
            int tripId = 0;
            int year = 1970;
            int month = 1;
            int day = 1;

            Matcher matchTripAndLocationId = scheduleInfo.matcher(link.attr("href"));

            if (matchTripAndLocationId.matches()) {
                tripId = Integer.parseInt(matchTripAndLocationId.group(1));
                //locationId = Integer.parseInt(matchTripAndLocationId.group(2));
                year = Integer.parseInt(matchTripAndLocationId.group(3));
                month = Integer.parseInt(matchTripAndLocationId.group(4));
                day = Integer.parseInt(matchTripAndLocationId.group(5));
            }


            String[] hoursAndMinutes = timeStamp.text().split(":");

            LocalDateTime entryLocalDateTime = new LocalDateTime();
            entryLocalDateTime = entryLocalDateTime.withDate(day, month, year);
            entryLocalDateTime = entryLocalDateTime.withTime(Integer.parseInt(hoursAndMinutes[0]), Integer.parseInt(hoursAndMinutes[1]), 0, 0);

            Departure departure = new Departure();
            //departure.setLine(line.text());
            //departure.setDestination(sdestination.text());
            departure.setRealtimeData(false);
            departure.setTripId(tripId);
            departure.setOperator(operator.text().trim());
            departure.setScheduledDepartureTime(entryLocalDateTime);


            Schedule schedule = new Schedule();
            schedule.setDestination(sdestination.text().trim());
            schedule.setLine(line.text().trim());
            schedule.setDepartures(new ArrayList<Departure>());
            if (container.getSchedules().contains(schedule))
                schedule = container.getSchedules().get(
                        container.getSchedules().indexOf(schedule)
                );
            else
                container.getSchedules().add(schedule);

            schedule.getDepartures().add(departure);


            //System.out.println(String.format("%s - %s - %s - %s - %s - %s", entryLocalDateTime.toString(), line.text(), sdestination.text(), operator.text(), tripId, locationId));

        }
        //Collections.sort(container.getSchedules());
        return container;

    }
}
