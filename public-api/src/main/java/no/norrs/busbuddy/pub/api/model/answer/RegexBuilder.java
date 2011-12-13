package no.norrs.busbuddy.pub.api.model.answer;

import java.util.regex.Pattern;

/**
 * @author Håvard Slettvold
 */


public class RegexBuilder {

    /* Regexes used for semantic searching */
    private static String startingPhrase = "Buss? (\\d+) (?:passerer|går fra|passes by|goes from) ";
    private static String station = "((?:[.\\w\\s\\p{InLATIN_1_SUPPLEMENT}](?!(?:kl\\.|at) ))+) ?"; //
    private static String timeSuffix = "(?:[,. ]| og(?: kommer)?| and) ?";
    private static String norwegianTime = "kl\\. (\\d{4})";
    private static String englishTime = "at (\\d{1,2}\\.\\d{2} (?:a|p)m)";
    private static String timePart = "(?:"+norwegianTime+"|"+englishTime+")"+timeSuffix;
    private static String time = timePart+"(?:(?:"+timePart+")?"+timePart+")?(?:til|to|arrives at) ";
    private static String duration = ", (\\d+) (?:minutter senere|minutes later)\\.";

    public static Pattern getOracleRegex() {
        return Pattern.compile(
                startingPhrase + station + time + station + "(?:" + timePart + "|" + duration + ")",
                Pattern.CASE_INSENSITIVE
                                );
    }

    public static String getStartingPhrase() {
        return startingPhrase;
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
