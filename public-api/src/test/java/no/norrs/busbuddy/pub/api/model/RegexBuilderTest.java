package no.norrs.busbuddy.pub.api.model;

import no.norrs.busbuddy.pub.api.model.answer.RegexBuilder;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Håvard Slettvold
 */


public class RegexBuilderTest {
    private String date = RegexBuilder.getDate();
    private String busRoute = RegexBuilder.getBusRoute();
    private String station = RegexBuilder.getStation();
    private String timeSuffix = RegexBuilder.getTimeSuffix();
    private String norwegianTime = RegexBuilder.getNorwegianTime();
    private String englishTime = RegexBuilder.getEnglishTime();
    private String timePart = RegexBuilder.getTimePart();
    private String time = RegexBuilder.getTime();
    private String duration = RegexBuilder.getDuration();

    private Pattern pattern;
    private Matcher matcher;

    String source1, source2, source3, source4, source5;

    @Test
    public void testDate() {
        pattern = Pattern.compile(date, Pattern.CASE_INSENSITIVE);
        source1 = "17. Des. 2011 er en lørdag.";
        source2 = "Dec. 24, 2011 is a Saturday.";

        try {
            matcher = pattern.matcher(source1);
            assertTrue(matcher.find());
            assertEquals("17", matcher.group(1));
            matcher = pattern.matcher(source2);
            assertTrue(matcher.find());
            assertEquals("24", matcher.group(1));
        } catch (AssertionError e) {
            System.err.println("Regex: "+pattern.toString());
            throw e;
        }
    }

    @Test
    public void testBusRoute() {
        pattern = Pattern.compile(busRoute, Pattern.CASE_INSENSITIVE);
        source1 = "Buss 5 passerer ";
        source2 = "Buss 16-199 går fra ";
        source3 = "Bus 6A goes from ";
        source4 = "Bus 5 passes by ";
        source5 = "Nattbuss 155 passerer ";

        try {
            matcher = pattern.matcher(source1);
            assertTrue(matcher.find());
            matcher = pattern.matcher(source2);
            assertTrue(matcher.find());
            matcher = pattern.matcher(source3);
            assertTrue(matcher.find());
            matcher = pattern.matcher(source4);
            assertTrue(matcher.find());
            matcher = pattern.matcher(source5);
            assertTrue(matcher.find());
        } catch (AssertionError e) {
            System.err.println("Regex: "+pattern.toString());
            throw e;
    }
    }

    @Test
    public void testStation() {
        pattern = Pattern.compile(station, Pattern.CASE_INSENSITIVE);
        source1 = "Gløshaugen Syd";
        source2 = "Prof. Brochs gate";
        source3 = "Ila, 5 minutter senere ";
        source4 = "Ila at 10.42 pm";
        source5 = "Ila kl. 1111 ";

        try {
            matcher = pattern.matcher(source1);
            assertTrue(matcher.find());
            matcher = pattern.matcher(source2);
            assertTrue(matcher.find());
            matcher = pattern.matcher(source3);
            assertTrue(matcher.find());
            assertEquals("Ila", matcher.group(1));
            matcher = pattern.matcher(source4);
            assertTrue(matcher.find());
            assertEquals("Ila", matcher.group(1));
            matcher = pattern.matcher(source5);
            assertTrue(matcher.find());
            assertEquals("Ila", matcher.group(1));

         } catch (AssertionError e) {
            System.err.println("Regex: "+pattern.toString());
            throw e;
        }
    }

    @Test
    public void testNorwegianTimePart() {
        pattern = Pattern.compile(timePart, Pattern.CASE_INSENSITIVE);
        source1 = "kl. 0903, ";
        source2 = "kl. 0913 og ";
        source3 = "kl. 0923 og kommer til ";
        source4 = "kl. 1135.";
        source5 = "kl. 1117 til ";

        try {
            matcher = pattern.matcher(source1);
            assertTrue(matcher.find());
            matcher = pattern.matcher(source2);
            assertTrue(matcher.find());
            matcher = pattern.matcher(source3);
            assertTrue(matcher.find());
            matcher = pattern.matcher(source4);
            assertTrue(matcher.find());
            matcher = pattern.matcher(source5);
            assertTrue(matcher.find());
        } catch (AssertionError e) {
            System.err.println("Regex: "+pattern.toString());
            throw e;
        }
    }

    @Test
    public void testEnglishTimePart() {
        pattern = Pattern.compile(timePart, Pattern.CASE_INSENSITIVE);
        source1 = "at 10.48 pm, ";
        source2 = "at 11.08 pm and ";
        source3 = "at 4.40 pm.";
        source4 = "at 11.28 pm and arrives at";

        try {
            matcher = pattern.matcher(source1);
            assertTrue(matcher.find());
            matcher = pattern.matcher(source2);
            assertTrue(matcher.find());
            matcher = pattern.matcher(source3);
            assertTrue(matcher.find());
            matcher = pattern.matcher(source4);
            assertTrue(matcher.find());
        } catch (AssertionError e) {
            System.err.println("Regex: "+pattern.toString());
            throw e;
        }
    }



    @Test
    public void testTime() {
        pattern = Pattern.compile(time, Pattern.CASE_INSENSITIVE);
        source1 = "Dronningens gate D3  kl. 0903, kl. 0913 og kl. 0923 og kommer til Gløshaugen Syd";
        source2 = "buss 9 går fra Torget kl. 1117 til Heimdal sentrum kl. 1135.";
        source3 = "Bus 5 passes by Gløshaugen Syd at 10.48 pm, at 11.08 pm and at 11.28 pm and arrives at Moholt, 5 minutes later.";

        try {
            matcher = pattern.matcher(source1);
            assertTrue(matcher.find());
            assertEquals("0903", matcher.group(1));
            assertEquals("0913", matcher.group(3));
            assertEquals("0923", matcher.group(5));
            matcher = pattern.matcher(source2);
            assertTrue(matcher.find());
            assertEquals("1117", matcher.group(1));
            assertEquals(null, matcher.group(5));
            matcher = pattern.matcher(source3);
            assertTrue(matcher.find());
            assertEquals("10.48 pm", matcher.group(2));
            assertEquals("11.08 pm", matcher.group(4));
            assertEquals("11.28 pm", matcher.group(6));
        } catch (AssertionError e) {
            System.err.println("Regex: "+pattern.toString());
            throw e;
        }
    }

    @Test
    public void testDuration() {
        pattern = Pattern.compile(duration, Pattern.CASE_INSENSITIVE);
        source1 = "Bus 5 passes by Gløshaugen Syd at 10.48 pm, at 11.08 pm and at 11.28 pm and arrives at Moholt, 5 minutes later.";

        try {
            matcher = pattern.matcher(source1);
            assertTrue(matcher.find());
            assertEquals("5", matcher.group(1));
        } catch (AssertionError e) {
            System.err.println("Regex: "+pattern.toString());
            throw e;
        }
    }

}
