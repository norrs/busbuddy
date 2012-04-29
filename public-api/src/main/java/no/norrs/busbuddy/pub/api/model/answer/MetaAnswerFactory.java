package no.norrs.busbuddy.pub.api.model.answer;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Håvard Slettvold
 */


public class MetaAnswerFactory {

    private final static Pattern oracleDateRegex = RegexBuilder.getDateRegex();
    private final static Pattern oracleSemanticsRegex = RegexBuilder.getOracleRegex();

    /**
     * The actual Factory method which produces MetaAnswers
     *
     * @param answer String obtained from {@link no.norrs.busbuddy.pub.api.model.Oracle}.
     * @return List of MetaAnswer objects made from parsing the answer supplied.
     */
    public static List<MetaAnswer> getMetaAnswers(String answer) {
        Matcher dateMatcher = oracleDateRegex.matcher(answer);
        DateTime basicDateTimeWithDate = new DateTime();
        if (dateMatcher.find()) {
            basicDateTimeWithDate = basicDateTimeWithDate.withDayOfMonth(Integer.parseInt(dateMatcher.group(1)));
        }

        List<MetaAnswer> metaAnswers = new ArrayList<MetaAnswer>();
        Matcher matcher = oracleSemanticsRegex.matcher(answer.replaceAll("\\s+", " "));

        List<DateTime> times;
        String match, start, destination, busRoute;
        int duration = -1;

        while (matcher.find()) {
            times = new ArrayList<DateTime>();

            busRoute = matcher.group(1);
            start = matcher.group(2);
            destination = matcher.group(9);
            times = findTimes(basicDateTimeWithDate, matcher);
            duration = findDuration(matcher);

            /* Make a MetaAnswer and add to the list */
            MetaAnswer ma = new MetaAnswer(start, destination, busRoute, times, duration);

            /* Check if the duration was inaccurate, and modify the MetaAnswer to reflect that */
            if (matcher.group(12) != null && matcher.group(12).indexOf('-') != -1) {
                ma.setInaccurate(matcher.group(12));
            }
            
            metaAnswers.add(ma);

            

        }

        return metaAnswers;
    }

    /**
     * Updates the DateTime with the specified date
     *
     * @param dateTime DateTime object to use for update
     * @param time The desired time specified in a 4 digit 24 hour format
     * @return DateTime updated with the specified time
     */
    private static DateTime getDateTimeForTime(DateTime dateTime, int time) {
        return dateTime.withTime((int)time/100, time%100, 0, 0);
    }

    /**
     * Finds departure times from the routes start, based on the matcher from oracleSemanticRegex
     *
     * @param basicDateTimeWithDate Premade DateTime that has had it's date adjusted.
     * @param matcher The matcher object from oracleSemanticRegex.
     * @return Departure times from route start.
     */
    public static List<DateTime> findTimes(DateTime basicDateTimeWithDate, Matcher matcher) {
        List<DateTime> times = new ArrayList<DateTime>();
        String match;

        for (int i=3; i < 9; i++) {
            match = matcher.group(i);
            if (match != null) {
                /* Norwegian times are odd groups, needs no fixing. */
                if (i%2 == 1) {
                    times.add(getDateTimeForTime(basicDateTimeWithDate, Integer.parseInt(matcher.group(i))));
                }
                else {
                    times.add(getDateTimeForTime(basicDateTimeWithDate, fixTime(matcher.group(i))));
                }
            }
        }

        return times;
    }

    /**
     * Converts english times like "2.34 pm" to "1434" or "10.45 am" to "1045".
     * Also casts to int.
     *
     * @param time String with the following format; HH.MM am/pm
     * @return int representation of the Sting time.
     */
    public static int fixTime(String time) {
        /*
         * Norwegian times are already in a 4 digit format; HHMM
         * English times are in the format;                 HH.MM am/pm
         *
         * This method converts english times to the same format as norwegian times
         */
        if (time.length() == 4) {
            return Integer.parseInt(time);
        }
        String tempTime = time.split(" ")[0];
        tempTime = tempTime.replaceAll("\\.", "");
        /* if the time is pm, you need to add 12 hours, or 1200, since it's in HHMM format */
        if (time.split(" ")[1].equals("pm")) {
            return Integer.parseInt(tempTime) + 1200;
        }
        else {
            return Integer.parseInt(tempTime);
        }
    }

    private static int findDuration(Matcher matcher) {
        /* Need to calc duration from end time.
         * This happens when there is only one start time.
         * Multiple end times is not possible as of november 2011.
         */
        if (matcher.group(12) == null) {
            return calculateDurationFromTimes(matcher);
        }
        /* Duration was given as a number in the answer.
         * This only happens when there are multiple start times.
         */
        else {
            /* Check if duration is a whole number.
             * If it isn't, it most likely contains "-", which makes it an estimate.
             */
            try {
                return Integer.parseInt(matcher.group(12));
            } catch (NumberFormatException nfe) {
                if (matcher.group(12).indexOf('-') != -1) {
                    String[] parts = matcher.group(12).split("-");
                    int min = Integer.parseInt(parts[0]);
                    int max = Integer.parseInt(parts[1]);

                    return (min + max) / 2;
                }
                else {
                    nfe.printStackTrace();
                }
            }
        }
        
        /* If all else fails, return -1. 
         * Should never happen unless duration contains something other than "\d+-\d+"  
         */
        return -1;
    }
    
    /**
     * Finds duration of a busRoute from start to destination when a time was given
     * instead of a duration in the answer.
     *
     * @param matcher The matcher object from oracleSemanticRegex.
     * @return int Time in minutes.
     */
    public static int calculateDurationFromTimes(Matcher matcher) {
        int duration = -1;
        int start, end;

        if (matcher.group(3) != null) {
            start = Integer.parseInt(matcher.group(3));
            end = Integer.parseInt(matcher.group(10));
        }
        else {
            start = fixTime(matcher.group(4));
            end = fixTime(matcher.group(11));
        }

        duration = (end%100) - (start%100) + ((int)(end/100) - (int)(start/100)) * 60;

        return duration;
    }

}
