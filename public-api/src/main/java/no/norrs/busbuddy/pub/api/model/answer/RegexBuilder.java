package no.norrs.busbuddy.pub.api.model.answer;

import java.util.regex.Pattern;

/**
 * @author Håvard Slettvold
 */


public class RegexBuilder {

    /* Regex for fetching date */
    private static String date = "(?:(?:\\w+\\. )?(\\d{1,2})[.,](?: \\w+\\.)?) \\d{4} (?:er en|is a) [\\w\\p{InLATIN_1_SUPPLEMENT}]+(?:dag|day)\\.";

    /* Regexes used for semantics of travel */
    private static String busRoute = "(?:natt)?buss? ([\\d\\w-]+) (?:passerer|går fra|passes by|goes from) ";
    private static String station = "((?:[.\\w\\s\\p{InLATIN_1_SUPPLEMENT}](?!(?:kl\\.|at) ))+) ?"; //
    private static String timeSuffix = "(?:[,. ]| og(?: kommer)?| and) ?";
    private static String norwegianTime = "kl\\. (\\d{4})";
    private static String englishTime = "at (\\d{1,2}\\.\\d{2} (?:a|p)m)";
    private static String timePart = "(?:"+norwegianTime+"|"+englishTime+")"+timeSuffix;
    private static String time = timePart+"(?:(?:"+timePart+")?"+timePart+")?(?:til|to|arrives at) ";
    private static String duration = ", (\\d+(?:-\\d+)?) (?:minutter senere|minutes later)\\.";

    public static Pattern getOracleRegex() {
        return Pattern.compile(
                busRoute + station + time + station + "(?:" + timePart + "|" + duration + ")",
                Pattern.CASE_INSENSITIVE
                                );
    }

    public static Pattern getDateRegex() {
        return Pattern.compile(date, Pattern.CASE_INSENSITIVE);
    }

    /*
     * These methods are meant to be used only for testing purposes.
     * Alone, many of these regexes will not work properly on full oracle answers.
     */
    public static String getDate() {
        return date;
    }

    public static String getBusRoute() {
        return busRoute;
    }

    public static String getStation() {
        return station;
    }

    public static String getTimePart() {
        return timePart;
    }

    public static String getTime() {
        return time;
    }

    public static String getTimeSuffix() {
        return timeSuffix;
    }

    public static String getNorwegianTime() {
        return norwegianTime;
    }

    public static String getEnglishTime() {
        return englishTime;
    }

    public static String getDuration() {
        return duration;
    }
}
