package no.norrs.busbuddy.pub.api.model;

import no.norrs.busbuddy.pub.api.model.answer.MetaAnswer;
import no.norrs.busbuddy.pub.api.model.answer.MetaAnswerFactory;
import no.norrs.busbuddy.pub.api.model.answer.RegexBuilder;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author Håvard Slettvold
 */


public class MetaAnswerFactoryTest {

    @Test
    public void testSimpleNorwegianAnswer() {
        String answer = "Buss 5 går fra Ila kl. 1055 til Dragvoll kl. 1114.";
        List<MetaAnswer> metaAnswers = MetaAnswerFactory.getMetaAnswers(answer);
        MetaAnswer testMetaAnswer;
        MetaAnswer ma;

        assertEquals(1, metaAnswers.size());

        testMetaAnswer = new MetaAnswer("Ila","Dragvoll","5",Arrays.asList(new DateTime[]{new DateTime().withTime(10, 55, 0, 0)}),19);
        ma = metaAnswers.get(0);
        assertEquals(testMetaAnswer.toString(), ma.toString());
    }

    @Test
    public void testComplicatedNorwegianAnswer() {
        String answer = "Holdeplassen nærmest Gløshaugen er Gløshaugen Syd. " +
                "Buss 5 passerer  Dronningens gate D3 kl. 1617  og  kommer til Gløshaugen Syd, 8 minutter senere."+
                "Buss 52 passerer  Munkegata M3 kl. 1625  og  kommer til  Gløshaugen Syd,  7 minutter senere. " +
                "Buss 52 passerer  Torget kl. 1626  og  kommer til  Gløshaugen Syd,  6 minutter senere.  " +
                "Tidene angir tidligste passeringer av holdeplassene.";
        List<MetaAnswer> metaAnswers = MetaAnswerFactory.getMetaAnswers(answer);
        MetaAnswer testMetaAnswer;
        MetaAnswer ma;

        assertEquals(3, metaAnswers.size());

        testMetaAnswer = new MetaAnswer("Dronningens gate D3","Gløshaugen Syd","5",Arrays.asList(new DateTime[]{new DateTime().withTime(16, 17, 0, 0)}),8);
        ma = metaAnswers.get(0);
        assertEquals(testMetaAnswer.toString(), ma.toString());

        testMetaAnswer = new MetaAnswer("Munkegata M3","Gløshaugen Syd","52",Arrays.asList(new DateTime[]{new DateTime().withTime(16, 25, 0, 0)}),7);
        ma = metaAnswers.get(1);
        assertEquals(testMetaAnswer.toString(), ma.toString());

        testMetaAnswer = new MetaAnswer("Torget","Gløshaugen Syd","52",Arrays.asList(new DateTime[]{new DateTime().withTime(16, 26, 0, 0)}),6);
        ma = metaAnswers.get(2);
        assertEquals(testMetaAnswer.toString(), ma.toString());
    }

    @Test
    public void testAdvancedNorwegianAnswer() {
        String answer = "Buss 5 passerer Glxshaugen Nord kl. 0931 og kl. 1001 og kommer til Sentrumsterminalen, 5-8 minutter senere. " +
                "Buss 52 passerer Gløshaugen Nord kl. 1010 og kommer til Munkegata M3, 6 minutter senere. " +
                "Tidene angir tidligste passeringer av holdeplassene.";
        List<MetaAnswer> metaAnswers = MetaAnswerFactory.getMetaAnswers(answer);
        MetaAnswer testMetaAnswer;
        MetaAnswer ma;

        assertEquals(2, metaAnswers.size());

        testMetaAnswer = new MetaAnswer("Glxshaugen Nord", "Sentrumsterminalen", "5", Arrays.asList(new DateTime[]{new DateTime().withTime(9,31,0,0),new DateTime().withTime(10,01,0,0)}), 6);
        testMetaAnswer.setInaccurate("5-8");
        ma = metaAnswers.get(0);
        assertEquals(testMetaAnswer.toString(), ma.toString());

        testMetaAnswer = new MetaAnswer("Gløshaugen Nord", "Munkegata M3", "52", Arrays.asList(new DateTime[]{new DateTime().withTime(10,10,0,0)}), 6);
        ma = metaAnswers.get(1);
        assertEquals(testMetaAnswer.toString(), ma.toString());
        assertFalse(testMetaAnswer.isInaccurate());

    }

    @Test
    public void testSimpleEnglishAnswer() {
        String answer = "Bus 7 goes from Reppe at 3.43 pm to Strandveien at 4.07 pm.";
        List<MetaAnswer> metaAnswers = MetaAnswerFactory.getMetaAnswers(answer);
        MetaAnswer testMetaAnswer;
        MetaAnswer ma;

        assertEquals(1, metaAnswers.size());

        testMetaAnswer = new MetaAnswer("Reppe","Strandveien","7",Arrays.asList(new DateTime[]{new DateTime().withTime(15, 43, 0, 0)}),24);
        ma = metaAnswers.get(0);
        assertEquals(testMetaAnswer.toString(), ma.toString());

    }

    @Test
    public void testComplicatedEnglishAnswer() {
        String answer = "Bus 8 passes by Studentersamfundet at 10.04 am and at 10.19 am and arrives at Nardokrysset, 6 minutes later. " +
                "Bus 52 passes by Studentersamfundet at 10.29 am and arrives at Nardokrysset, 5 minutes later. " +
                "The hours indicate the earliest passing times.";
        List<MetaAnswer> metaAnswers = MetaAnswerFactory.getMetaAnswers(answer);
        MetaAnswer testMetaAnswer;
        MetaAnswer ma;

        assertEquals(2, metaAnswers.size());

        testMetaAnswer = new MetaAnswer("Studentersamfundet","Nardokrysset","8",Arrays.asList(new DateTime[]{new DateTime().withTime(10, 04, 0, 0), new DateTime().withTime(10, 19, 0, 0)}),6);
        ma = metaAnswers.get(0);
        assertEquals(testMetaAnswer.toString(), ma.toString());

        testMetaAnswer = new MetaAnswer("Studentersamfundet","Nardokrysset","52",Arrays.asList(new DateTime[]{new DateTime().withTime(10, 29, 0, 0)}),5);
        ma = metaAnswers.get(1);
        assertEquals(testMetaAnswer.toString(), ma.toString());

    }

    @Test
    public void testNattbussAndDateAnswer() {
        String answer = "17. Des. 2011 er en lørdag. For denne dato gjelder AtB Vinterruter. " +
                "Nattbuss 155 passerer Dronningens gate D3 kl. 0102 og kl. 0202 og kommer til Moholt, 10 minutter senere. " +
                "Nattbuss 155 passerer Torget kl. 0103 og kommer til Moholt, 9 minutter senere. " +
                "Tidene angir tidligste passeringer av holdeplassene.";
        List<MetaAnswer> metaAnswers = MetaAnswerFactory.getMetaAnswers(answer);
        MetaAnswer testMetaAnswer;
        MetaAnswer ma;

        assertEquals(2, metaAnswers.size());

        DateTime basicDateTime = new DateTime().withDayOfMonth(17);

        testMetaAnswer = new MetaAnswer("Dronningens gate D3","Moholt","155",Arrays.asList(new DateTime[]{basicDateTime.withTime(01, 02, 0, 0), basicDateTime.withTime(02, 02, 0, 0)}),10);
        ma = metaAnswers.get(0);
        assertEquals(testMetaAnswer.toString(), ma.toString());

        testMetaAnswer = new MetaAnswer("Torget","Moholt","155",Arrays.asList(new DateTime[]{basicDateTime.withTime(01, 03, 0, 0)}),9);
        ma = metaAnswers.get(1);
        assertEquals(testMetaAnswer.toString(), ma.toString());
    }

    @Test
    public void testFixTime() {
        String time1 = "2.34 pm";
        String time2 = "10.45 am";

        assertEquals(1434, MetaAnswerFactory.fixTime(time1));
        assertEquals(1045, MetaAnswerFactory.fixTime(time2));
    }

    @Test
    public void testCalculateDurationFromTimes() {
        String answer = "Buss 5 går fra Ila kl. 1055 til Dragvoll kl. 1114.";
        Matcher matcher = RegexBuilder.getOracleRegex().matcher(answer);

        assertTrue(matcher.find());

        int duration = MetaAnswerFactory.calculateDurationFromTimes(matcher);

        assertEquals(19, duration);
    }

    @Test
    public void testFindTimes() {
        String answer = "Bus 5 passes by Gløshaugen Syd at 10.48 pm, at 11.08 pm and at 11.28 pm and arrives at Moholt, 5 minutes later.";
        Matcher matcher = RegexBuilder.getOracleRegex().matcher(answer);

        assertTrue(matcher.find());

        DateTime dt = new DateTime();
        List<DateTime> times = MetaAnswerFactory.findTimes(dt, matcher);

        assertEquals(Arrays.asList(new DateTime[]{dt.withTime(22, 48, 0, 0), dt.withTime(23, 8, 0, 0), dt.withTime(23, 28, 0, 0)}).toString(), times.toString());
    }
}
