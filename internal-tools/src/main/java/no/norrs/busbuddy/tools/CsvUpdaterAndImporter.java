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
import no.norrs.busbuddy.api.dao.BusStopDAO;
import no.norrs.busbuddy.pub.api.model.BusStop;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * @author Roy Sindre Norangshol
 */
public class CsvUpdaterAndImporter {
    private BusStopDAO busstopDAO;
    private ApplicationContext context;

    public CsvUpdaterAndImporter() {
        context = new ClassPathXmlApplicationContext("classpath:Spring-Module.xml");
        busstopDAO = (BusStopDAO) context.getBean("busstopDAO");
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

    public static void main(String[] args) {
        new CsvUpdaterAndImporter();
    }
}
