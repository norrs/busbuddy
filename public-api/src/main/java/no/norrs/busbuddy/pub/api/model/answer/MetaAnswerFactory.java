package no.norrs.busbuddy.pub.api.model.answer;

import no.norrs.busbuddy.pub.api.model.answer.MetaAnswer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Håvard Slettvold
 */


public class MetaAnswerFactory {

    private final static Pattern fromDestinationPattern = RegexBuilder.getOracleRegex();

    public static List<MetaAnswer> getMetaAnswers(String answer) {
        List<MetaAnswer> metaAnswers = new ArrayList<MetaAnswer>();
        Matcher matcher = fromDestinationPattern.matcher(answer.replaceAll("\\s+", " "));

        List<Integer> times;
        String match, start, destination;
        int routenum = -1, duration = -1;

        while (matcher.find()) {
            times = new ArrayList<Integer>();

            routenum = Integer.parseInt(matcher.group(1));
            start = matcher.group(2);
            destination = matcher.group(9);
            times = findTimes(matcher);

            /* Need to calc duration from end time.
             * This happens when there is only one start time.
             * Multiple end times is not possible.
             */
            if (matcher.group(12) == null) {
                duration = findDuration(matcher);
            }
            /* Duration was given as a number in the answer.
             * This only happens when there are multiple start times.
             */
            else {
                duration = Integer.parseInt(matcher.group(12));
            }

            /* Make a MetaAnswer and add to the list */
            metaAnswers.add(new MetaAnswer(start, destination, routenum, times, duration));
        }

        return metaAnswers;
    }

    private static List<Integer> findTimes(Matcher matcher) {
        List<Integer> times = new ArrayList<Integer>();
        String match;

        for (int i=3; i < 9; i++) {
            match = matcher.group(i);
            if (match != null) {
                /* Norwegian times are odd groups, needs no fixing. */
                if (i%2 == 1) {
                    times.add(Integer.parseInt(matcher.group(i)));
                }
                else {
                    times.add(fixTime(matcher.group(i)));
                }
            }
        }

        return times;
    }

    private static int fixTime(String time) {
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

    public static int findDuration(Matcher matcher) {
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
