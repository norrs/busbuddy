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

import org.joda.time.LocalDateTime;

import java.io.BufferedReader;
import java.io.File;

/**
 * Roy Sindre Norangshol
 * Date: 8/20/11
 * Time: 10:58 PM
 */
public class Foobar {
    private static BufferedReader reader;
    private static String data;
    private static StringBuffer contents;
    private static File f;

    public static void main(String[] args) {
       /* try {

            f = new File("/tmp/test.html");
            reader = new BufferedReader(new FileReader(f));
            contents = new StringBuffer();
            data = null;
            while ((data = reader.readLine()) != null) {
                contents.append(data).append(System.getProperty("line.separator"));

            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        data = contents.toString();
*/
          LocalDateTime now = new LocalDateTime();
          now = now.withTime(13,8,0,0);
            System.out.println(String.format("%s %s", now.toString("HH:mm"), now.toString("dd.m.Y")));
     /*   try {
            AtbRpController controller = new AtbRpControllerImpl();
            //System.out.println(controller.getBusStopsFor(6356));
            System.out.println(controller.getSchedulesForecast(16011455));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
      */

    }
}