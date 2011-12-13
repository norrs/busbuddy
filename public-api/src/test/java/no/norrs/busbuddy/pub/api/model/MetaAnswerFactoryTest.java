package no.norrs.busbuddy.pub.api.model;

import no.norrs.busbuddy.pub.api.model.answer.MetaAnswer;
import no.norrs.busbuddy.pub.api.model.answer.MetaAnswerFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

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

        testMetaAnswer = new MetaAnswer("Ila","Dragvoll",5,Arrays.asList(new Integer[]{1055}),19);
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

        testMetaAnswer = new MetaAnswer("Dronningens gate D3","Gløshaugen Syd",5,Arrays.asList(new Integer[]{1617}),8);
        ma = metaAnswers.get(0);
        assertEquals(testMetaAnswer.toString(), ma.toString());

        testMetaAnswer = new MetaAnswer("Munkegata M3","Gløshaugen Syd",52,Arrays.asList(new Integer[]{1625}),7);
        ma = metaAnswers.get(1);
        assertEquals(testMetaAnswer.toString(), ma.toString());

        testMetaAnswer = new MetaAnswer("Torget","Gløshaugen Syd",52,Arrays.asList(new Integer[]{1626}),6);
        ma = metaAnswers.get(2);
        assertEquals(testMetaAnswer.toString(), ma.toString());
    }

    @Test
    public void testSimpleEnglishAnswer() {
        String answer = "Bus 7 goes from Reppe at 3.43 pm to Strandveien at 4.07 pm.";
        List<MetaAnswer> metaAnswers = MetaAnswerFactory.getMetaAnswers(answer);
        MetaAnswer testMetaAnswer;
        MetaAnswer ma;

        assertEquals(1, metaAnswers.size());

        testMetaAnswer = new MetaAnswer("Reppe","Strandveien",7,Arrays.asList(new Integer[]{1543}),24);
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

        testMetaAnswer = new MetaAnswer("Studentersamfundet","Nardokrysset",8,Arrays.asList(new Integer[]{1004, 1019}),6);
        ma = metaAnswers.get(0);
        assertEquals(testMetaAnswer.toString(), ma.toString());

        testMetaAnswer = new MetaAnswer("Studentersamfundet","Nardokrysset",52,Arrays.asList(new Integer[]{1029}),5);
        ma = metaAnswers.get(1);
        assertEquals(testMetaAnswer.toString(), ma.toString());

    }
}
