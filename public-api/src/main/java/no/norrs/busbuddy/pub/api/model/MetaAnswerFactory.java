package no.norrs.busbuddy.pub.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Håvard Slettvold
 */


public class MetaAnswerFactory {

    // originalen
    //private final static Pattern fromDestinationPattern = Pattern.compile("(?:Holdeplassen nærmest (?:\\w.+?) er|Buss? \\d+ (?:passerer|går fra|goes from)) (\\w.+?)(?:\\.| (?:kl|at))");
    private final static Pattern fromDestinationPattern = Pattern.compile(
            "buss? (\\d+) (?:passerer|går fra|goes from) (\\w.+?) (?:kl\\. \\d{4}.*?|at \\d{1,2}\\.\\d{2} (?:a|p)m.*?){1,3} (?:to|(?:og kommer )?til) (\\w.+?)(?:,| kl\\.| at)",
            Pattern.CASE_INSENSITIVE
    );

    /*
    Buss 5 går fra Ila kl. 1055 til Dronningens gate D3 kl. 1100 og buss 9 går fra Torget kl. 1117 til Heimdal sentrum kl. 1135.
    Buss 5 passerer Glxshaugen Nord kl. 0931 og kl. 1001 og kommer til Sentrumsterminalen, 5-8 minutter senere. Buss 52 passerer Gløshaugen Nord kl. 1010 og kommer til Munkegata M3, 6 minutter senere.
    Bus 7 goes from Reppe at 3.43 pm to Strandveien at 4.07 pm and bus 4 goes from Strandveien at 4.25 pm to Lade allé 80 at 4.40 pm.
      */

    public static List<MetaAnswer> getMetaAnswers(String answer) {
        List<MetaAnswer> ma = new ArrayList<MetaAnswer>();
        Matcher matcher = fromDestinationPattern.matcher(answer.replaceAll("\\s+", " "));

        while (matcher.find()) {
            ma.add(new MetaAnswer(matcher.group(2), matcher.group(3), Integer.parseInt(matcher.group(1))));
        }

        return ma;
    }

}
